package com.example.plantcare.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlant(PlantEntity plant);

    @Update
    void updatePlant(PlantEntity plant);

    @Delete
    void deletePlant(PlantEntity plant);

    @Query("SELECT * FROM plants")
    LiveData<List<PlantEntity>> getAllPlants();
}
