package com.example.eduvote;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class DAOPartyList {

    private DatabaseReference databaseReference;
    public DAOPartyList() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("PartyList");


    }
    public Task<Void> add(PartyList pl) {

        //if(pl == null)
        return databaseReference.push().setValue(pl);
    }
    public Task<Void> delete(String key) {
        return databaseReference.child(key).removeValue();
    }

}
