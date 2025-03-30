package com.example.eduvote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView hamburgerMenu;  // Reference to the button

    String previousEvent;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Remove or comment out the default back button behavior
        // super.onBackPressed();

        // Implement your custom back navigation

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference previousEventRef = database.child("previousEvent");

        previousEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                previousEvent = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        // Initialize the DrawerLayout, NavigationView, and other components
        drawerLayout = findViewById(R.id.admin_drawer_layout);
        navigationView = findViewById(R.id.admin_nav);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_menu, R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        hamburgerMenu = findViewById(R.id.hamburgerMenu);

        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.dashboard) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.pollManagement) {
                    Intent intent = new Intent(AdminDashboard.this, pollManagement.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else if (id == R.id.logout) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(AdminDashboard.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(AdminDashboard.this, "You have successfully logged out.", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.viewPreviousEventResult) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    if(previousEvent != null && !previousEvent.isEmpty()){
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent = new Intent(AdminDashboard.this, VotingResult.class);
                        startActivity(intent);
                    } else {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(AdminDashboard.this, "There is no previous event.", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        //Total voters
        TextView totalVotersTextView = findViewById(R.id.totalVoters);
        DatabaseReference studentVotersRef = database.child("students").child("studentID");
        studentVotersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalVoters = snapshot.getChildrenCount();
                totalVotersTextView.setText(String.valueOf(totalVoters));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TotalVoters", "error: " + error);
            }
        });

        //Total Active PartyList

        TextView totalActivePartylist = findViewById(R.id.totalVoters8);
        DatabaseReference currentEventRef = database.child("currentEvent");
        currentEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentEvent = snapshot.getValue(String.class);
                Log.d("CurrentEvent", "Current Event: " + currentEvent);

                if(currentEvent != null && !currentEvent.isEmpty()){
                    DatabaseReference totalPartylistRefer = database.child("VotingEvents").child(currentEvent);

                    totalPartylistRefer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long totalPartylists = snapshot.getChildrenCount();
                            totalActivePartylist.setText(String.valueOf(totalPartylists));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    TextView totalVoters6 = findViewById(R.id.totalVoters6);
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference studentVotersRef = database.child("students").child("studentID");

                    studentVotersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long totalVoters = snapshot.getChildrenCount();
                            long votedStudentCount = 0;



                            for (DataSnapshot studentVoteSnapshot : snapshot.getChildren()) {
                                Boolean isVoted = studentVoteSnapshot.child("isVoted").getValue(Boolean.class);

                                if(isVoted != null && isVoted){
                                    votedStudentCount += 1;
                                }
                            }


                            float votingParticipationPercentage = ((float) votedStudentCount / totalVoters) * 100;
                            totalVoters6.setText(String.valueOf(votingParticipationPercentage) + "%");

                            Log.d("VotingParticipation", "TotalVoters: "+totalVoters);
                            Log.d("VotingParticipation", "TotalStudentVotes: " + votedStudentCount);
                            Log.d("VotingParticipation", "VotingParticipation: " + votingParticipationPercentage);



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TotalPartyList", "error: " + error);
            }
        });


    }

}
