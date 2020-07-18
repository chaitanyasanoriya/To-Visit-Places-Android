package com.lambton.projects.tovisit_chaitanya_c0777253_android.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lambton.projects.tovisit_chaitanya_c0777253_android.models.FavouritePlace;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.repositories.FavouritePlaceRepository;

import java.util.List;

public class FavouritePlaceViewModel extends AndroidViewModel
{
    private FavouritePlaceRepository mRepository;
    private LiveData<List<FavouritePlace>> mFavouritePlaceList;

    public FavouritePlaceViewModel(@NonNull Application application)
    {
        super(application);
        mRepository = new FavouritePlaceRepository(application);
        mFavouritePlaceList = mRepository.getFavouritePlaceList();
        System.out.println(3);
    }

    public void insert(FavouritePlace favouritePlace)
    {
        mRepository.insert(favouritePlace);
    }

    public void update(FavouritePlace favouritePlace)
    {
        mRepository.update(favouritePlace);
    }

    public void delete(FavouritePlace favouritePlace)
    {
        mRepository.delete(favouritePlace);
    }

    public LiveData<List<FavouritePlace>> getFavouritePlaceList()
    {
        return mFavouritePlaceList;
    }
}
