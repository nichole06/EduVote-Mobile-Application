package com.example.eduvote;

import java.util.List;

public class Position {

    private String name;
    private List<String> candidates;

    public Position(String name, List<String> candidates) {
        this.name = name;
        this.candidates = candidates;
    }

    public Position(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getCandidates() {
        return candidates;
    }
}
