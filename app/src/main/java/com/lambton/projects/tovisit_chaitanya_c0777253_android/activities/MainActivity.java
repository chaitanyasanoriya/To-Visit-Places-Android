package com.lambton.projects.tovisit_chaitanya_c0777253_android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.R;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.adapters.RecyclerViewAdapter;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.models.FavouritePlace;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.viewmodels.FavouritePlaceViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;
    private TextView mNoFavouritePlacesTextView;
    private FavouritePlaceViewModel mFavouritePlaceViewModel;
    private List<FavouritePlace> mFavouritePlaceList = null;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMemberVariables();
        setupRecyclerView();
    }

    private void setMemberVariables()
    {
        mRecyclerView = findViewById(R.id.recyclerview);
        mNoFavouritePlacesTextView = findViewById(R.id.no_favourite_places_textview);
        mFavouritePlaceViewModel = new ViewModelProvider(this).get(FavouritePlaceViewModel.class);
        mFavouritePlaceViewModel.getFavouritePlaceList().observe(this, favouritePlaces -> MainActivity.this.setFavouritePlaces(favouritePlaces));
//        populate();
    }

    /*private void populate()
    {
        for (int i = 0; i < 5; i++)
        {
            mFavouritePlaceViewModel.insert(new FavouritePlace(String.valueOf(i), String.valueOf(i), 0, 0, new Date(), true,false));
        }
    }*/

    private void setupRecyclerView()
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewAdapter = new RecyclerViewAdapter(mFavouritePlaceList, this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleCallBack);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void setFavouritePlaces(List<FavouritePlace> favouritePlaces)
    {
        mFavouritePlaceList = favouritePlaces;
        if (favouritePlaces != null && favouritePlaces.size() > 0)
        {
            mNoFavouritePlacesTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mFavouritePlaceList.add(0,new FavouritePlace("To Visit place",null,0,0, new Date(),false,true));
            int pos = getVisitedPlaces(favouritePlaces);
            if(pos != favouritePlaces.size())
            {
                mFavouritePlaceList.add(pos,new FavouritePlace("Visited place",null,0,0, new Date(),false,true));
            }

        } else
        {
            mNoFavouritePlacesTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            try
            {
                mFavouritePlaceList.clear();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        mRecyclerViewAdapter.setFavouritePlaces(mFavouritePlaceList);
    }

    private int getVisitedPlaces(List<FavouritePlace> favouritePlaces)
    {
        int n = favouritePlaces.size();
        for(int i=0;i<n;i++)
        {
            if(favouritePlaces.get(i).isVisited())
            {
                return i;
            }
        }
        return n;
    }


    public void addNewClicked(View view)
    {
        Intent intent = new Intent(MainActivity.this,MapsActivity.class);
        startActivity(intent);
    }

//    SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener()
//    {
//        @Override
//        public boolean onQueryTextSubmit(String query)
//        {
//            if(query.isEmpty())
//            {
//                mContactRecyclerViewAdapter.setContacts(mContactList);
//                setNumberOfContacts(mContactList.size());
//            }
//            else
//            {
//                List<Contact> contacts = new ArrayList<>();
//                for(Contact contact: mContactList)
//                {
//                    if(contact.getFirstName().toLowerCase().contains(query.toLowerCase()) || contact.getLastName().toLowerCase().contains(query.toLowerCase()))
//                    {
//                        contacts.add(contact);
//                    }
//                }
//                setNumberOfContacts(contacts.size());
//                mContactRecyclerViewAdapter.setContacts(contacts);
//            }
//            return true;
//        }
//
//        @Override
//        public boolean onQueryTextChange(String newText)
//        {
//            try
//            {
//                List<Contact> contacts = new ArrayList<>();
//                for(Contact contact: mContactList)
//                {
//                    if(contact.getFirstName().toLowerCase().contains(newText.toLowerCase()) || contact.getLastName().toLowerCase().contains(newText.toLowerCase()))
//                    {
//                        contacts.add(contact);
//                    }
//                }
//                setNumberOfContacts(contacts.size());
//                mContactRecyclerViewAdapter.setContacts(contacts);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//
//            return false;
//        }
//    };

    private void setNumberOfFavouritePlaces(int size)
    {
        mNoFavouritePlacesTextView.setText(getString(R.string.number_of_places) + " " + size);
    }

    ItemTouchHelper.SimpleCallback mSimpleCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT)
    {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
        {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {
            int position = viewHolder.getAdapterPosition();
            if(direction == ItemTouchHelper.LEFT)
            {
                FavouritePlace favouritePlace = mRecyclerViewAdapter.deleteFavouritePlace(position);
                mFavouritePlaceViewModel.delete(favouritePlace);
                Snackbar.make(mRecyclerView,"Deleted "+favouritePlace.getTitle(), Snackbar.LENGTH_LONG).setAction("Undo", v ->
                {
                    mRecyclerViewAdapter.addFavouritePlaceList(favouritePlace,position);
                     mFavouritePlaceViewModel.insert(favouritePlace,null);
                }).show();
            }
            else if(direction == ItemTouchHelper.RIGHT)
            {
                FavouritePlace favouritePlace = mRecyclerViewAdapter.getFavouritePlace(position);
                favouritePlace.setVisited(!favouritePlace.isVisited());
                mFavouritePlaceViewModel.update(favouritePlace);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
        {
            String label = "Visited";
            if (viewHolder instanceof RecyclerViewAdapter.ViewHolder)
            {
                RecyclerViewAdapter.ViewHolder viewHolder1 = (RecyclerViewAdapter.ViewHolder) viewHolder;
                if(viewHolder1.visited)
                {
                    label = "To Visit";
                }
                else
                {
                    label = "Visited";
                }
            }
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_outline_24)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .addSwipeRightLabel(label)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimaryDark))
                    .setSwipeRightLabelColor(Color.WHITE)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RecyclerViewAdapter.ViewHolder)
            {
                RecyclerViewAdapter.ViewHolder viewHolder1 = (RecyclerViewAdapter.ViewHolder) viewHolder;
                if(viewHolder1.isSection)
                {
                    return 0;
                }
            }
            return super.getSwipeDirs(recyclerView, viewHolder);
        }
    };

    public void doodleMapClicked(View view)
    {
        Intent intent = new Intent(MainActivity.this,DoodleMapsActivity.class);
        startActivity(intent);
    }
}