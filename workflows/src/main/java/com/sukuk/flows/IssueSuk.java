package com.sukuk.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.sukuk_1.states.SukContract;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Collections;

public class IssueSuk {

    @InitiatingFlow
    @StartableByRPC
    public static class IssueSukInitiator extends FlowLogic<SignedTransaction> {

        private String SukDescription;
        private int Value;

        public IssueSukInitiator(String SukDescription, int Value) {
            this.SukDescription = SukDescription;
            this.Value = Value;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            // Obtain a reference to a notary we wish to use.
            /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config (Preferred)*/
            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));

            //Create the output object
            SukContract basket = new SukContract(this.SukDescription, this.getOurIdentity(), this.Value);

            //Building transaction
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(basket)
                    .addCommand(new com.sukuk_1.contracts.SukContract.Commands.Create(), this.getOurIdentity().getOwningKey());

            // Verify the transaction
            txBuilder.verify(getServiceHub());

            // Sign the transaction
            SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(txBuilder);

            // Notarise the transaction and record the states in the ledger.
            return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
        }
    }
}

//flow start IssueSukInitiator appleDescription: Fuji4072, weight: 10
//run vaultQuery contractStateType: com.sukuk_1.states.SukContract