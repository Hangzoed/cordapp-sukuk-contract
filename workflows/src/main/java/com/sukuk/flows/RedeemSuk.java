package com.sukuk.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.sukuk_1.states.SukContract;
import com.sukuk_1.states.Suk;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;
import java.util.UUID;

public class RedeemSuk {

    @InitiatingFlow
    @StartableByRPC
    public static class RedeemSukInitiator extends FlowLogic<SignedTransaction> {

        private Party buyer;
        private UniqueIdentifier stampId;

        public RedeemSukInitiator(Party buyer, UniqueIdentifier stampId) {
            this.buyer = buyer;
            this.stampId = stampId;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            //Query the Suk
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withUuid(Arrays.asList(UUID.fromString(stampId.toString())))
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);
            StateAndRef appleStampStateAndRef = getServiceHub().getVaultService().queryBy(Suk.class, inputCriteria).getStates().get(0);

            //Query output SukContract
            QueryCriteria.VaultQueryCriteria outputCriteria = new QueryCriteria.VaultQueryCriteria()
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);
            StateAndRef BasketOfApplesStateAndRef = getServiceHub().getVaultService().queryBy(SukContract.class, outputCriteria).getStates().get(0);
            SukContract originalSukContract = (SukContract) BasketOfApplesStateAndRef.getState().getData();

            //Modify output to address the owner change
            SukContract output = originalSukContract.changeOwner(buyer);

            /* Obtain a reference to a notary we wish to use.*/
            Party notary = BasketOfApplesStateAndRef.getState().getNotary();

            //Build Transaction
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addInputState(appleStampStateAndRef)
                    .addInputState(BasketOfApplesStateAndRef)
                    .addOutputState(output, com.sukuk_1.contracts.SukContract.ID)
                    .addCommand(new com.sukuk_1.contracts.SukContract.Commands.Redeem(),
                            Arrays.asList(getOurIdentity().getOwningKey(), this.buyer.getOwningKey()));

            // Verify that the transaction is valid.
            txBuilder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            // Send the state to the counterparty, and receive it back with their signature.
            FlowSession otherPartySession = initiateFlow(buyer);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, Arrays.asList(otherPartySession)));

            // Notarise and record the transaction in both parties' vaults.
            SignedTransaction result = subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));

            return result;
        }
    }

    @InitiatedBy(RedeemSukInitiator.class)
    public static class RedeemSukResponder extends FlowLogic<Void> {
        //private variable
        private FlowSession counterpartySession;

        public RedeemSukResponder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Override
        @Suspendable
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                }
            });

            //Stored the transaction into data base.
            subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
            return null;
        }
    }
}
//flow start RedeemSukInitiator buyer: Peter, stampId: