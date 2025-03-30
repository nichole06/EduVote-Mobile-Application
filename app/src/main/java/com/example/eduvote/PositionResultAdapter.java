package com.example.eduvote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class PositionResultAdapter extends RecyclerView.Adapter<PositionResultAdapter.ViewHolder> {

    private Context context;
    private List<Position> positions;
    private Map<String, List<Candidate>> candidatesMap;

    public PositionResultAdapter(Context context, List<Position> positions, Map<String, List<Candidate>> candidatesMap) {
        this.context = context;
        this.positions = positions;
        this.candidatesMap = candidatesMap;
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
        holder.positionName.setText(getDisplayName(pos.getName()));

        List<Candidate> candidates = candidatesMap.getOrDefault(pos.getName(), List.of());

        // ✅ Use CandidateResultAdapter instead of CandidateAdapter
        CandidateResultAdapter candidateResultAdapter = new CandidateResultAdapter(context, candidates);
        holder.candidatesRecyclerView.setAdapter(candidateResultAdapter);
        holder.candidatesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return positions.size();
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
}
