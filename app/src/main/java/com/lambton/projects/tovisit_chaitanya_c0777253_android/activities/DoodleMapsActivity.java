package com.lambton.projects.tovisit_chaitanya_c0777253_android.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.R;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.Utils;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.volley.GetByVolley;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.volley.VolleySingleton;

import java.io.IOException;

public class DoodleMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnPolylineClickListener
{
    private GoogleMap mMap;
    private Marker mMarker1, mMarker2;
    private String mMode = "driving";
    private ToggleButton mToggleButton;
    private int mStrokeColor = Color.RED;
    private String[] mInfoString;
    private Marker mInfoMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mToggleButton = findViewById(R.id.toggle_btn);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnPolylineClickListener(this);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.6426,-79.3871),10));
        if(isDarkMode())
        {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DoodleMapsActivity.this, R.raw.night));
        }
        else
        {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DoodleMapsActivity.this, R.raw.standard));
        }
    }

    public void backClicked(View view)
    {
        finish();
    }

    public void navigateClicked(View view)
    {
        clearInfoMarker();
        LatLng latLng1 = mMarker1.getPosition();
        LatLng latLng2 = mMarker2.getPosition();
        if (latLng1 == null || latLng2 == null)
        {
            Utils.showError("Markers not set", "Please place marker to get directions", DoodleMapsActivity.this);
        } else
        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Utils.getDirectionURL(latLng1,latLng2,DoodleMapsActivity.this,mMode), null, response -> mInfoString = GetByVolley.getDirection(response, mMap, mStrokeColor), null);
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        Utils.clearPolylines();
        if(mMarker2 != null)
        {
            mMarker2.remove();
        }
        clearInfoMarker();
        mMarker2 = mMarker1;
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
        mMarker1 = mMap.addMarker(markerOptions);
        setInfoAsync(latLng,mMarker1);
    }

    private void clearInfoMarker()
    {
        if(mInfoMarker != null)
        {
            mInfoMarker.remove();
            mInfoMarker = null;
        }
    }

    private void setInfoAsync(LatLng latLng, Marker mMarker)
    {
        new Thread(() ->
        {
            try
            {
                Geocoder geocoder = new Geocoder(DoodleMapsActivity.this);
                Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                String[] strings = Utils.getFormattedAddress(address);
                if (!strings[0].isEmpty())
                {
                    DoodleMapsActivity.this.runOnUiThread(() ->
                    {
                        setInfo(strings[0], strings[1], mMarker);
                    });
                } else
                {
                    DoodleMapsActivity.this.runOnUiThread(() -> setInfo(Utils.getFormattedDate(), "", mMarker));
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                DoodleMapsActivity.this.runOnUiThread(() -> setInfo(Utils.getFormattedDate(), "", mMarker));
            }
        }).start();
    }

    private void setInfo(String title, String snippet, Marker marker)
    {
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.showInfoWindow();
    }

    public void modeClicked(View view)
    {
        if (mToggleButton.isChecked())
        {
            mStrokeColor = Color.GREEN;
            mMode = "walking";
        } else
        {
            mStrokeColor = Color.RED;
            mMode = "driving";
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline)
    {
        LatLng place1 = polyline.getPoints().get(0);
        LatLng place2 = polyline.getPoints().get(1);
        LatLng mid_point = Utils.midPoint(place1.latitude, place1.longitude, place2.latitude, place2.longitude);
        showDistanceMarker(mid_point, mInfoString[0], mInfoString[1]);
    }

    /**
     * Method to show Marker at Location will distance and snippet
     *
     * @param latLng   - Location of the Marker
     * @param distance - Distance to be displayed
     * @param snippet  - Snippet to be displayed
     */
    private void showDistanceMarker(LatLng latLng, String distance, String snippet)
    {
        if (mInfoMarker != null)
        {
            mInfoMarker.remove();
        }

        BitmapDescriptor transparent = BitmapDescriptorFactory.fromResource(R.mipmap.transparent);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(distance)
                .snippet(snippet)
                .icon(transparent); //puts the info window on the polyline

        mInfoMarker = mMap.addMarker(options);
        mInfoMarker.showInfoWindow();
    }

    private boolean isDarkMode()
    {
        return (this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}