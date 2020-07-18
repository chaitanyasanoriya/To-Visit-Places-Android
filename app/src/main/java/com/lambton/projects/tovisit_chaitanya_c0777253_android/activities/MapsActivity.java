package com.lambton.projects.tovisit_chaitanya_c0777253_android.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.R;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.Utils;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.callbacks.Callbacks;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.models.FavouritePlace;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.viewmodels.FavouritePlaceViewModel;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.volley.GetByVolley;
import com.lambton.projects.tovisit_chaitanya_c0777253_android.volley.VolleySingleton;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Callbacks
{

    private static final int REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM_LEVEL = 14f;
    private static final int RADIUS = 2500;
    private static final String STARTUP_PLACES = "cafe";

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private BottomNavigationView mBottomNavigationView;
    private Marker mMarker;
    private String mTitle = "", mSubTitle = "";
    private int mIcon = R.mipmap.cafe_marker;
    private FavouritePlace mFavouritePlace = null;
    private FavouritePlaceViewModel mFavouritePlaceViewModel;
    private EditText mSearchEditText;
    private int mMapStyle;
    private int mSelectedSort = R.id.standard_radio;
    private int mMapType = GoogleMap.MAP_TYPE_NORMAL;
    private int mSelectedType = R.id.normal_radio;
    private int mStrokeColor = Color.RED;
    private String mMode = "driving";
    private ToggleButton mToggleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setMemberVariables();
        checkPermissions();
    }

    private void setMemberVariables()
    {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mBottomNavigationView = findViewById(R.id.botton_navigation_view);
        mFavouritePlaceViewModel = new ViewModelProvider(this).get(FavouritePlaceViewModel.class);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSearchEditText = findViewById(R.id.search_edittext);
        mToggleButton = findViewById(R.id.toggle_btn);
        mMapStyle = R.raw.standard;
    }

    /**
     * Method that checks User Location access and request permission
     */
    private void checkPermissions()
    {
        if (!hasLocationPermission())
        {
            requestLocationPermission();
        }
    }

    /**
     * Method to request Location Permission
     */
    private void requestLocationPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    /**
     * Method to check if the app has User Location Permission
     * @return - True if the app has User Location Permission
     */
    private boolean hasLocationPermission()
    {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (mMap != null)
                {
                    enableUserLocationAndZoom();
                }
            }
        }
    }

    /**
     * Method to show User Location on Map and Zoom onto it
     */
    @SuppressLint("MissingPermission")
    private void enableUserLocationAndZoom()
    {
        mMap.setMyLocationEnabled(true);
        Location location = getCurrentLocation();
        if (location != null)
        {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL));
