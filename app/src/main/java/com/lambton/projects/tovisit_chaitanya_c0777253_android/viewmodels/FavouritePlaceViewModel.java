package com.lambton.projects.tovisit_chaitanya_c0777253_android.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.Polyline;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.callbacks.Callbacks;
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

    public void insert(FavouritePlace favouritePlace, Callbacks callbacks)
    {
        mRepository.insert(favouritePlace, callbacks);
    }

    public void update(FavouritePlace favouritePlace)
    {
        System.out.println("Updating title: "+favouritePlace.getTitle()+" visited: "+favouritePlace.isVisited());
        mRepository.update(favouritePlace);
    }

    public void delete(FavouritePlace favouritePlace)
    {
        if(favouritePlace == null)
        {
            return;
        }
        mRepository.delete(favouritePlace);
    }

    public LiveData<List<FavouritePlace>> getFavouritePlaceList()
    {
        return mFavouritePlaceList;
    }
}
