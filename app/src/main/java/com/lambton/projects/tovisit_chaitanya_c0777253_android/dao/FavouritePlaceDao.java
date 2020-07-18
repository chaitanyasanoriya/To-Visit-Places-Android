package com.lambton.projects.tovisit_chaitanya_c0777253_android.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.lambton.projects.tovisit_chaitanya_c0777253_android.models.FavouritePlace;

import java.util.List;

@Dao
public interface FavouritePlaceDao
{
    @Insert
    long insert(FavouritePlace favouritePlace);

    @Update
    void update(FavouritePlace favouritePlace);

    @Delete
    void delete(FavouritePlace favouritePlace);

    @Query("Select * from favourite_places_table ORDER BY visited, title")
    LiveData<List<FavouritePlace>> getAllFavouritePlaces();
}
