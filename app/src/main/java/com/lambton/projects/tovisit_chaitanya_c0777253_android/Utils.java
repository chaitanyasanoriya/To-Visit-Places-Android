package com.lambton.projects.tovisit_chaitanya_c0777253_android;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.text.format.DateFormat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Utils
{

    private static List<Polyline> mPolylineList = new ArrayList<>();
    private static List<Marker> mMarkerList = new ArrayList<>();

    /**
     * Method to Sort LatLng Clockwise, so that the Markers will always form Quadrilateral
     * @param corners - List of LatLng
     * @return - Sorted LatLng
     */
    public static List<LatLng> orderRectCorners(List<LatLng> corners)
    {

        List<LatLng> ordCorners = orderPointsByRows(corners);

        if (ordCorners.get(0).latitude > ordCorners.get(1).latitude)
        {
            LatLng tmp = ordCorners.get(0);
            ordCorners.set(0, ordCorners.get(1));
            ordCorners.set(1, tmp);
        }

        if (ordCorners.get(2).latitude < ordCorners.get(3).latitude)
        {
            LatLng tmp = ordCorners.get(2);
            ordCorners.set(2, ordCorners.get(3));
            ordCorners.set(3, tmp);
        }
        return ordCorners;
    }

    /**
     * Sorts List of LatLng by Row
     * @param points - List of LatLng
     * @return - Sorted List of LatLng sorted by row
     */
    private static List<LatLng> orderPointsByRows(List<LatLng> points)
    {
        Collections.sort(points, (p1, p2) -> Double.compare(p1.longitude,p2.longitude));
        return points;
    }

    /**
     * Method to get Title and Snippet in a formatted manner from Address Object
     * @param address - Address Object from which the Title and Snippet needs to be extracted
     * @return - String Array where title is at 0th index and snippet at 1st index
     */
    public static String[] getFormattedAddress(Address address)
    {
        dump(address);
        StringBuilder title = new StringBuilder();
        StringBuilder snippet = new StringBuilder();
        if (address.getSubThoroughfare() != null)
        {
            title.append(address.getSubThoroughfare());
        }
        if (address.getThoroughfare() != null)
        {
            if (!title.toString().isEmpty())
            {
                title.append(", ");
            }
            title.append(address.getThoroughfare());
        }
        if (address.getPostalCode() != null)
        {
            if (!title.toString().isEmpty())
            {
                title.append(", ");
            }
            title.append(address.getPostalCode());
        }
        if (address.getLocality() != null)
        {
            snippet.append(address.getLocality());
        }
        if (address.getAdminArea() != null)
        {
            if (!snippet.toString().equals("") || !snippet.toString().equals(" "))
            {
                snippet.append(", ");
            }
            snippet.append(address.getAdminArea());
        }
        if(title.toString().isEmpty())
        {
            title.append(getFormattedDate());
        }
        System.out.println("title: "+title+" snippet: "+snippet);
        return new String[]{title.toString(), snippet.toString()};
    }

    /**
     * Method to get distance between two points in Km
     * @param lat1 - Latitude of point A
     * @param lon1 - Longitude of point A
     * @param lat2 - Latitude of point B
     * @param lon2 - Longitude of point B
     * @return - Distance between the points in Km
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2)
    {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    /**
     * Converts degrees to radians
     * @param deg - Degrees to be converted into radians
     * @return - Degrees in Radians
     */
    private static double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Converts Radians in degrees
     * @param rad - Radians to be converted into degrees
     * @return - Radians in Degrees
     */
    private static double rad2deg(double rad)
    {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * Method to calculate Mid-Point between two points
     * @param lat1 - Latitude of Point A
     * @param lon1 - Longitude of Point A
     * @param lat2 - Latitude of Point B
     * @param lon2 - Longitude of Point B
     * @return - Mid-Point between the two Points
     */
    public static LatLng midPoint(double lat1, double lon1, double lat2, double lon2)
    {
        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }

    public static String getFormattedDate()
    {
        return new DateFormat().format("EEE, MM-dd-yyyy hh:mm", new Date()).toString();
    }

    public static void showError(String title, String message, Context context)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    public static void addPolyline(Polyline polyline)
    {
        mPolylineList.add(polyline);
    }

    public static void clearPolylines()
    {
        for(Polyline polyline: mPolylineList)
        {
            polyline.remove();
        }
    }

    public static void addMarker(Marker marker)
    {
        mMarkerList.add(marker);
    }

    public static void clearMarkers()
    {
        for(Marker marker: mMarkerList)
        {
            marker.remove();
        }
    }

    public static void dump(Object o)
    {
        if(o == null)
        {
            System.out.println("object is null");
            return;
        }
        Field[] fields = o.getClass().getDeclaredFields();
        if(fields.length == 0)
        {
            System.out.println("No fields");
        }
        for (int i=0; i<fields.length; i++)
        {
            try
            {
                System.out.println(fields[i].getName() + " - " + fields[i].get(o));
                dump(fields[i]);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static String getDirectionURL(Location location,LatLng latLng, Context context, String mode)
    {
        StringBuilder URL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        URL.append("origin=" + location.getLatitude() + "," + location.getLongitude());
        URL.append("&destination=" + latLng.latitude + "," + latLng.longitude);
        URL.append("&key=" + context.getString(R.string.google_maps_key));
        URL.append("&mode=" + mode);
        System.out.println(URL);
        return URL.toString();
    }

    public static String getDirectionURL(LatLng userLatLng,LatLng latLng, Context context, String mode)
    {
        StringBuilder URL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        URL.append("origin=" + userLatLng.latitude + "," + userLatLng.longitude);
        URL.append("&destination=" + latLng.latitude + "," + latLng.longitude);
        URL.append("&key=" + context.getString(R.string.google_maps_key));
        URL.append("&mode=" + mode);
        System.out.println(URL);
        return URL.toString();
    }
}
