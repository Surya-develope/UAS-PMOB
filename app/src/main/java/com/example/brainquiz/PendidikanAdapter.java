package com.example.brainquiz;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainquiz.R;
import com.example.brainquiz.filter.Pendidikan;

import java.util.List;

public class PendidikanAdapter extends RecyclerView.Adapter<PendidikanAdapter.ViewHolder> {

    private List<Pendidikan> pendidikanList;

    public PendidikanAdapter(List<Pendidikan> pendidikanList) {
        this.pendidikanList = pendidikanList;
    }

    @NonNull
    @Override
    public PendidikanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pendidikan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendidikanAdapter.ViewHolder holder, int position) {
        Pendidikan pendidikan = pendidikanList.get(position);
        holder.tvNama.setText(pendidikan.getNama());
    }

    @Override
    public int getItemCount() {
        return pendidikanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
        }
    }
}
