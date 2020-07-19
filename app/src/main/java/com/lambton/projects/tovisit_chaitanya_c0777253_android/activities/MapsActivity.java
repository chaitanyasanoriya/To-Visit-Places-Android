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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Callbacks
{

    private static final int REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM_LEVEL = 13.25f;
    private static final int RADIUS = 2500;
    private static final String STARTUP_PLACES = "cafe";
    private static final String VISITED_TEXT = " (Visited)";

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private BottomNavigationView mBottomNavigationView;
    private Marker mMarker;
    private int mIcon = R.mipmap.cafe_marker;
    private FavouritePlace mFavouritePlace = null;
    private FavouritePlaceViewModel mFavouritePlaceViewModel;
    private EditText mSearchEditText;
    private int mMapStyle;
    private int mSelectedSort = R.id.standard_radio;
    private int mSelectedType = R.id.normal_radio;
    private int mStrokeColor = Color.RED;
    private String mMode = "driving";
    private ToggleButton mToggleButton;
    private Marker mInfoMarker;
    private String[] mInfoString;
    private List<FavouritePlace> mFavouritePlaceList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setMemberVariables();
        detectDarkMode();
        checkPermissions();
    }

    private void detectDarkMode()
    {
        int currentNightMode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode)
        {
            case Configuration.UI_MODE_NIGHT_NO:
                mMapStyle = R.raw.standard;
                mSelectedSort = R.id.standard_radio;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                mMapStyle = R.raw.night;
                mSelectedSort = R.id.night_radio;
                break;
        }
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
        mFavouritePlaceViewModel.getFavouritePlaceList().observe(this, favouritePlaces -> mFavouritePlaceList = favouritePlaces);
        mFavouritePlace = (FavouritePlace) getIntent().getSerializableExtra("favouriteplace");
        mSearchEditText.setOnKeyListener(mOnKeyListener);
    }

    private void addSelectedMarker()
    {
        if (mFavouritePlace == null)
        {
            return;
        }
        MarkerOptions markerOptions = createMarkerOptions(new LatLng(mFavouritePlace.getLat(), mFavouritePlace.getLng()));
        markerOptions = markerOptions.title(mFavouritePlace.getTitle()).snippet(mFavouritePlace.getSubtitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.star_map));
        mMarker = mMap.addMarker(markerOptions);
        mMarker.setTag("custom");
        if (mFavouritePlace.isVisited())
        {
            mMarker.setSnippet(mMarker.getSnippet() + VISITED_TEXT);
        }
        mMarker.showInfoWindow();
        zoomToMarker(mMarker);
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
     *
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
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL));
            showLaunchNearbyPlaces(latLng, STARTUP_PLACES);
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
        addSelectedMarker();
    }

    private void setOnClickListeners()
    {
        mMap.setOnInfoWindowClickListener(mOnInfoWindowClickListener);
        mMap.setOnInfoWindowLongClickListener(mOnInfoWindowLongClickListener);
        mMap.setOnMapLongClickListener(mOnMapLongClickListener);
        mMap.setOnMarkerClickListener(mOnMarkerClickListener);
        mMap.setOnPolylineClickListener(mOnPolylineClickListener);
        mMap.setOnMarkerDragListener(mOnMarkerDragListener);
    }

    private void setUISettings()
    {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, mMapStyle));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    public void backClicked(View view)
    {
        /*Intent intent = new Intent(MapsActivity.this,MainActivity.class);
        startActivity(intent);*/
        finish();
    }

    public void showLaunchNearbyPlaces(LatLng latLng, String placeType)
    {
        String url = getPlaceURL(latLng.latitude, latLng.longitude, placeType);
        showNearbyPlaces(url);
    }

    private String getPlaceURL(double latitude, double longitude, String placeType)
    {
        StringBuilder URL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        URL.append("location=" + latitude + "," + longitude);
        URL.append("&radius=" + RADIUS);
        URL.append("&type=" + placeType);
        URL.append("&key=" + getString(R.string.google_maps_key));
        return URL.toString();
    }

    private void showNearbyPlaces(String url)
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> GetByVolley.getNearbyPlaces(response, mMap, mIcon), null);
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
            String url = getPlaceURL(location.getLatitude(), location.getLongitude(), item.getTitle().toString().toLowerCase());
            showNearbyPlaces(url);
            return true;
        }
    };

    GoogleMap.OnMapLongClickListener mOnMapLongClickListener = latLng -> addMarker(latLng);

    private void addMarker(LatLng latLng)
    {
        Utils.clearPolylines();
        if (mMarker != null)
        {
            Object object = mMarker.getTag();
            if (object != null && ((String) object).equals("custom"))
            {
                mMarker.remove();
            }
        }
        mFavouritePlace = null;
        MarkerOptions markerOptions = createMarkerOptions(latLng);
        mMarker = mMap.addMarker(markerOptions);
        mMarker.setTag("custom");
        zoomToMarker(mMarker);
        setInfoAsync(latLng, mMarker);
    }

    private void setInfoAsync(LatLng latLng, Marker mMarker)
    {
        new Thread(() ->
        {
            try
            {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                String[] strings = Utils.getFormattedAddress(address);
                if (!strings[0].isEmpty())
                {
                    MapsActivity.this.runOnUiThread(() ->
                    {
                        setInfo(strings[0], strings[1], mMarker);
                    });
                } else
                {
                    MapsActivity.this.runOnUiThread(() -> setInfo(Utils.getFormattedDate(), "", mMarker));
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                MapsActivity.this.runOnUiThread(() -> setInfo(Utils.getFormattedDate(), "", mMarker));
            }
        }).start();
    }

    private void setInfo(String title, String snippet, Marker mMarker)
    {
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
        if (isSameAsFavouritePlace(mFavouritePlace, marker))
        {
            showDeleteFavouritePlace(marker);
        } else
        {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.star_map));
            String snippet = marker.getSnippet();
            if (snippet == null)
            {
                snippet = "";
            }
            mFavouritePlace = new FavouritePlace(marker.getTitle(), snippet, marker.getPosition().latitude, marker.getPosition().longitude, new Date(), false, false);
            mFavouritePlaceViewModel.insert(mFavouritePlace, this);
        }
    };

    private boolean isSameAsFavouritePlace(FavouritePlace favouritePlace, Marker marker)
    {
        if (favouritePlace != null && favouritePlace.getTitle().equals(marker.getTitle()) && favouritePlace.getSubtitle().equals(marker.getSnippet().replace(VISITED_TEXT, "")) && favouritePlace.getLat() == marker.getPosition().latitude && favouritePlace.getLng() == marker.getPosition().longitude)
        {
            return true;
        }
        return false;
    }

    private void updateFavourite(Marker marker, FavouritePlace favouritePlace)
    {
        LatLng latLng = marker.getPosition();
        new Thread(() ->
        {
            try
            {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                String[] strings = Utils.getFormattedAddress(address);
                if (strings[0] == null || strings[0].isEmpty())
                {
                    strings[0] = Utils.getFormattedDate();
                }
                MapsActivity.this.runOnUiThread(() ->
                {
                    marker.hideInfoWindow();
                    favouritePlace.setTitle(strings[0]);
                    favouritePlace.setSubtitle(strings[1]);
                    favouritePlace.setLat(latLng.latitude);
                    favouritePlace.setLng(latLng.longitude);
                    mFavouritePlaceViewModel.update(favouritePlace);
                    mFavouritePlace = favouritePlace;
                    marker.setTitle(strings[0]);
                    marker.setSnippet(strings[1]);
                    setMarkerVisitedStatus(marker, favouritePlace);
                    marker.showInfoWindow();
                });
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    private void setMarkerVisitedStatus(Marker marker, FavouritePlace favouritePlace)
    {
        if (favouritePlace.isVisited())
        {
            marker.setSnippet(marker.getSnippet() + VISITED_TEXT);
        } else
        {
            marker.setSnippet(marker.getSnippet().replace(VISITED_TEXT, ""));
        }
    }

    private void showDeleteFavouritePlace(Marker marker)
    {
        new AlertDialog.Builder(this)
                .setTitle("Delete Favorite Place")
                .setMessage("Are you sure you want to delete this favourite place?")
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
        FavouritePlace favouritePlace = getFavouritePlaceAssociated(marker);
        if (favouritePlace != null)
        {
            String snippet = marker.getSnippet();
            if (snippet != null && snippet.isEmpty())
            {
                snippet = "";
            }
            if (favouritePlace.isVisited())
            {
                favouritePlace.setVisited(false);
                marker.setSnippet(snippet.replace(VISITED_TEXT, ""));
            } else
            {
                favouritePlace.setVisited(true);
                marker.setSnippet(snippet + VISITED_TEXT);
            }
            mFavouritePlaceViewModel.update(favouritePlace);
        }
        marker.showInfoWindow();
    };

    private FavouritePlace getFavouritePlaceAssociated(Marker marker)
    {
        for (FavouritePlace favouritePlace : mFavouritePlaceList)
        {
            if (isSameAsFavouritePlace(favouritePlace, marker))
            {
                return favouritePlace;
            }
        }
        return null;
    }


    public void searchAddressClicked(View view)
    {
        String text = mSearchEditText.getText().toString().trim();
        if (!text.isEmpty())
        {
            try
            {
                Geocoder geocoder = new Geocoder(this);
                Location location = getCurrentLocation();
                Address address = geocoder.getFromLocationName(text, 1, location.getLatitude() - 1, location.getLongitude() - 1, location.getLatitude() + 1, location.getLongitude() + 1).get(0);
                String[] strings = Utils.getFormattedAddress(address);
                MarkerOptions markerOptions = createMarkerOptions(new LatLng(address.getLatitude(), address.getLongitude()));
                if (strings[0].isEmpty())
                {
                    markerOptions.title(Utils.getFormattedDate());
                } else
                {
                    markerOptions.title(strings[0]);
                }
                markerOptions.snippet(strings[1]);
                if (mMarker != null)
                {
                    mMarker.remove();
                }
                mFavouritePlace = null;
                mMarker = mMap.addMarker(markerOptions);
                mMarker.setTag("custom");
                mMarker.showInfoWindow();
                zoomToMarker(mMarker);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void zoomToMarker(Marker marker)
    {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), DEFAULT_ZOOM_LEVEL));
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
                    Utils.setOriginalMarker(mIcon,mMap);
                    break;
                case R.id.retro_radio:
                    updateMap(R.raw.retro);
                    mSelectedSort = R.id.retro_radio;
                    Utils.setOriginalMarker(mIcon,mMap);
                    break;
                case R.id.dark_radio:
                    updateMap(R.raw.dark);
                    mSelectedSort = R.id.dark_radio;
                    Utils.setLightMarkers(this,mMap,mIcon);
                    break;
                case R.id.night_radio:
                    updateMap(R.raw.night);
                    mSelectedSort = R.id.night_radio;
                    Utils.setLightMarkers(this,mMap,mIcon);
                    break;
                case R.id.aubergine_radio:
                    updateMap(R.raw.aubergine);
                    mSelectedSort = R.id.aubergine_radio;
                    Utils.setLightMarkers(this,mMap,mIcon);
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();
    }

    private void updateMap(int res)
    {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, res));
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
            mMarker = marker;
            marker.showInfoWindow();
            return true;
        }
    };

    private boolean isNotSameMarker(Marker marker)
    {
        boolean cond1 = mMarker.getTitle().equals(marker.getTitle());
        boolean cond2 = true;
        if (mMarker.getSnippet() != null && marker.getSnippet() != null)
        {
            cond2 = mMarker.getSnippet().equals(marker.getSnippet());
        }
        boolean cond3 = marker.getPosition().latitude == mMarker.getPosition().latitude;
        boolean cond4 = marker.getPosition().longitude == mMarker.getPosition().longitude;
        if (cond1 && cond2 && cond3 && cond4)
        {
            return false;
        }
        return true;
    }

    public void navigateClicked(View view)
    {
        LatLng latLng = null;
        if (mMarker != null)
        {
            latLng = mMarker.getPosition();
        }

        if (latLng == null)
        {
            Utils.showError("Destination not selected", "Please select a destination to navigate to", MapsActivity.this);
        } else
        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Utils.getDirectionURL(getCurrentLocation(), latLng, this, mMode), null, response -> mInfoString = GetByVolley.getDirection(response, mMap, mStrokeColor), null);
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
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
    public void inserted(Long id)
    {
        if (mFavouritePlace != null)
        {
            mFavouritePlace.setId(id);
        }
    }

    GoogleMap.OnPolylineClickListener mOnPolylineClickListener = new GoogleMap.OnPolylineClickListener()
    {
        @Override
        public void onPolylineClick(Polyline polyline)
        {
            LatLng place1 = polyline.getPoints().get(0);
            LatLng place2 = polyline.getPoints().get(1);
            LatLng mid_point = Utils.midPoint(place1.latitude, place1.longitude, place2.latitude, place2.longitude);
            showDistanceMarker(mid_point, mInfoString[0], mInfoString[1]);
        }
    };

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

    private GoogleMap.OnMarkerDragListener mOnMarkerDragListener = new GoogleMap.OnMarkerDragListener()
    {
        @Override
        public void onMarkerDragStart(Marker marker)
        {
        }

        @Override
        public void onMarkerDrag(Marker marker)
        {

        }

        @Override
        public void onMarkerDragEnd(Marker marker)
        {
            Utils.clearPolylines();
            if (mFavouritePlace != null)
            {
                updateFavourite(marker, mFavouritePlace);
            } else
            {
                setInfoAsync(marker.getPosition(), marker);
            }
        }
    };

    public void myLocationClicked(View view)
    {
//        Utils.setLightMarkers(this,mMap,mIcon);
//        Bitmap bitmap = Utils.setLightMarkers(this,mMap,mIcon);
//        ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
        enableUserLocationAndZoom();
    }

    EditText.OnKeyListener mOnKeyListener = (view, keyCode, event) ->
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
        {
            searchAddressClicked(null);
            return true;
        }
        return false;
    };
}