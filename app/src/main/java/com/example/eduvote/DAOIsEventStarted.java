package com.example.eduvote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOIsEventStarted {

    private DatabaseReference databaseReference;

    public DAOIsEventStarted(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("isEventStarted");
    }

    public Task<Void> updateStatus(Boolean status) {

        //if(pl == null)
        return databaseReference.setValue(status);
    }

}
