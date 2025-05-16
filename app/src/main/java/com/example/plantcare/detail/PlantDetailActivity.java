package com.example.plantcare.detail;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.PlantDao;
import com.example.plantcare.data.PlantEntity;

import org.json.JSONException;
import org.json.JSONObject;

public class PlantDetailActivity extends AppCompatActivity {

    private static final String TREFLE_TOKEN =
            "Z1IFfWPyN9LX5wNFgkTnhEadEz3kI3wzb9Hm9DKC2wE";

    private ProgressBar progress;
    private ScrollView content;
    private ImageView imgPlant;
    private TextView tvCommon, tvScientific, tvFamily, tvGenus, tvDesc;
    private Button btnSave;

    private int    currentId;
    private String currentCommon, currentScientific, currentImageUrl;
    private PlantDao dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        // Récupération des vues
        progress     = findViewById(R.id.progressBar);
        content      = findViewById(R.id.contentLayout);
        imgPlant     = findViewById(R.id.imgPlantDetail);
        tvCommon     = findViewById(R.id.tvCommonDetail);
        tvScientific = findViewById(R.id.tvScientificDetail);
        tvFamily     = findViewById(R.id.tvFamilyDetail);
        tvGenus      = findViewById(R.id.tvGenusDetail);
        tvDesc       = findViewById(R.id.tvDescDetail);
        btnSave      = findViewById(R.id.btnSavePlant);

        // Initialisation du DAO Room
        dao = AppDatabase.getInstance(this).plantDao();

        // Lecture du slug passé depuis SearchFragment
        String plantSlug = getIntent().getStringExtra("plant_slug");
        if (plantSlug == null || plantSlug.isEmpty()) {
            Toast.makeText(this, "Slug de plante manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Chargement du détail
        fetchPlantDetail(plantSlug);

        // Bouton "Ajouter à Mes Plantes"
        btnSave.setOnClickListener(v ->
                new Thread(() -> {
                    PlantEntity entity = new PlantEntity(
                            currentId,
                            currentCommon,
                            currentScientific,
                            currentImageUrl
                    );
                    dao.insertPlant(entity);
                    runOnUiThread(() ->
                            Toast.makeText(
                                    this,
                                    "Plante ajoutée à Mes Plantes",
                                    Toast.LENGTH_SHORT
                            ).show()
                    );
                }).start()
        );
    }

    /**
     * Appelle l'API Trefle par slug pour obtenir les détails,
     * puis met à jour l'UI.
     */
    private void fetchPlantDetail(String slug) {
        progress.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);

        // Construction de l'URL : /plants/{slug}?token=...
        String url = Uri.parse("https://trefle.io/api/v1/plants")
                .buildUpon()
                .appendPath(slug)
                .appendQueryParameter("token", TREFLE_TOKEN)
                .build()
                .toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progress.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    try {
                        JSONObject data = response.getJSONObject("data");

                        // Stockage temporaire
                        currentId         = data.getInt("id");
                        currentCommon     = data.optString("common_name", "—");
                        currentScientific = data.optString("scientific_name", "—");
                        currentImageUrl   = data.optString("image_url", "");

                        // Affichage image + textes de base
                        Glide.with(this)
                                .load(currentImageUrl)
                                .placeholder(R.drawable.ic_leaf_logo)
                                .into(imgPlant);
                        tvCommon.setText(currentCommon);
                        tvScientific.setText(currentScientific);

                        // Extraction propre de family
                        String familyName = "—";
                        if (data.has("family") && !data.isNull("family")) {
                            JSONObject famObj = data.getJSONObject("family");
                            familyName = famObj.optString("name", "—");
                        }
                        // Extraction propre de genus
                        String genusName = "—";
                        if (data.has("genus") && !data.isNull("genus")) {
                            JSONObject genObj = data.getJSONObject("genus");
                            genusName = genObj.optString("name", "—");
                        }
                        tvFamily.setText(getString(R.string.label_family) + " " + familyName);
                        tvGenus .setText(getString(R.string.label_genus)  + " " + genusName);

                        // Description / bibliographie
                        tvDesc.setText(data.optString("bibliography",
                                getString(R.string.no_description)));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(
                                this,
                                "Erreur de parsing détail",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    String msg = (error.networkResponse != null)
                            ? "Erreur réseau code " + error.networkResponse.statusCode
                            : "Erreur réseau : " + error.toString();
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    progress.setVisibility(View.GONE);
                }
        );
        queue.add(req);
    }
}
