package com.example.eduvote;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PartyListMainAdapter extends FirebaseRecyclerAdapter<PartyListMainModel,PartyListMainAdapter.ViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PartyListMainAdapter(@NonNull FirebaseRecyclerOptions<PartyListMainModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PartyListMainAdapter.ViewHolder holder, int position, @NonNull PartyListMainModel model) {

        holder.partyName.setText(model.getPartyName());

        String partyLabel = "Partylist " + getPartyName(position);
        holder.partyCount.setText(partyLabel);

        String key = getRef(position).getKey();
        holder.ptInfo.setTag(key);

        holder.ptInfo.setOnClickListener(view -> {
            String clickedKey = (String) view.getTag();
            Log.d("ClickedKey", "ClickedKey: "+clickedKey);
            Intent intent = new Intent(view.getContext(), partylistInfo.class);
            intent.putExtra("partyKey", clickedKey);
            view.getContext().startActivity(intent);
        });

        holder.deleteBtn.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Are you sure you want to delete this candidate?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DAOPartyList daoPLremove = new DAOPartyList();
                            daoPLremove.delete(key)
                                            .addOnSuccessListener(suc -> {
                                                Toast.makeText(view.getContext(), "Partylist has been successfully deleted!", Toast.LENGTH_SHORT).show();


                                            }).addOnFailureListener(e -> {
                                                Toast.makeText(view.getContext(), "Failed to delete Partylist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        });
    }

    private String getPartyName(int index) {
        StringBuilder partyName = new StringBuilder();

        while (index >= 0) {
            partyName.insert(0, (char) ('A' + (index % 26)));
            index = (index / 26) - 1; // Adjust index for next letter
        }

        return partyName.toString();
    }

    // separate

    @NonNull
    @Override
    public PartyListMainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.partylist_item,parent, false));


    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView partyName;
        TextView partyCount;
        ImageView ptInfo;
        ImageView deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            partyName = itemView.findViewById(R.id.partyName);
            partyCount = itemView.findViewById(R.id.partyCount);
            ptInfo = itemView.findViewById(R.id.ptInfo);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}



