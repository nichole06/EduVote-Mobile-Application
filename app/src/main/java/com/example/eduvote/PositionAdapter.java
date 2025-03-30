package com.example.eduvote;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.ViewHolder>
        implements CandidateAdapter.OnVoteSelectedListener {

    private Context context;
    private List<Position> positions;
    private Map<String, List<Candidate>> candidatesMap;
    private Map<String, String> selectedCandidates; // Stores selected candidate per position

    private CandidateAdapter.OnVoteSelectedListener voteSelectedListener; // External listener

    public PositionAdapter(Context context, List<Position> positions,
                           Map<String, List<Candidate>> candidatesMap,
                           CandidateAdapter.OnVoteSelectedListener listener) {
        this.context = context;
        this.positions = positions;
        this.candidatesMap = candidatesMap;
        this.selectedCandidates = new HashMap<>();
        this.voteSelectedListener = listener; // Store external listener
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.position_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Position pos = positions.get(position);
        String displayName = getDisplayName(pos.getName());
        holder.positionName.setText(displayName);

        List<Candidate> candidates = candidatesMap.getOrDefault(pos.getName(), List.of());

        // Setup inner RecyclerView with correct selection handling
        CandidateAdapter candidateAdapter = new CandidateAdapter(
                context, candidates, selectedCandidates, pos.getName(), this
        );

        holder.candidatesRecyclerView.setAdapter(candidateAdapter);
        holder.candidatesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    // 🔥 Fix: Correctly updates selectedCandidates when a vote is selected
    @Override
    public void onVoteSelected(Map<String, String> updatedVotes) {
        this.selectedCandidates.putAll(updatedVotes); // Store updated votes
        Log.d("PositionAdapter", "Updated Votes: " + selectedCandidates);

        // Notify the external listener (e.g., VotingActivity)
        if (voteSelectedListener != null) {
            voteSelectedListener.onVoteSelected(selectedCandidates);
        }
    }

    // 🔹 Helper method to format position names
    private String getDisplayName(String position) {
        switch (position.toLowerCase()) {
            case "governor": return "Governor";
            case "vicegovernor": return "Vice Governor";
            case "secretary": return "Secretary";
            case "treasurer": return "Treasurer";
            case "budget": return "Budget Officer";
            case "auditor": return "Auditor";
            case "pio": return "P.I.O";
            case "fourthyearrepresentative": return "4th Year Representative";
            case "thirdyearrep": return "3rd Year Representative";
            case "secondyearrep": return "2nd Year Representative";
            default: return position;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView positionName;
        RecyclerView candidatesRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            positionName = itemView.findViewById(R.id.positionName);
            candidatesRecyclerView = itemView.findViewById(R.id.candidatesRecyclerView);
        }
    }

    // Getter method to retrieve selected candidates
    public Map<String, String> getSelectedCandidates() {
        return selectedCandidates;
    }
}
