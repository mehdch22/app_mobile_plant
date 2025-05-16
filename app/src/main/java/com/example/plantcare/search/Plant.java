package com.example.plantcare.search;

/**
 * Modèle représentant une plante issue de l'API.
 */
public class Plant {
    private final int id;
    private final String commonName;
    private final String scientificName;
    private final String imageUrl;
    private final String slug;

    public Plant(int id,
                 String commonName,
                 String scientificName,
                 String imageUrl,
                 String slug) {
        this.id             = id;
        this.commonName     = commonName;
        this.scientificName = scientificName;
        this.imageUrl       = imageUrl;
        this.slug           = slug;
    }

    public int getId() { return id; }
    public String getCommonName() { return commonName; }
    public String getScientificName() { return scientificName; }
    public String getImageUrl() { return imageUrl; }
    public String getSlug() { return slug; }
}
