package com.example.plantcare.myplants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.R;
import com.example.plantcare.data.PlantEntity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPlantsAdapter
        extends RecyclerView.Adapter<MyPlantsAdapter.ViewHolder> {

    public interface OnPlantAction {
        void onWater(PlantEntity plant);
        void onDelete(PlantEntity plant);
    }

    private final LayoutInflater inflater;
    private final OnPlantAction listener;
    private final List<PlantEntity> items = new ArrayList<>();

    public MyPlantsAdapter(Context ctx, OnPlantAction listener) {
        this.inflater = LayoutInflater.from(ctx);
        this.listener = listener;
    }

    /** Met à jour la liste des plantes affichées */
    public void setPlants(List<PlantEntity> plants) {
        items.clear();
        if (plants != null) items.addAll(plants);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.plant_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
        PlantEntity p = items.get(pos);

        // Nom commun
        vh.tvCommonName.setText(p.getCommonName());

        // Dernier arrosage
        long ts = p.getLastWatered();
        String last;
        if (ts == 0) {
            last = vh.itemView.getContext().getString(R.string.never_watered);
        } else {
            last = DateFormat.getDateTimeInstance().format(new Date(ts));
        }
        vh.tvLastWatered.setText(
                vh.itemView.getContext()
                        .getString(R.string.label_last_watering, last)
        );

        // Actions
        vh.btnWater.setOnClickListener(v -> listener.onWater(p));
        vh.btnDelete.setOnClickListener(v -> listener.onDelete(p));
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView    tvCommonName, tvLastWatered;
        final Button      btnWater;
        final ImageButton btnDelete;

        ViewHolder(View item) {
            super(item);
            tvCommonName  = item.findViewById(R.id.tvCommonName);
            tvLastWatered = item.findViewById(R.id.tvLastWatered);
            btnWater      = item.findViewById(R.id.btnWater);
            btnDelete     = item.findViewById(R.id.btnDelete);
        }
    }
}
