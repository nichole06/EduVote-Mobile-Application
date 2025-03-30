package com.example.eduvote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class addCandidate extends AppCompatActivity {

    String candidatePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_candidate);

        Spinner posSpinner = findViewById(R.id.posDropdownMenuSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.position_dropdown_items, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        posSpinner.setAdapter(adapter);


        EditText edit_candidateName = findViewById(R.id.candidateName);
        Button addCandidateBtn = findViewById(R.id.addCandidateBtn);
        DAOcandidate daoCand = new DAOcandidate();
        String partyKey = getIntent().getStringExtra("partyKey");

        posSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0) {
                    return;
                }

                candidatePos = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addCandidateBtn.setOnClickListener(v -> {
            String candidateName = edit_candidateName.getText().toString();
            String candidatePosFinal = candidatePos.replaceAll("[.\\s]", "").toLowerCase();

            Log.d("CandidateName", "Candidate Name: "+candidateName +candidatePosFinal);

            if(candidateName.isEmpty()) {
                Toast.makeText(this, "Please provide a candidate name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (candidatePos.isEmpty() || candidatePos.equals("Select a Position")) {
                Toast.makeText(this, "Please provide the candidate's position", Toast.LENGTH_SHORT).show();
                return;
            }

            Candidate candidate = new Candidate(candidateName);
            daoCand.add(candidate, partyKey, candidatePosFinal).addOnSuccessListener(suc -> {
                Toast.makeText(this, "Candidate has been added successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(addCandidate.this, partylistInfo.class);
                intent.putExtra("partyKey", partyKey);
                startActivity(intent);
            }).addOnFailureListener(err -> {
                Toast.makeText(this, ""+err, Toast.LENGTH_SHORT).show();
            });


        });


    }
}