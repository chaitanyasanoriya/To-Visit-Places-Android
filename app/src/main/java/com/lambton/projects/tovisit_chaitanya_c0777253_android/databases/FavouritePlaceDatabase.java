package com.lambton.projects.tovisit_chaitanya_c0777253_android.databases;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.lambton.projects.tovisit_chaitanya_c0777253_android.dao.FavouritePlaceDao;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.models.FavouritePlace;

import java.util.Date;

@Database(entities = {FavouritePlace.class}, version = 2)
public abstract class FavouritePlaceDatabase extends RoomDatabase
{
    private static FavouritePlaceDatabase mInstance;
    public abstract FavouritePlaceDao mFavouritePlaceDao();

    public static synchronized FavouritePlaceDatabase getInstance(Context context)
    {
        System.out.println("getInstance");
        if(mInstance == null)
        {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    FavouritePlaceDatabase.class, "favourite_place_database")
                    .fallbackToDestructiveMigration()
//                    .addCallback(roomCallBack)
                    .build();
        }
        return mInstance;
    }

    /*private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            super.onCreate(db);
            System.out.println("callback");
            new PopulateDBAsyncTask(mInstance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void,Void,Void>
    {
        private FavouritePlaceDao favouritePlaceDao;

        private PopulateDBAsyncTask(FavouritePlaceDatabase db)
        {
            favouritePlaceDao = db.mFavouritePlaceDao();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            System.out.println("adding");
            favouritePlaceDao.insert(new FavouritePlace("hello","world",0,0,new Date(),true,false));
            favouritePlaceDao.insert(new FavouritePlace("test","123",0,0,new Date(),false,false));
            return null;
        }
    }*/
}
