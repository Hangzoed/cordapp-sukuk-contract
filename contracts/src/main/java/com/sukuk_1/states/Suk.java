package com.sukuk_1.states;

import com.sukuk_1.contracts.SukVoucherContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@BelongsToContract(SukVoucherContract.class)
public class Suk implements LinearState {

    //Private Variables
    private String VoucherDesc; //For example: "One stamp can exchange for a basket of HoneyCrispy Apple"
    private Party issuer; //The person who issued the stamp
    private Party holder; //The person who currently owns the stamp

    //LinearState required variable.
    private UniqueIdentifier linearID;

    //ALL Corda State required parameter to indicate storing parties
    private List<AbstractParty> participants;

    //Constructor Tips: Command + N in IntelliJ can auto generate constructor.
    @ConstructorForDeserialization
    public Suk(String VoucherDesc, Party issuer, Party holder, UniqueIdentifier linearID) {
        this.VoucherDesc = VoucherDesc;
        this.issuer = issuer;
        this.holder = holder;
        this.linearID = linearID;
        this.participants = new ArrayList<AbstractParty>();
        this.participants.add(issuer);
        this.participants.add(holder);
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return this.participants;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearID;
    }

    //Getters
    public String getVoucherDesc() {
        return VoucherDesc;
    }

    public Party getIssuer() {
        return issuer;
    }

    public Party getHolder() {
        return holder;
    }

}

//Advanced sukuk_1 add brand and type of apple for more complicated contract writing
