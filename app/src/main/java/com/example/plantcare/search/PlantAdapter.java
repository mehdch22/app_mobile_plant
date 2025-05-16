package com.example.plantcare.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.search.Plant;

import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Plant plant);
    }

    private final List<Plant> plants;
    private final Context context;
    private final OnItemClickListener listener;

    public PlantAdapter(List<Plant> plants, Context context, OnItemClickListener listener) {
        this.plants = plants;
        this.context = context;
        this.listener = listener;
    }

    @NonNull @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.card_item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant p = plants.get(position);
        holder.tvCommon.setText(p.getCommonName());
        holder.tvScientific.setText(p.getScientificName());
        Glide.with(context)
                .load(p.getImageUrl())
                .placeholder(R.drawable.ic_leaf_logo)
                .into(holder.imgPlant);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(p));
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlant;
        TextView tvCommon, tvScientific;

        PlantViewHolder(View itemView) {
            super(itemView);
            imgPlant    = itemView.findViewById(R.id.imgPlant);
            tvCommon    = itemView.findViewById(R.id.tvCommonName);
            tvScientific= itemView.findViewById(R.id.tvScientificName);
        }
    }
}
