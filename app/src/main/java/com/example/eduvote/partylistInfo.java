package com.example.eduvote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class partylistInfo extends AppCompatActivity {

    String candidatePos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_partylist_info);

        Button addCandidateBtn = findViewById(R.id.addCandidate);
        String partyKey = getIntent().getStringExtra("partyKey");


        Spinner posSpinner = findViewById(R.id.deleteCandidate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.position_dropdown_items_delete, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        posSpinner.setAdapter(adapter);

        DAOcandidate daoCand = new DAOcandidate();
        posSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0) {
                    return;
                }

                candidatePos = parent.getItemAtPosition(position).toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(partylistInfo.this);
                builder.setMessage("Are you sure you want to delete this candidate?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Candidate candidate = new Candidate("");
                                String candidatePosFinal = candidatePos.replace(" ", "").toLowerCase();
                                daoCand.add(candidate, partyKey, candidatePosFinal).addOnSuccessListener(suc -> {
                                    Toast.makeText(partylistInfo.this, "Candidate of "+ candidatePos + "has been deleted successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = getIntent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    finish();
                                }).addOnFailureListener(err -> {

                                });

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Handle the negative button (No) click
                                dialog.cancel(); // Close the dialog
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addCandidateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(partylistInfo.this, addCandidate.class);
                intent.putExtra("partyKey", partyKey);
                startActivity(intent);
            }
        });

        fetchPartyListData(partyKey);
    }

    private void fetchPartyListData(String partyKey) {
        DatabaseReference partyRef = FirebaseDatabase.getInstance().getReference().child("PartyList").child(partyKey);

        TextView partyName = findViewById(R.id.partyName);

        TextView governor = findViewById(R.id.governor);
        TextView viceGovernor = findViewById(R.id.viceGov);
        TextView secretary = findViewById(R.id.secretary);
        TextView treasurer = findViewById(R.id.treasurer);
        TextView budget = findViewById(R.id.budget);
        TextView auditor = findViewById(R.id.auditor);
        TextView pio = findViewById(R.id.pio);
        TextView fourthYearRep = findViewById(R.id.fourthYearRep);
        TextView thirdYearRep = findViewById(R.id.thirdYearRep);
        TextView secondYearRep = findViewById(R.id.secondYearRep);

        partyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String partyNameValue = snapshot.child("partyName").getValue(String.class);

                String governorName = snapshot.child("positions").child("governor").child("candidateName").getValue(String.class);
                String viceGovernorName = snapshot.child("positions").child("vicegovernor").child("candidateName").getValue(String.class);
                String secretaryName = snapshot.child("positions").child("secretary").child("candidateName").getValue(String.class);
                String treasurerName = snapshot.child("positions").child("treasurer").child("candidateName").getValue(String.class);
                String budgetName = snapshot.child("positions").child("budget").child("candidateName").getValue(String.class);
                String auditorName = snapshot.child("positions").child("auditor").child("candidateName").getValue(String.class);
                String pioName = snapshot.child("positions").child("pio").child("candidateName").getValue(String.class);
                String fourthYearRepName = snapshot.child("positions").child("fourthyearrepresentative").child("candidateName").getValue(String.class);
                String thirdYearRepName = snapshot.child("positions").child("thirdyearrepresentative").child("candidateName").getValue(String.class);
                String secondYearRepName = snapshot.child("positions").child("secondyearrepresentative").child("candidateName").getValue(String.class);

                if(partyNameValue != null) {
                    partyName.setText(partyNameValue);

                    if (governorName != null && !governorName.isEmpty()) governor.setText(governorName);
                    if (viceGovernorName != null && !viceGovernorName.isEmpty()) viceGovernor.setText(viceGovernorName);
                    if (secretaryName != null && !secretaryName.isEmpty()) secretary.setText(secretaryName);
                    if (treasurerName != null && !treasurerName.isEmpty()) treasurer.setText(treasurerName);
                    if (budgetName != null && !budgetName.isEmpty()) budget.setText(budgetName);
                    if (auditorName != null && !auditorName.isEmpty()) auditor.setText(auditorName);
                    if (pioName != null && !pioName.isEmpty()) pio.setText(pioName);
                    if (fourthYearRepName != null && !fourthYearRepName.isEmpty()) fourthYearRep.setText(fourthYearRepName);
                    if (thirdYearRepName != null && !thirdYearRepName.isEmpty()) thirdYearRep.setText(thirdYearRepName);
                    if (secondYearRepName != null && !secondYearRepName.isEmpty()) secondYearRep.setText(secondYearRepName);


                }else {
                    partyName.setText("Party name not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}