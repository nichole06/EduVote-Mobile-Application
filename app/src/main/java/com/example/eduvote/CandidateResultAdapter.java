package com.example.eduvote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CandidateResultAdapter extends RecyclerView.Adapter<CandidateResultAdapter.ViewHolder> {
    private Context context;
    private List<Candidate> candidates;

    public CandidateResultAdapter(Context context, List<Candidate> candidates) {
        this.context = context;
        this.candidates = candidates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_result_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidate candidate = candidates.get(position);

        if (candidate == null) {
            return;
        }

        // Set candidate name and vote count
        holder.candidateName.setText(candidate.getCandidateName());
        holder.voteCount.setText(String.valueOf(candidate.getVoteCount())); // Display vote count
    }

    @Override
    public int getItemCount() {
        return candidates != null ? candidates.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView candidateName, voteCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            candidateName = itemView.findViewById(R.id.candidateName);
            voteCount = itemView.findViewById(R.id.voteCount);
        }
    }
}
