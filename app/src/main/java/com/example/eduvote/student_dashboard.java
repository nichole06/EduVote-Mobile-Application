package com.example.eduvote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class student_dashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    String previousEvent;
    ImageView hamburgerMenu;  // Reference to the button

    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);

        String studentID = getIntent().getStringExtra("studentID");
        String studentName = getIntent().getStringExtra("studentName");

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

        drawerLayout = findViewById(R.id.student_drawer_layout);
        navigationView = findViewById(R.id.student_nav);
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
                    Log.i("MENU_DRAWER_TAG", "Dashboard is clicked");
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.logout) {
                    Log.i("MENU_DRAWER_TAG", "Logout is clicked");
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(student_dashboard.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(student_dashboard.this, "You have successfully logged out.", Toast.LENGTH_SHORT).show();
                } else if(id == R.id.viewPreviousEventResult) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    if(previousEvent != null && !previousEvent.isEmpty()){
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent = new Intent(student_dashboard.this, VotingResult.class);
                        intent.putExtra("studentID", studentID);
                        intent.putExtra("studentName", studentName);
                        startActivity(intent);
                    } else {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(student_dashboard.this, "There is no previous event.", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });



        DatabaseReference isEventStartedRef = FirebaseDatabase.getInstance().getReference().child("isEventStarted");
        if (studentID == null) {
            Log.e("student_dashboard", "studentID is null! Something went wrong.");
            Toast.makeText(this, "Error: Student ID not found.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity safely
            return;
        }
        DatabaseReference isVoted = FirebaseDatabase.getInstance().getReference().child("students").child("studentID").child(studentID);
        Button voteBtn = findViewById(R.id.voteBtn);
        TextView text1 = findViewById(R.id.text1);
        TextView text2 = findViewById(R.id.text2);
        TextView text3 = findViewById(R.id.text3);
        ImageView rect = findViewById(R.id.rect);
        TextView studentNameView = findViewById(R.id.studentName);
        studentNameView.setText(studentName);

        isEventStartedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isStartedEvent = snapshot.getValue(Boolean.class);

                Log.d("isStartedEvent", ""+isStartedEvent);
                Log.d("studentIsVote", "IsVoted:" +isVoted.child("isVoted").getKey());

                isVoted.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        boolean isVoted = snapshot.child("isVoted").getValue(boolean.class);
                        if (isStartedEvent && !isVoted) {
                            rect.setVisibility(View.VISIBLE);
                            voteBtn.setVisibility(View.VISIBLE);
                            text1.setVisibility(View.VISIBLE);
                        } else if(isStartedEvent && isVoted == true){
                            rect.setVisibility(View.VISIBLE);
                            text3.setVisibility(View.VISIBLE);
                        } else {
                            rect.setVisibility(View.VISIBLE);
                            text2.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(student_dashboard.this, votingSystem.class);
                intent.putExtra("studentID", studentID);
                intent.putExtra("studentName", studentName);
                startActivity(intent);
            }
        });

//        viewResult.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(student_dashboard.this, VotingResult.class);
//                intent.putExtra("studentID", studentID);
//                intent.putExtra("studentName", studentName);
//                startActivity(intent);
//            }
//        });

    }
}