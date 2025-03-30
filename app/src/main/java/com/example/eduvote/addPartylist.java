package com.example.eduvote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class addPartylist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_partylist);

        //here

        final EditText edit_partyName = findViewById(R.id.addPartyListEditText);
        Button addPartyBtn = findViewById(R.id.addPartyBtn);
        DAOPartyList daopl = new DAOPartyList();

        addPartyBtn.setOnClickListener(v-> {

            String partyName = edit_partyName.getText().toString().trim();

            if(partyName.isEmpty()) {
                Toast.makeText(this, "Please provide a party name", Toast.LENGTH_SHORT).show();
                return;
            }

            PartyList pl = new PartyList(partyName);
            daopl.add(pl).addOnSuccessListener(suc->{
                Toast.makeText(this, "Partylist has been added successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(addPartylist.this, pollManagement.class);
                startActivity(intent);
            }).addOnFailureListener(er->{
                Toast.makeText(this, ""+er, Toast.LENGTH_SHORT).show();
            });
        });
    }
}