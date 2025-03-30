package com.example.eduvote;

public class PartyListMainModel {

    String partyName;
    String partyKey;

    public PartyListMainModel(){}

    public PartyListMainModel(String partyName, String partyKey) {
        this.partyName = partyName;
        this.partyKey = partyKey;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getPartyKey() {
        return partyKey;
    }

    public void setPartyKey(String partyKey) {
        this.partyKey = partyKey;
    }
}
