package com.example.eduvote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class pollManagement extends AppCompatActivity {

    RecyclerView recyclerView;
    PartyListMainAdapter partyListMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_poll_management);

        // here

        Button addPartyListBtn = findViewById(R.id.addPartylistBtn);
        Button startEventBtn = findViewById(R.id.startVoting);

        addPartyListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(pollManagement.this, addPartylist.class);
                startActivity(intent);
            }
        });

        startEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pollManagement.this, startEventManager.class);
                startActivity(intent);
            }
        });



        recyclerView = findViewById(R.id.recycleViewPartylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this ));

        FirebaseRecyclerOptions<PartyListMainModel> options = new FirebaseRecyclerOptions.Builder<PartyListMainModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("PartyList"), PartyListMainModel.class).build();

        partyListMainAdapter = new PartyListMainAdapter(options);
        recyclerView.setAdapter(partyListMainAdapter);



    }

    @Override
    protected void onStart() {
        partyListMainAdapter.startListening();

        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference().child("students").child("studentID");
        DatabaseReference isEventStarted = FirebaseDatabase.getInstance().getReference().child("isEventStarted");
        DatabaseReference currentEventRef = FirebaseDatabase.getInstance().getReference().child("currentEvent");
        DatabaseReference previousEventRef = FirebaseDatabase.getInstance().getReference().child("previousEvent");

        Button startEventBtn = findViewById(R.id.startVoting);

        isEventStarted.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Button startEventBtn = findViewById(R.id.startVoting);
                Boolean status = snapshot.getValue(Boolean.class);

                if(status) {
                    startEventBtn.setText("Finish Event");
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    startEventBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(pollManagement.this)
                                    .setTitle("Finish Event")
                                    .setMessage("Are you sure you want to finish the current event?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        database.child("isEventStarted").setValue(false);

                                        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Map<String, Object> updates = new HashMap<>();

                                                    for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                                        String studentID = studentSnapshot.getKey(); // Get student ID

                                                        if (studentID != null) {
                                                            updates.put(studentID + "/isVoted", false);
                                                        }
                                                    }
                                                    // Update all students in one operation
                                                    studentsRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
                                                        Log.d("Firebase", "All students' isVoted set to false.");
                                                    }).addOnFailureListener(e -> {
                                                        Log.e("Firebase", "Failed to update isVoted for all students", e);
                                                    });

                                                } else {
                                                    Log.e("Firebase", "No students found in the database.");
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("Firebase", "Database error: " + error.getMessage());
                                            }
                                        });


                                        currentEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String currentEvent = snapshot.getValue(String.class);
                                                previousEventRef.setValue(currentEvent);
                                                currentEventRef.setValue("");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });




                                        Intent intent = getIntent();
                                        finish();
                                        overridePendingTransition(0, 0); // No animation
                                        startActivity(intent);
                                        overridePendingTransition(0, 0); // No animation
                                        Toast.makeText(pollManagement.this, "The event has been successfully finished", Toast.LENGTH_SHORT).show();

                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> {
                                        dialog.dismiss(); // Close the dialog
                                    })
                                    .show();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        super.onStart();
    }

    @Override
    protected void onStop() {
        partyListMainAdapter.startListening();
        super.onStop();
    }
}