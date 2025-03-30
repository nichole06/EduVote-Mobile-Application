package com.example.eduvote;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class PartyList {

    // fpr add partylist
    private String partyName;
    private Map<String, String> positions;

    // for add candidate

    public PartyList(){}

    public PartyList(String partyName) {
        this.partyName = partyName;
        this.positions = new HashMap<>();
        this.positions.put("governor", "");
        this.positions.put("vicegovernor", "");
        this.positions.put("secretary", "");
        this.positions.put("treasurer", "");
        this.positions.put("budget", "");
        this.positions.put("auditor", "");
        this.positions.put("pio", "");
        this.positions.put("fourthyearrepresentative", "");
        this.positions.put("thirdyearrep", "");
        this.positions.put("secondyearrep", "");

        Log.d("PartyList", "Positions initialized: " + positions);
    }

    public PartyList(String candidateName, String position) {

        this.positions = new HashMap<>();
        this.positions.put(position, candidateName);
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public Map<String, String> getPositions() {
        return positions;
    }

    public void setPositions(Map<String, String> positions) {
        this.positions = positions;
    }
}
