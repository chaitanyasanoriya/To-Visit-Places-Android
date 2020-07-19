package com.lambton.projects.tovisit_chaitanya_c0777253_android.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.lambton.projects.tovisit_chaitanya_c0777253_android.callbacks.Callbacks;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.dao.FavouritePlaceDao;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.databases.FavouritePlaceDatabase;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.models.FavouritePlace;

import java.util.List;

public class FavouritePlaceRepository
{
    private FavouritePlaceDao mFavouritePlaceDao;
    private LiveData<List<FavouritePlace>> mFavouritePlaceList;

    public FavouritePlaceRepository(Application application)
    {
        FavouritePlaceDatabase db = FavouritePlaceDatabase.getInstance(application);
        mFavouritePlaceDao = db.mFavouritePlaceDao();
        mFavouritePlaceList = mFavouritePlaceDao.getAllFavouritePlaces();
    }

    public void insert(FavouritePlace favouritePlace, Callbacks callbacks)
    {
        new FavouritePlaceRepository.InsertCarsAsyncTask(mFavouritePlaceDao, callbacks).execute(favouritePlace);
    }

    public void update(FavouritePlace favouritePlace)
    {
        new FavouritePlaceRepository.UpdateCarsAsyncTask(mFavouritePlaceDao).execute(favouritePlace);
    }

    public void delete(FavouritePlace favouritePlace)
    {
        new FavouritePlaceRepository.DeleteCarsAsyncTask(mFavouritePlaceDao).execute(favouritePlace);
    }

    public LiveData<List<FavouritePlace>> getFavouritePlaceList()
    {
        return mFavouritePlaceList;
    }

    private static class InsertCarsAsyncTask extends AsyncTask<FavouritePlace, Void, Long>
    {
        private FavouritePlaceDao mFavouritePlaceDao;
        private Callbacks mCallbacks;

        private InsertCarsAsyncTask(FavouritePlaceDao mFavouritePlaceDao, Callbacks callbacks)
        {
            this.mFavouritePlaceDao = mFavouritePlaceDao;
            this.mCallbacks = callbacks;
        }

        @Override
        protected Long doInBackground(FavouritePlace ... favouritePlaces)
        {
            return mFavouritePlaceDao.insert(favouritePlaces[0]);
        }

        @Override
        protected void onPostExecute(Long id)
        {
            if(mCallbacks != null)
            {
                mCallbacks.inserted(id);
            }
            super.onPostExecute(id);
        }
    }

    private static class UpdateCarsAsyncTask extends AsyncTask<FavouritePlace, Void, Void>
    {
        private FavouritePlaceDao mFavouritePlaceDao;

        private UpdateCarsAsyncTask(FavouritePlaceDao mFavouritePlaceDao)
        {
            this.mFavouritePlaceDao = mFavouritePlaceDao;
        }

        @Override
        protected Void doInBackground(FavouritePlace ... favouritePlaces)
        {
            mFavouritePlaceDao.update(favouritePlaces[0]);
            return null;
        }
    }

    private static class DeleteCarsAsyncTask extends AsyncTask<FavouritePlace, Void, Void>
    {
        private FavouritePlaceDao mFavouritePlaceDao;

        private DeleteCarsAsyncTask(FavouritePlaceDao mFavouritePlaceDao)
        {
            this.mFavouritePlaceDao = mFavouritePlaceDao;
        }

        @Override
        protected Void doInBackground(FavouritePlace ... favouritePlaces)
        {
            mFavouritePlaceDao.delete(favouritePlaces[0]);
            return null;
        }
    }

}
