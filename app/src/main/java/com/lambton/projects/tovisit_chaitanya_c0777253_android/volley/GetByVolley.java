package com.lambton.projects.tovisit_chaitanya_c0777253_android.volley;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GetByVolley {

    public static void getDirection(JSONObject jsonObject, GoogleMap googleMap, Location location)
    {
        HashMap<String, String> distances = null;
        VolleyParser directionParser = new VolleyParser();
        distances = directionParser.parseDistance(jsonObject);
        String distance = distances.get("distance");
        String duration = distances.get("duration");
        String[] directionsList = directionParser.parseDirections(jsonObject);
        displayDirections(directionsList, distance, duration, googleMap, location);
    }

    private static void displayDirections(String[] directionsList, String distance, String duration, GoogleMap googleMap, Location location) {
        googleMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title("Duration: "+ duration)
                .snippet("Distance: "+distance)
                .draggable(true);
        googleMap.addMarker(options);
        for(String direction: directionsList)
        {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(direction));
            googleMap.addPolyline(polylineOptions);
        }
    }

    public static void getNearbyPlaces(JSONObject jsonObject, GoogleMap googleMap)
    {
        List<HashMap<String, String>> nearbyPlaces = null;
        VolleyParser dataParser = new VolleyParser();
        nearbyPlaces = dataParser.parsePlace(jsonObject);
        showNearbyPlaces(nearbyPlaces, googleMap);
    }

    private static void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaces, GoogleMap googleMap) {
        googleMap.clear();
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
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            googleMap.addMarker(options);
        }
    }
}
