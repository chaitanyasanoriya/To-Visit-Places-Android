package com.lambton.projects.tovisit_chaitanya_c0777253_android.volley;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GetByVolley {

    public static String[] getDirection(JSONObject jsonObject, GoogleMap googleMap, int strokeColor)
    {
        try
        {
            HashMap<String, String> distances = null;
            VolleyParser directionParser = new VolleyParser();
            distances = directionParser.parseDistance(jsonObject);
            String distance = distances.get("distance");
            String duration = distances.get("duration");
            String [] directionsList = directionParser.parseDirections(jsonObject);
            displayDirections(directionsList, googleMap, strokeColor);
            return new String[] {distance,duration};
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return new String[]{"",""};
    }

    private static void displayDirections(String[] directionsList, GoogleMap googleMap, int strokeColor) {
        Utils.clearPolylines();
        for(String direction: directionsList)
        {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(strokeColor)
                    .width(10)
                    .addAll(PolyUtil.decode(direction)).clickable(true);
            Utils.addPolyline(googleMap.addPolyline(polylineOptions));
        }
    }

    public static void getNearbyPlaces(JSONObject jsonObject, GoogleMap googleMap, int icon)
    {
        List<HashMap<String, String>> nearbyPlaces = null;
        VolleyParser dataParser = new VolleyParser();
        nearbyPlaces = dataParser.parsePlace(jsonObject);
        showNearbyPlaces(nearbyPlaces, googleMap,icon);
    }

    private static void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaces, GoogleMap googleMap, int icon) {
        Utils.clearMarkers();
        for (HashMap<String, String> nearbyPlace: nearbyPlaces)
        {
            String placeName = nearbyPlace.get("place_name");
            String vicinity = nearbyPlace.get("vicinity");
            double lat = Double.parseDouble(nearbyPlace.get("latitude"));
            double lng = Double.parseDouble(nearbyPlace.get("longitude"));
            String reference = nearbyPlace.get("reference");

            LatLng latLng = new LatLng(lat,lng);
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title(placeName)
                    .snippet(vicinity)
                    .icon(BitmapDescriptorFactory.fromResource(icon));
            Utils.addMarker(googleMap.addMarker(options));
        }
    }
}
