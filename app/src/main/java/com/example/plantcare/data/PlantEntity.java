package com.example.plantcare.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plants")
public class PlantEntity {
    @PrimaryKey
    private final int id;
    private final String commonName;
    private final String scientificName;
    private final String imageUrl;
    private long lastWatered;  // ← nouveau champ

    public PlantEntity(int id,
                       String commonName,
                       String scientificName,
                       String imageUrl) {
        this.id             = id;
        this.commonName     = commonName;
        this.scientificName = scientificName;
        this.imageUrl       = imageUrl;
        this.lastWatered    = 0;
    }

    public int getId() { return id; }
    public String getCommonName() { return commonName; }
    public String getScientificName() { return scientificName; }
    public String getImageUrl() { return imageUrl; }
    public long getLastWatered() { return lastWatered; }
    public void setLastWatered(long ts) { this.lastWatered = ts; }
}
