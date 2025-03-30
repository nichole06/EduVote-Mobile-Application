package com.example.eduvote;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {
    private Context context;
    private List<Candidate> candidates;
    private Map<String, String> selectedCandidates;
    private OnVoteSelectedListener voteSelectedListener;
    private String positionName;
    private int selectedPosition = -1; // Tracks the selected candidate in this position

    public CandidateAdapter(Context context, List<Candidate> candidates, Map<String, String> selectedCandidates, String positionName, OnVoteSelectedListener listener) {
        this.context = context;
        this.candidates = candidates;
        this.selectedCandidates = selectedCandidates;
        this.positionName = positionName;
        this.voteSelectedListener = listener;

        // Initialize selected position from existing selection
        String selectedId = selectedCandidates.get(positionName);
        if (selectedId != null) {
            for (int i = 0; i < candidates.size(); i++) {
                if (selectedId.equals(candidates.get(i).getId())) {
                    selectedPosition = i;
                    break;
                }
            }
        }
    }

    public interface OnVoteSelectedListener {
        void onVoteSelected(Map<String, String> selectedCandidates);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidate candidate = candidates.get(position);

        if (candidate == null || positionName == null) {
            return;
        }

        // Set candidate name
        holder.candidateName.setText(candidate.getCandidateName() + candidate.getCandidatePartylist());

        // Ensure only one is selected
        holder.radioVote.setChecked(position == selectedPosition);

        holder.radioVote.setOnClickListener(v -> {
            if (selectedPosition != position) {
                selectedPosition = position; // Update selected position
                selectedCandidates.put(positionName, candidate.getCandidateName());

                if (voteSelectedListener != null) {
                    voteSelectedListener.onVoteSelected(selectedCandidates);
                }


                // Refresh the list to update selection
                notifyItemRangeChanged(0, candidates.size());
            }

            Log.d("SelectedCandidate", "Selected Candidates: "+selectedCandidates);
        });
    }

    @Override
    public int getItemCount() {
        return candidates != null ? candidates.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioVote;
        TextView candidateName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioVote = itemView.findViewById(R.id.radioVote);
            candidateName = itemView.findViewById(R.id.candidateName);
        }
    }
}