//            showLaunchNearbyPlaces(latLng,STARTUP_PLACES);
        }
    }

    @SuppressLint("MissingPermission")
    private Location getCurrentLocation()
    {
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
        setUISettings();
        setOnClickListeners();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestLocationPermission();
            return;
        }
        enableUserLocationAndZoom();
    }

    private void setOnClickListeners()
    {
        mMap.setOnInfoWindowClickListener(mOnInfoWindowClickListener);
        mMap.setOnInfoWindowLongClickListener(mOnInfoWindowLongClickListener);
        mMap.setOnMapLongClickListener(mOnMapLongClickListener);
        mMap.setOnMarkerClickListener(mOnMarkerClickListener);
    }

    private void setUISettings()
    {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this,mMapStyle));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    public void backClicked(View view)
    {
        Intent intent = new Intent(MapsActivity.this,MainActivity.class);
        startActivity(intent);
//        finish();
    }

    public void showLaunchNearbyPlaces(LatLng latLng, String placeType)
    {
        String url = getPlaceURL(latLng.latitude,latLng.longitude,placeType);
        showNearbyPlaces(url);
    }

    private String getPlaceURL(double latitude, double longitude, String placeType) {
        StringBuilder URL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        URL.append("location="+latitude + ","+ longitude);
        URL.append("&radius="+RADIUS);
        URL.append("&type="+placeType);
        URL.append("&key="+getString(R.string.google_maps_key));
        System.out.println(URL);
        return URL.toString();
    }

    private void showNearbyPlaces(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> GetByVolley.getNearbyPlaces(response,mMap,mIcon),null);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @SuppressLint("MissingPermission")
    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            switch (item.getItemId())
            {
                case R.id.cafe:
                    mIcon = R.mipmap.cafe_marker;
                    break;
                case R.id.hospital:
                    mIcon = R.mipmap.hospital_marker;
                    break;
                case R.id.restaurant:
                    mIcon = R.mipmap.restaurant_marker;
                    break;
                case R.id.museum:
                    mIcon = R.mipmap.museum_marker;
                    break;
                case R.id.police:
                    mIcon = R.mipmap.police_marker;
                    break;
            }
            String url = getPlaceURL(location.getLatitude(),location.getLongitude(), item.getTitle().toString().toLowerCase());
            showNearbyPlaces(url);
            return true;
        }
    };

    GoogleMap.OnMapLongClickListener mOnMapLongClickListener = latLng -> addMarker(latLng);

    private void addMarker(LatLng latLng)
    {
        Utils.clearPolylines();
        if(mMarker != null)
        {
            Object object = mMarker.getTag();
            if(object != null && ((String) object).equals("custom"))
            {
                mMarker.remove();
            }
        }
        mFavouritePlace = null;
        MarkerOptions markerOptions = createMarkerOptions(latLng);
        mMarker = mMap.addMarker(markerOptions);
        mMarker.setTag("custom");
        setInfoAsync(latLng ,mMarker);
    }

    private void setInfoAsync(LatLng latLng,Marker mMarker)
    {
        new Thread(() ->
        {
            try
            {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                Address address = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1).get(0);
                String [] strings = Utils.getFormattedAddress(address);
                if(!strings[0].isEmpty())
                {
                    MapsActivity.this.runOnUiThread(() ->
                    {
                        mTitle = strings[0];
                        mSubTitle = strings[1];
                        setInfo(strings[0],strings[1],mMarker);
                    });
                }
                else
                {
                    MapsActivity.this.runOnUiThread(() -> setInfo(Utils.getFormattedDate(),"",mMarker));
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                MapsActivity.this.runOnUiThread(() -> setInfo(Utils.getFormattedDate(),"",mMarker));
            }
        }).start();
    }

    private void setInfo(String title, String snippet, Marker mMarker)
    {
        System.out.println("title: "+title+" snippet: "+snippet);
        mMarker.setTitle(title);
        mMarker.setSnippet(snippet);
        mMarker.showInfoWindow();
    }

    private MarkerOptions createMarkerOptions(LatLng latLng)
    {
        return new MarkerOptions().position(latLng).draggable(true);
    }

    GoogleMap.OnInfoWindowClickListener mOnInfoWindowClickListener = marker ->
    {
        Object object = marker.getTag();
        if(mFavouritePlace == null)
        {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.star_map));
            if(object != null && ((String) object).equals("custom"))
            {
                mFavouritePlace = new FavouritePlace(marker.getTitle(),marker.getSnippet(),marker.getPosition().latitude,marker.getPosition().longitude,new Date(),false,false);
                mFavouritePlaceViewModel.insert(mFavouritePlace, this);
            }
            else
            {
                addToFavourite(marker);
            }
        }
        else
        {
            showDeleteFavouritePlace(marker);
        }
    };

    private void addToFavourite(Marker marker)
    {
        LatLng latLng = marker.getPosition();
        new Thread(() ->
        {
            try
            {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                Address address = geocoder.getFromLocation(latLng.latitude,latLng.latitude,1).get(0);
                String [] strings = Utils.getFormattedAddress(address);
                MapsActivity.this.runOnUiThread(() ->
                {
                    FavouritePlace favouritePlace = new FavouritePlace(strings[0],strings[1],marker.getPosition().latitude,marker.getPosition().longitude,new Date(),false,false);
                    mFavouritePlaceViewModel.insert(favouritePlace, this);
                    mFavouritePlace = favouritePlace;
                });
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    private void showDeleteFavouritePlace(Marker marker)
    {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, which) -> deletePlace(marker))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    private void deletePlace(Marker marker)
    {
        mFavouritePlaceViewModel.delete(mFavouritePlace);
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mFavouritePlace = null;
    }

    GoogleMap.OnInfoWindowLongClickListener mOnInfoWindowLongClickListener = marker ->
    {
        marker.hideInfoWindow();
        System.out.println(1);
        if(mFavouritePlace != null)
        {
            String snippet = marker.getSnippet();
            if(snippet != null && snippet.isEmpty())
            {
                snippet = "";
            }
            if(mFavouritePlace.isVisited())
            {
                mFavouritePlace.setVisited(false);
                marker.setSnippet(snippet.replace(" (Visited)",""));
            }
            else
            {
                mFavouritePlace.setVisited(true);
                marker.setSnippet(snippet+" (Visited)");
            }
            mFavouritePlaceViewModel.update(mFavouritePlace);
        }
        marker.showInfoWindow();
    };



    public void searchAddressClicked(View view)
    {
        String text = mSearchEditText.getText().toString().trim();
        if(!text.isEmpty())
        {
            try
            {
                Geocoder geocoder = new Geocoder(this);
                Location location = getCurrentLocation();
                System.out.println(1);
                Address address = geocoder.getFromLocationName(text,1,location.getLatitude() - 1, location.getLongitude() - 1, location.getLatitude() + 1, location.getLongitude() + 1).get(0);
                System.out.println(2);
                MarkerOptions markerOptions = createMarkerOptions(new LatLng(address.getLatitude(),address.getLongitude()));
                System.out.println(3);
                String [] strings = Utils.getFormattedAddress(address);
                if(strings[0].isEmpty())
                {
                    markerOptions.title(Utils.getFormattedDate());
                }
                else
                {
                    markerOptions.title(strings[0]);
                }
                markerOptions.snippet(strings[1]);
                if(mMarker != null)
                {
                    mMarker.remove();
                }
                mFavouritePlace = null;
                mMarker = mMap.addMarker(markerOptions);
                mMarker.setTag("custom");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void showMapStyleAlert(View view)
    {
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_map_style, null);

        RadioGroup group = dialogView.findViewById(R.id.radio_group);
        group.check(mSelectedSort);
        group.setOnCheckedChangeListener((group1, checkedId) ->
        {
            switch (checkedId)
            {
                case R.id.standard_radio:
                    updateMap(R.raw.standard);
                    mSelectedSort = R.id.standard_radio;
                    break;
                case R.id.retro_radio:
                    updateMap(R.raw.retro);
                    mSelectedSort = R.id.retro_radio;
                    break;
                case R.id.dark_radio:
                    updateMap(R.raw.dark);
                    mSelectedSort = R.id.dark_radio;
                    break;
                case R.id.night_radio:
                    updateMap(R.raw.night);
                    mSelectedSort = R.id.night_radio;
                    break;
                case R.id.aubergine_radio:
                    updateMap(R.raw.aubergine);
                    mSelectedSort = R.id.aubergine_radio;
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();
    }

    private void updateMap(int res)
    {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this,res));
    }


    public void showMapTypeAlert(View view)
    {
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_map_type, null);

        RadioGroup group = dialogView.findViewById(R.id.radio_group);
        group.check(mSelectedType);
        group.setOnCheckedChangeListener((group1, checkedId) ->
        {
            switch (checkedId)
            {
                case R.id.normal_radio:
                    updateMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mSelectedType = R.id.normal_radio;
                    break;
                case R.id.hybrid_radio:
                    updateMapType(GoogleMap.MAP_TYPE_HYBRID);
                    mSelectedType = R.id.hybrid_radio;
                    break;
                case R.id.satellite_radio:
                    updateMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mSelectedType = R.id.satellite_radio;
                    break;
                case R.id.terrain_radio:
                    updateMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    mSelectedType = R.id.terrain_radio;
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();
    }

    private void updateMapType(int res)
    {
        mMap.setMapType(res);
    }

    GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener()
    {
        @Override
        public boolean onMarkerClick(Marker marker)
        {

            if(mMarker != null)
            {
                Object object = mMarker.getTag();
                if(object != null &&((String) object).equals("custom"))
                {
                    mMarker.remove();
                }
            }
            mMarker = marker;
            mFavouritePlace = null;
            marker.showInfoWindow();
            return true;
        }
    };

    public void navigateClicked(View view)
    {
        LatLng latLng = null;
        String title = "";
        String snippet = "";
        if(mFavouritePlace != null)
        {
            latLng = new LatLng(mFavouritePlace.getLat(),mFavouritePlace.getLng());
            title = mFavouritePlace.getTitle();
            snippet = mFavouritePlace.getSubtitle();
        }
        else if(mMarker != null)
        {
            latLng = mMarker.getPosition();
            title = mMarker.getTitle();
            snippet = mMarker.getSnippet();
        }

        if(title == null || title.isEmpty())
        {
            title = Utils.getFormattedDate();
        }

        if(latLng == null)
        {
            Utils.showError("Destination not selected","Please select a destination to navigate to",MapsActivity.this);
        }
        else
        {
            LatLng finalLatLng = latLng;
            String finalTitle = title;
            String finalSnippet = snippet;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getDirectionURL(latLng), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String [] strings = GetByVolley.getDirection(response,mMap, finalLatLng,mStrokeColor, finalTitle, finalSnippet);
                }
            },null);
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    private String getDirectionURL(LatLng latLng) {
        Location userlocation = getCurrentLocation();
        StringBuilder URL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        URL.append("origin="+userlocation.getLatitude() + ","+ userlocation.getLongitude());
        URL.append("&destination="+latLng.latitude+","+latLng.longitude);
        URL.append("&key="+getString(R.string.google_maps_key));
        URL.append("&mode="+mMode);
        System.out.println(URL);
        return URL.toString();
    }

    public void modeClicked(View view)
    {
        if(mToggleButton.isChecked())
        {
            mStrokeColor = Color.GREEN;
            mMode = "walking";
        }
        else
        {
            mStrokeColor = Color.RED;
            mMode = "driving";
        }
    }

    @Override
    public void inserted(Long id)
    {
        if(mFavouritePlace != null)
        {
            mFavouritePlace.setId(id);
        }
    }

    GoogleMap.OnPolylineClickListener mOnPolylineClickListener = new GoogleMap.OnPolylineClickListener()
    {
        @Override
        public void onPolylineClick(Polyline polyline)
        {

        }
    };
}