package com.sukuk_1.contracts;

import com.sukuk_1.states.Suk;
import com.sukuk_1.states.TemplateState;
import net.corda.core.identity.Party;
import org.junit.Test;

public class StateTests {

    //Mock State test check for if the state has correct parameters type
    @Test
    public void hasFieldOfCorrectType() throws NoSuchFieldException {
        TemplateState.class.getDeclaredField("msg");
        assert (TemplateState.class.getDeclaredField("msg").getType().equals(String.class));
    }

    @Test
    public void AppleStampStateHasFieldOfCorrectType() throws NoSuchFieldException {
        Suk.class.getDeclaredField("stampDesc");
        assert (Suk.class.getDeclaredField("stampDesc").getType().equals(String.class));

        Suk.class.getDeclaredField("issuer");
        assert (Suk.class.getDeclaredField("issuer").getType().equals(Party.class));

        Suk.class.getDeclaredField("holder");
        assert (Suk.class.getDeclaredField("issuer").getType().equals(Party.class));
    }
}