package com.sukuk_1;

import com.google.common.collect.ImmutableList;
import com.sukuk.flows.IssueSuk;
import com.sukuk_1.states.SukContract;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

public class FarmerSelfCreateSukContractTest {
    private MockNetwork network;
    private StartedMockNode a;
    private StartedMockNode b;

    @Before
    public void setup() {
        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(ImmutableList.of(
                        TestCordapp.findCordapp("com.sukuk_1.contracts"),
                        TestCordapp.findCordapp("com.sukuk_1.flows")))
                .withNotarySpecs(ImmutableList.of(new MockNetworkNotarySpec(CordaX500Name.parse("O=Notary,L=London,C=GB")))));
        a = network.createPartyNode(null);
        b = network.createPartyNode(null);
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Test
    public void createBasketOfApples() {
        IssueSuk.IssueSukInitiator flow1 = new IssueSuk.IssueSukInitiator("Fuji4072", 10);
        Future<SignedTransaction> future = a.startFlow(flow1);
        network.runNetwork();

        //successful query means the state is stored at node b's vault. Flow went through.
        QueryCriteria inputCriteria = new QueryCriteria.VaultQueryCriteria().withStatus(Vault.StateStatus.UNCONSUMED);
        SukContract state = a.getServices().getVaultService()
                .queryBy(SukContract.class, inputCriteria).getStates().get(0).getState().getData();

        System.out.println("-------------------------");
        System.out.println(state.getOwner());
        System.out.println("-------------------------");

        assert (state.getDescription().equals("Fuji4072"));
    }
}

