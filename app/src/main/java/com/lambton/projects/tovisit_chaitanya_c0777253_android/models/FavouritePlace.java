package com.lambton.projects.tovisit_chaitanya_c0777253_android.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.lambton.projects.tovisit_chaitanya_c0777253_android.typeconverters.ConvertDatatypes;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "favourite_places_table")
public class FavouritePlace implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private long id;

    @Nullable
    private String title;

    @Nullable
    private String subtitle;

    @NonNull
    private double lat;

    @NonNull
    private double lng;

    @NonNull
    @TypeConverters(ConvertDatatypes.class)
    private Date createdDate;

    @NonNull
    private boolean visited;

    private boolean section;

    public FavouritePlace()
    {
    }

    public FavouritePlace(@Nullable String title, @Nullable String subtitle, double lat, double lng, @NonNull Date createdDate, boolean visited, boolean section)
    {
        this.title = title;
        this.subtitle = subtitle;
        this.lat = lat;
        this.lng = lng;
        this.createdDate = createdDate;
        this.visited = visited;
        this.section = section;
    }

    public boolean isSection()
    {
        return section;
    }

    public void setSection(boolean section)
    {
        this.section = section;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    @Nullable
    public String getTitle()
    {
        return title;
    }

    public void setTitle(@Nullable String title)
    {
        this.title = title;
    }

    @Nullable
    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(@Nullable String subtitle)
    {
        this.subtitle = subtitle;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLng()
    {
        return lng;
    }

    public void setLng(double lng)
    {
        this.lng = lng;
    }

    @NonNull
    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(@NonNull Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }
}
