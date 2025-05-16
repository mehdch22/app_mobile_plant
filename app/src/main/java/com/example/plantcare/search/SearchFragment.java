package com.example.plantcare.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantcare.R;
import com.example.plantcare.detail.PlantDetailActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private static final String TREFLE_TOKEN =
            "Z1IFfWPyN9LX5wNFgkTnhEadEz3kI3wzb9Hm9DKC2wE";

    private TextInputEditText etSearch;
    private Button btnSearch;
    private RecyclerView rvResults;
    private PlantAdapter adapter;
    private final ArrayList<Plant> plants = new ArrayList<>();
    private RequestQueue queue;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch  = v.findViewById(R.id.etSearch);
        btnSearch = v.findViewById(R.id.btnSearch);
        rvResults = v.findViewById(R.id.rvResults);

        adapter = new PlantAdapter(plants, requireContext(), plant -> {
            Intent i = new Intent(requireContext(), PlantDetailActivity.class);
            i.putExtra("plant_slug", plant.getSlug());
            startActivity(i);
        });
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvResults.setAdapter(adapter);

        queue = Volley.newRequestQueue(requireContext());
        btnSearch.setOnClickListener(view -> {
            String q = etSearch.getText().toString().trim();
            if (q.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Veuillez entrer un nom de plante",
                        Toast.LENGTH_SHORT).show();
            } else {
                fetchPlants(q);
            }
        });

        return v;
    }

    private void fetchPlants(String name) {
        String url = Uri.parse("https://trefle.io/api/v1/plants")
                .buildUpon()
                .appendQueryParameter("token", TREFLE_TOKEN)
                .appendQueryParameter("q", name)
                .build()
                .toString();

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    plants.clear();
                    try {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject o = data.getJSONObject(i);
                            int    id     = o.getInt("id");
                            String slug   = o.getString("slug");
                            String common = o.optString("common_name", "—");
                            String sci    = o.optString("scientific_name", "—");
                            String imgUrl = o.optString("image_url", "");
                            plants.add(new Plant(id, common, sci, imgUrl, slug));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(),
                                "Erreur de parsing", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    String msg = (error.networkResponse != null)
                            ? "Erreur réseau : code " + error.networkResponse.statusCode
                            : "Erreur réseau : " + error.toString();
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                }
        );

        queue.add(req);
    }
}
