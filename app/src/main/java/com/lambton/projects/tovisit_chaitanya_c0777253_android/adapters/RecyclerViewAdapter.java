package com.lambton.projects.tovisit_chaitanya_c0777253_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lambton.projects.tovisit_chaitanya_c0777253_android.activities.MapsActivity;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.models.FavouritePlace;

import java.util.List;

import com.lambton.projects.tovisit_chaitanya_c0777253_android.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    private List<FavouritePlace> mFavouritePlaceList;
    private Context mContext;
    private DateFormat mDF;

    public FavouritePlace deleteFavouritePlace(int position)
    {
        FavouritePlace favouritePlace = mFavouritePlaceList.remove(position);
        this.notifyItemRemoved(position);
        return favouritePlace;
    }

    public void addFavouritePlaceList(FavouritePlace favouritePlace, int position)
    {
        if(mFavouritePlaceList.size() == position - 1)
        {
            position--;
        }
        mFavouritePlaceList.add(position,favouritePlace);
        this.notifyItemInserted(position);
    }

    public FavouritePlace getFavouritePlace(int position)
    {
        return mFavouritePlaceList.get(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTitleTextView;
        public TextView mSubTitleTextView;
        public LinearLayout mMainLayout;
        public boolean isSection = false;
        public boolean visited = false;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title_textview);
            mSubTitleTextView = itemView.findViewById(R.id.subtitle_textview);
            mMainLayout = itemView.findViewById(R.id.main_layout);
        }
    }

    public RecyclerViewAdapter(List<FavouritePlace> favouritePlaces, Context context)
    {
        this.mFavouritePlaceList = favouritePlaces;
        this.mContext = context;
        mDF = new DateFormat();
    }

    public void setFavouritePlaces(List<FavouritePlace> favouritePlaces)
    {
        this.mFavouritePlaceList = favouritePlaces;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.custom_recycler_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        FavouritePlace favouritePlace = mFavouritePlaceList.get(position);
        holder.visited = favouritePlace.isVisited();
        String title = favouritePlace.getTitle();
        if(title == null || title.isEmpty())
        {
            title = "Created on " + mDF.format("EEE, MM-dd-yyyy hh:mm", favouritePlace.getCreatedDate());
        }
        holder.mTitleTextView.setText(title);
        holder.mSubTitleTextView.setText(favouritePlace.getSubtitle());
        if(favouritePlace.isSection())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                holder.mMainLayout.setBackgroundColor(mContext.getColor(R.color.section));
            }
            else
            {
                holder.mMainLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.section));
            }
            holder.mSubTitleTextView.setVisibility(View.GONE);
            holder.isSection = true;
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                holder.mMainLayout.setBackgroundColor(mContext.getColor(R.color.background));
            }
            else
            {
                holder.mMainLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background));
            }
            holder.mSubTitleTextView.setVisibility(View.VISIBLE);
            holder.isSection = false;
        }
        holder.mMainLayout.setOnClickListener(view ->
        {
            if(!favouritePlace.isSection())
            {
                Intent intent = new Intent(mContext, MapsActivity.class);
                intent.putExtra("favouriteplace",favouritePlace);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if(mFavouritePlaceList == null)
        {
            return 0;
        }
        return mFavouritePlaceList.size();
    }

}
