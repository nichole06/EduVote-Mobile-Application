package com.example.eduvote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class startEventManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_event_manager);

        // here
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        EditText eventNameEditText = findViewById(R.id.addEventEditText);
        Button startEventBtn = findViewById(R.id.startEvent);

        startEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child("PartyList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String eventName = eventNameEditText.getText().toString().replace(" ", "").trim();
                        if (snapshot.exists()) {
                            database.child("VotingEvents").child(eventName).setValue(snapshot.getValue())
                                    .addOnSuccessListener(aVoid -> {
                                        database.child("isEventStarted").setValue(true);
                                        Toast.makeText(startEventManager.this, "Event has started succesuffly!", Toast.LENGTH_SHORT).show();
                                        database.child("currentEvent").setValue(eventName);
                                        Intent intent = new Intent(startEventManager.this, pollManagement.class);
                                        startActivity(intent);
                                    }).addOnFailureListener(aVoid -> {
                                        Toast.makeText(startEventManager.this, "Failed to start the event", Toast.LENGTH_SHORT).show();
                                    });

                        }else {
                            Toast.makeText(startEventManager.this, "There is no party list that has been added yet", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("StartEventManager", ""+error);
                    }
                });
            }
        });

    }
}