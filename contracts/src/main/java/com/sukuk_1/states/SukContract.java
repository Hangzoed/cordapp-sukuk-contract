package com.sukuk_1.states;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@BelongsToContract(com.sukuk_1.contracts.SukContract.class)
public class SukContract implements ContractState {

    //Private Variables
    private String description; //Brand or type
    private Party Issuer; //Origin of the apple
    private Party owner; //The person who exchange the basket of apple with the stamp.
    private int Value;

    //ALL Corda State required parameter to indicate storing parties
    private List<AbstractParty> participants;

    //Constructors
    //Basket of Apple creation. Only Issuer name is stored.
    public SukContract(String description, Party Issuer, int Value) {
        this.description = description;
        this.Issuer = Issuer;
        this.owner=Issuer;
        this.Value = Value;
        this.participants = new ArrayList<AbstractParty>();
        this.participants.add(Issuer);
    }

    //Constructor for object creation during transaction
    @ConstructorForDeserialization
    public SukContract(String description, Party Issuer, Party owner, int Value) {
        this.description = description;
        this.Issuer = Issuer;
        this.owner = owner;
        this.Value = Value;
        this.participants = new ArrayList<AbstractParty>();
        this.participants.add(Issuer);
        this.participants.add(owner);
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return participants;
    }

    //getters
    public String getDescription() {
        return description;
    }

    public Party getIssuer() {
        return Issuer;
    }

    public Party getOwner() {
        return owner;
    }

    public int getValue() {
        return Value;
    }

    public SukContract changeOwner(Party buyer){
        SukContract newOwnerState = new SukContract(this.description,this.Issuer,buyer,this.Value);
        return newOwnerState;
    }

}

//Advance version will fill in brand and type.