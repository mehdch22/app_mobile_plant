package com.example.plantcare.myplants;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.plantcare.R;
import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.PlantDao;
import com.example.plantcare.data.PlantEntity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyPlantsFragment extends Fragment {

    private RecyclerView rvMyPlants;
    private MyPlantsAdapter adapter;
    private PlantDao dao;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_plants, container, false);

        rvMyPlants = v.findViewById(R.id.rvMyPlants);
        dao = AppDatabase.getInstance(requireContext()).plantDao();

        adapter = new MyPlantsAdapter(requireContext(), new MyPlantsAdapter.OnPlantAction() {
            @Override
            public void onWater(PlantEntity plant) {
                // Mettre à jour le timestamp dans un thread de fond
                new Thread(() -> {
                    plant.setLastWatered(System.currentTimeMillis());
                    dao.updatePlant(plant);
                }).start();

                // Ouvrir le TimePicker pour planifier le rappel
                Calendar now = Calendar.getInstance();
                new TimePickerDialog(requireContext(),
                        (TimePicker tp, int hour, int minute) -> {
                            Calendar target = Calendar.getInstance();
                            target.set(Calendar.HOUR_OF_DAY, hour);
                            target.set(Calendar.MINUTE, minute);
                            target.set(Calendar.SECOND, 0);

                            long delay = target.getTimeInMillis() - System.currentTimeMillis();
                            if (delay < 0) {
                                // si l'heure choisie est déjà passée, reporter au lendemain
                                delay += TimeUnit.DAYS.toMillis(1);
                            }

                            OneTimeWorkRequest wr = new OneTimeWorkRequest.Builder(WaterReminderWorker.class)
                                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                    .build();
                            WorkManager.getInstance(requireContext()).enqueue(wr);

                            Snackbar.make(rvMyPlants, R.string.notif_watered, Snackbar.LENGTH_SHORT).show();
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                ).show();
            }

            @Override
            public void onDelete(PlantEntity plant) {
                // Suppression en background
                new Thread(() -> {
                    dao.deletePlant(plant);
                }).start();
                Snackbar.make(rvMyPlants, R.string.notif_deleted, Snackbar.LENGTH_SHORT).show();
            }
        });

        rvMyPlants.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMyPlants.setAdapter(adapter);
        dao.getAllPlants().observe(getViewLifecycleOwner(), this::updateList);

        return v;
    }

    private void updateList(List<PlantEntity> list) {
        adapter.setPlants(list);
        View empty = requireView().findViewById(R.id.tvEmpty);
        empty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }
}