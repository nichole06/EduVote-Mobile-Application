package com.example.eduvote;

public class Candidate {

    private String candidateName;
    private long voteCount;
    private String id;
    private String position;
    private String candidatePartylist;

    public Candidate() {}

    public Candidate(String candidateName) {
        this.candidateName = candidateName;
        this.voteCount = 0;
    }

    public Candidate(String candidateName, long voteCount) {
        this.candidateName = candidateName;
        this.voteCount = voteCount;
    }

//    public Candidate(String id, String candidateName, String position) {
//        this.id = id;
//        this.candidateName = candidateName;
//        this.position = position;
//    }

    public Candidate(String candidateName, long voteCount, String candidatePartylist) {
        this.voteCount = voteCount;
        this.candidateName = candidateName;
        this.candidatePartylist = candidatePartylist;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public String getCandidatePartylist() {return candidatePartylist;}
}
