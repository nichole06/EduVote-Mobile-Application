package com.example.eduvote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;

import java.util.*;

public class votingSystem extends AppCompatActivity implements CandidateAdapter.OnVoteSelectedListener {

    private RecyclerView positionsRecyclerView;
    private PositionAdapter positionAdapter;
    private List<Position> positionsList;
    private Map<String, List<Candidate>> candidatesMap;
    private FirebaseDatabase database;
    private DatabaseReference votingEventsRef;
    private String selectedEvent; // Change dynamically if needed
    private Map<String, String> selectedCandidates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_system);

        // Initialize RecyclerView
        positionsRecyclerView = findViewById(R.id.positionsRecycleView);
        positionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize lists
        positionsList = new ArrayList<>();
        candidatesMap = new HashMap<>();


        Button submitVoteBtn = findViewById(R.id.submitButton);
        submitVoteBtn.setOnClickListener(v -> submitVotesToFirebase());

        //Initialize current event
        DatabaseReference databaseForCurrentEvent = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentEventReference = databaseForCurrentEvent.child("currentEvent");

        currentEventReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    selectedEvent = snapshot.getValue(String.class);
                    handleSelectedEvent(selectedEvent);
                }
                else {
                    Log.e("Firebase", "No current event found in database");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
    }

    private void handleSelectedEvent(String event) {
        // Use selectedEvent here (e.g., update UI, query another database node, etc.)
        Log.d("Firebase", "Handling event: " + event);
        database = FirebaseDatabase.getInstance();
        votingEventsRef = database.getReference("VotingEvents").child(selectedEvent);
        TextView event_name = findViewById(R.id.eventName);
        event_name.setText(selectedEvent);
        fetchPositionsAndCandidates();
    }

    private void fetchPositionsAndCandidates() {
        votingEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                positionsList.clear();
                candidatesMap.clear();
                HashMap<String, Integer> positionOrder = getPositionOrder();

                for (DataSnapshot partylistSnapshot : snapshot.getChildren()) {
                    String partylistId = partylistSnapshot.getKey();
                    String candidatePartyList = " (" + partylistSnapshot.child("partyName").getValue(String.class) + ")";
                    Log.d("Firebase Debug", "Processing Partylist: " + partylistId); //

                    DataSnapshot positionsSnapshot = partylistSnapshot.child("positions");
                    if (!positionsSnapshot.exists()) {
                        Log.e("Firebase Debug", "No positions found for partylist: " + partylistId);
                        continue;
                    }

                    for (DataSnapshot positionSnapshot : positionsSnapshot.getChildren()) {
                        String positionName = positionSnapshot.getKey();
                        Log.d("Firebase Debug", "Found Position: " + positionName); //

                        if (!positionsListContains(positionName)) {
                            positionsList.add(new Position(positionName));
                        }

                        // ✅ Get candidate details
                        String candidateName = positionSnapshot.child("candidateName").getValue(String.class);
                        Long votes = positionSnapshot.child("voteCount").getValue(Long.class);

                        if (candidateName != null) {
                            candidatesMap.computeIfAbsent(positionName, k -> new ArrayList<>())
                                    .add(new Candidate(candidateName, votes != null ? votes : 0, candidatePartyList));
                            Log.d("Firebase Data", "Added Candidate: " + candidateName + ", Votes: " + votes);
                        } else {
                            Log.e("Firebase Data", "Candidate name is null for position: " + positionName + " in partylist: " + partylistId);
                        }
                    }
                }

                // ✅ Sort positions based on predefined order
                positionsList.sort(Comparator.comparingInt(p -> positionOrder.getOrDefault(p.getName(), Integer.MAX_VALUE)));

                // ✅ Update UI (Pass `this` as the listener)
                positionAdapter = new PositionAdapter(votingSystem.this, positionsList, candidatesMap, votingSystem.this);
                positionsRecyclerView.setAdapter(positionAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching positions and candidates", error.toException());
            }
        });
    }

    @Override
    public void onVoteSelected(Map<String, String> updatedVotes) {
        selectedCandidates.putAll(updatedVotes);
        Log.d("Voting System", "Selected Votes Updated: " + selectedCandidates);
    }

    private void submitVotesToFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("VotingEvents").child(selectedEvent);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> updates = new HashMap<>();
                boolean hasUpdates = false;

                for (DataSnapshot partyListSnapshot : snapshot.getChildren()) {
                    DataSnapshot positionsSnapshot = partyListSnapshot.child("positions");

                    for (DataSnapshot positionSnapshot : positionsSnapshot.getChildren()) {
                        String positionName = positionSnapshot.getKey(); // Example: "Governor"
                        String selectedCandidateName = selectedCandidates.get(positionName); // Example: "Juan"

                        if (selectedCandidateName != null) {
                            String candidateNameInDB = positionSnapshot.child("candidateName").getValue(String.class);

                            if (candidateNameInDB != null && candidateNameInDB.equals(selectedCandidateName)) {
                                Long currentVoteCount = positionSnapshot.child("voteCount").getValue(Long.class);
                                if (currentVoteCount == null) {
                                    currentVoteCount = 0L;
                                }

                                // ✅ FIX: Correctly build the path using getKey()
                                String path = partyListSnapshot.getKey() + "/positions/" + positionName + "/voteCount";
                                Log.d("SubmitVote", "Updating path: " + path);

                                updates.put(path, currentVoteCount + 1);
                                hasUpdates = true;
                            }
                        }
                    }
                }

                if (hasUpdates) {
                    databaseReference.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("SubmitVote", "Votes submitted successfully.");
                                Toast.makeText(getApplicationContext(), "Votes submitted successfully!", Toast.LENGTH_SHORT).show();

                                String studentID = getIntent().getStringExtra("studentID");
                                assert studentID != null;
                                DatabaseReference isVoted = FirebaseDatabase.getInstance().getReference().child("students").child("studentID").child(studentID);
                                isVoted.child("isVoted").setValue(true).addOnSuccessListener(iVoid -> {
                                    Intent intent= new Intent(votingSystem.this, student_dashboard.class);
                                    intent.putExtra("studentID", studentID);
                                    String studentName = getIntent().getStringExtra("studentName");
                                    intent.putExtra("studentName", studentName);
                                    startActivity(intent);
                                    finish();
                                }).addOnFailureListener(e -> {

                                });


                            })
                            .addOnFailureListener(e -> {
                                Log.e("SubmitVote", "Error updating votes", e);
                                Toast.makeText(getApplicationContext(), "Failed to submit votes!", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e("SubmitVote", "No valid votes to submit.");
                    Toast.makeText(getApplicationContext(), "No votes selected!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SubmitVote", "Firebase Error", error.toException());
                Toast.makeText(getApplicationContext(), "Failed to submit votes!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HashMap<String, Integer> getPositionOrder() {
        HashMap<String, Integer> order = new HashMap<>();
        order.put("governor", 1);
        order.put("vicegovernor", 2);
        order.put("secretary", 3);
        order.put("treasurer", 4);
        order.put("budget", 5);
        order.put("auditor", 6);
        order.put("pio", 7);
        order.put("secondyearrep", 8);
        order.put("thirdyearrep", 9);
        order.put("fourthyearrepresentative", 10);
        return order;
    }

    private boolean positionsListContains(String positionName) {
        return positionsList.stream().anyMatch(pos -> pos.getName().equals(positionName));
    }
}
