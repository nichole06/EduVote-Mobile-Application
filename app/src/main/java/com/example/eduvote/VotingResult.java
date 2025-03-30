package com.example.eduvote;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.*;

public class VotingResult extends AppCompatActivity {

    private RecyclerView resultsRecyclerView;
    private PositionResultAdapter positionResultAdapter;
    private List<Position> positionsList;
    private Map<String, List<Candidate>> resultsMap;
    private FirebaseDatabase database;
    private DatabaseReference votingEventsRef;
    private String selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_result);

        // Initialize RecyclerView
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize lists
        positionsList = new ArrayList<>();
        resultsMap = new HashMap<>();

        DatabaseReference databaseForCurrentEvent = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentEventReference = databaseForCurrentEvent.child("previousEvent");

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
        TextView eventName = findViewById(R.id.eventName);
        eventName.setText(selectedEvent + " - Result");
        fetchVotingResults();
    }

    private void fetchVotingResults() {
        votingEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                positionsList.clear();
                resultsMap.clear();
                HashMap<String, Integer> positionOrder = getPositionOrder();

                for (DataSnapshot partylistSnapshot : snapshot.getChildren()) {
                    String partylistId = partylistSnapshot.getKey();
                    String candidatePartyList = " (" + partylistSnapshot.child("partyName").getValue(String.class) + ")";
                    Log.d("Firebase Debug", "Processing Partylist: " + partylistId);

                    DataSnapshot positionsSnapshot = partylistSnapshot.child("positions");
                    if (!positionsSnapshot.exists()) {
                        Log.e("Firebase Debug", "No positions found for partylist: " + partylistId);
                        continue;
                    }

                    for (DataSnapshot positionSnapshot : positionsSnapshot.getChildren()) {
                        String positionName = positionSnapshot.getKey();
                        Log.d("Firebase Debug", "Found Position: " + positionName);

                        if (!positionsListContains(positionName)) {
                            positionsList.add(new Position(positionName));
                        }

                        // ✅ Get candidate details with vote count
                        String candidateName = positionSnapshot.child("candidateName").getValue(String.class);
                        Long votes = positionSnapshot.child("voteCount").getValue(Long.class);

                        if (candidateName != null) {
                            resultsMap.computeIfAbsent(positionName, k -> new ArrayList<>())
                                    .add(new Candidate(candidateName + candidatePartyList, votes != null ? votes : 0));
                            Log.d("Voting Results", "Candidate: " + candidateName + ", Votes: " + votes);
                        } else {
                            Log.e("Voting Results", "Candidate name is null for position: " + positionName + " in partylist: " + partylistId);
                        }
                    }
                }

                // ✅ Sort positions based on predefined order
                positionsList.sort(Comparator.comparingInt(p -> positionOrder.getOrDefault(p.getName(), Integer.MAX_VALUE)));

                // ✅ Set up adapter using PositionResultAdapter
                positionResultAdapter = new PositionResultAdapter(VotingResult.this, positionsList, resultsMap);
                resultsRecyclerView.setAdapter(positionResultAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching voting results", error.toException());
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
