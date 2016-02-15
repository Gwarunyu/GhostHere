package com.nuttwarunyu.ghosthere;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    List<ParseObject> parseObjectList;
    private GoogleMap mMap;
    private HashMap<Marker, MyMarker> markerMyMarkerHashMap;
    private ArrayList<MyMarker> myMarkerArrayList = new ArrayList<MyMarker>();

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        markerMyMarkerHashMap = new HashMap<Marker, MyMarker>();

        new TaskProcess().execute();

        /*myMarkerArrayList.add(new MyMarker("Brasil", "icon1", Double.parseDouble("-28.5971788"), Double.parseDouble("-52.7309824")));
        myMarkerArrayList.add(new MyMarker("United States", "icon2", Double.parseDouble("33.7266622"), Double.parseDouble("-87.1469829")));
        myMarkerArrayList.add(new MyMarker("Canada", "icon3", Double.parseDouble("51.8917773"), Double.parseDouble("-86.0922954")));
        myMarkerArrayList.add(new MyMarker("England", "icon4", Double.parseDouble("52.4435047"), Double.parseDouble("-3.4199249")));
        myMarkerArrayList.add(new MyMarker("España", "icon5", Double.parseDouble("41.8728262"), Double.parseDouble("-0.2375882")));
        myMarkerArrayList.add(new MyMarker("Portugal", "icon6", Double.parseDouble("40.8316649"), Double.parseDouble("-4.936009")));
*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("checkSelfPermission", "in condition");
            mMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                Log.d("location", "Not null");
                onLocationChange(location);
            }

        } else {
            Log.d("checkSelfPermission", "out condition");
        }

    }

    private void plotMarkers(ArrayList<MyMarker> markers) {
        Log.d("plotMarkers ", "markers in : " + markers);
        if (markers.size() > 0) {
            for (MyMarker myMarker : markers) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(myMarker.getmLatitude()),Double.parseDouble(myMarker.getmLongitude())));
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost));

                Marker currentMarker = mMap.addMarker(markerOptions);
                markerMyMarkerHashMap.put(currentMarker, myMarker);

                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            }
        }
    }

    private void onLocationChange(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost2)).title("I'm Here").snippet("This is Snippet"));
        Log.d("LatLong", "Lat : " + latitude + " Lng : " + longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }

    private class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
            MyMarker myMarker = markerMyMarkerHashMap.get(marker);
            ImageView markerIcon = (ImageView) view.findViewById(R.id.marker_icon);
            TextView markerLabel = (TextView) view.findViewById(R.id.marker_label);

            markerIcon.setImageResource(manageMarkerIcon(myMarker.getmIcon()));
            markerLabel.setText(myMarker.getmLabel());
            return view;
        }
    }

    private int manageMarkerIcon(String markerIcon) {
        switch (markerIcon) {
            case "icon1":
                return R.drawable.icon1;
            case "icon2":
                return R.drawable.icon2;
            case "icon3":
                return R.drawable.icon3;
            case "icon4":
                return R.drawable.icon4;
            case "icon5":
                return R.drawable.icon5;
            case "icon6":
                return R.drawable.icon6;
            default:
                return R.drawable.cemetery;
        }
    }

    private class TaskProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            myMarkerArrayList = new ArrayList<MyMarker>();

            try {
                ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("TestObject");

                parseObjectList = parseQuery.find();
                Log.d("doInBackground", "parseObjectList : " + parseObjectList);
                for (ParseObject parseObject : parseObjectList) {
                    MyMarker myMarker = new MyMarker();
                    myMarker.setmLabel((String) parseObject.get("title_ghost"));
                    myMarker.setmLatitude((String) parseObject.get("lat_ghost"));
                    myMarker.setmLongitude((String) parseObject.get("lng_ghost"));
                    myMarker.setmIcon((String) parseObject.get("img_info"));
                    Log.d("doInBackground", "mymarker : " + myMarker);
                    myMarkerArrayList.add(myMarker);
                }
            } catch (ParseException e) {
                Log.e("doInBackground", "Error : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("onPostExecute", "myMarkerArrayList : " + myMarkerArrayList);
            plotMarkers(myMarkerArrayList);
        }
    }

}
