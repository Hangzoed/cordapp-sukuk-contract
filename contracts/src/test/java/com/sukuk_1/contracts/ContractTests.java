package com.sukuk_1.contracts;

import com.sukuk_1.states.Suk;
import com.sukuk_1.states.TemplateState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Arrays;

import static net.corda.testing.node.NodeTestUtils.ledger;

public class ContractTests {
    private final MockServices ledgerServices = new MockServices(Arrays.asList("com.sukuk_1.contracts"));
    TestIdentity alice = new TestIdentity(new CordaX500Name("Alice", "TestLand", "US"));
    TestIdentity bob = new TestIdentity(new CordaX500Name("Alice", "TestLand", "US"));

    //Template Tester
    @Test
    public void issuerAndRecipientCannotHaveSameEmail() {
        TemplateState state = new TemplateState("Hello-World", alice.getParty(), bob.getParty());
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.input(TemplateContract.ID, state);
                tx.output(TemplateContract.ID, state);
                tx.command(alice.getPublicKey(), new TemplateContract.Commands.Send());
                return tx.fails(); //fails because of having inputs
            });
            l.transaction(tx -> {
                tx.output(TemplateContract.ID, state);
                tx.command(alice.getPublicKey(), new TemplateContract.Commands.Send());
                return tx.verifies();
            });
            return null;
        });
    }

    //Basket of Apple cordapp testers
    @Test
    public void StampIssuanceCanOnlyHaveOneOutput() {
        Suk stamp = new Suk("FUji4072", alice.getParty(), bob.getParty(), new UniqueIdentifier());
        Suk stamp2 = new Suk("HoneyCrispy7864", alice.getParty(), bob.getParty(), new UniqueIdentifier());

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.output(SukVoucherContract.ID, stamp);
                tx.output(SukVoucherContract.ID, stamp2);
                tx.command(alice.getPublicKey(), new SukVoucherContract.Commands.Issue());
                return tx.fails(); //fails because of having inputs
            });
            l.transaction(tx -> {
                tx.output(SukVoucherContract.ID, stamp);
                tx.command(alice.getPublicKey(), new SukVoucherContract.Commands.Issue());
                return tx.verifies();
            });
            return null;
        });
    }

    @Test
    public void StampMustHaveDescription() {
        Suk stamp = new Suk("", alice.getParty(), bob.getParty(), new UniqueIdentifier());
        Suk stamp2 = new Suk("FUji4072", alice.getParty(), bob.getParty(), new UniqueIdentifier());

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.output(SukVoucherContract.ID, stamp);
                tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new SukVoucherContract.Commands.Issue());
                return tx.fails(); //fails because of having inputs
            });
            l.transaction(tx -> {
                tx.output(SukVoucherContract.ID, stamp2);
                tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new SukVoucherContract.Commands.Issue());
                return tx.verifies();
            });
            return null;
        });
    }
}