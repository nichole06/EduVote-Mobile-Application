package com.example.eduvote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOcandidate {
    private DatabaseReference databaseReference;

    public DAOcandidate() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("PartyList");
    }

    public Task<Void> add(Candidate candidate, String partyKey, String position) {

        //if(pl == null)
        return databaseReference.child(partyKey).child("positions").child(position).setValue(candidate);
    }



}
