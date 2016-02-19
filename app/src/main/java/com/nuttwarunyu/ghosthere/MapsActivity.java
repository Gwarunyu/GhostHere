package com.nuttwarunyu.ghosthere;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
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
    private ArrayList<CircleArea> circleAreaArrayList = new ArrayList<CircleArea>();
    String title_ghost;
    String story_ghost;
    Double latitude;
    Double longitude;
    Button btnGoToStory;

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

        btnGoToStory = (Button) findViewById(R.id.btn_goStory);
        markerMyMarkerHashMap = new HashMap<Marker, MyMarker>();

        if (!isConnectingToInternet()) {
            Toast.makeText(MapsActivity.this, "Internet not connect", Toast.LENGTH_SHORT).show();
        } else
            new TaskProcess().execute();

        btnGoToStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReadStoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("checkSelfPermission", "in condition");
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.82718, 100.51282), 15));

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                Log.d("location", "Not null");
                onLocationChange(location);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setMessage("GPS is disabled in your device. Enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Enable GPS",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Intent callGPSSettingIntent = new Intent(
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(callGPSSettingIntent);
                                    }
                                });
                alertDialogBuilder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }

        } else {
            Log.d("checkSelfPermission", "out condition");
        }

    }

    private void plotMarkers(ArrayList<MyMarker> markers) {
        if (markers.size() > 0) {
            for (final MyMarker myMarker : markers) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(myMarker.getmLatitude()),
                                Double.parseDouble(myMarker.getmLongitude())));
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost));

                Marker currentMarker = mMap.addMarker(markerOptions);
                markerMyMarkerHashMap.put(currentMarker, myMarker);

                CircleOptions circleOptions = new CircleOptions().fillColor(manageCircleColor(myMarker.getmColor()))
                        .strokeColor(manageCircleColor(myMarker.getmColor())).radius(manageRadius(myMarker.getmRadius()))
                        .center(new LatLng(Double.parseDouble(myMarker.getmLatitude()), Double.parseDouble(myMarker.getmLongitude())));
                mMap.addCircle(circleOptions);


                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (story_ghost == null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
                            Toast.makeText(MapsActivity.this, "ที่นี่คือ. . ." + title_ghost, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), GhostStoryActivity.class);
                            intent.putExtra("title", title_ghost);
                            intent.putExtra("story", story_ghost);
                            startActivity(intent);
                        }
                    }
                });
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
            final MyMarker myMarker = markerMyMarkerHashMap.get(marker);
            ImageView markerIcon = (ImageView) view.findViewById(R.id.marker_icon);
            TextView markerLabel = (TextView) view.findViewById(R.id.marker_label);
            title_ghost = myMarker.getmTitle();
            story_ghost = myMarker.getmStory();
            latitude = Double.parseDouble(myMarker.getmLatitude());
            longitude = Double.parseDouble(myMarker.getmLongitude());

            Log.d("getInfoContents", "story_ghost : " + story_ghost);

            //Glide.with(getApplicationContext()).load(s).fitCenter().into(markerIcon);
            markerIcon.setImageResource(manageMarkerIcon(myMarker.getmIcon()));
            markerLabel.setText(title_ghost);

            return view;
        }

    }

    private Double manageRadius(String s) {
        switch (s) {
            case "s":
                return (double) 100;
            case "m":
                return (double) 150;
            case "l":
                return (double) 200;
            default:
                return (double) 150;
        }
    }

    private int manageCircleColor(String s) {
        switch (s) {
            case "red":
                return 0x30ff0004;
            case "yellow":
                return 0x30FFF200;
            case "dark":
                return 0x30000000;
            default:
                return 0x30C40DAF;
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
                ParseQuery<ParseObject> parseQueryCircle = new ParseQuery<ParseObject>("ghost_db");

                parseObjectList = parseQuery.find();
                for (ParseObject parseObject : parseObjectList) {
                    MyMarker myMarker = new MyMarker();
                    myMarker.setmTitle((String) parseObject.get("title_ghost"));
                    myMarker.setmLatitude((String) parseObject.get("lat_ghost"));
                    myMarker.setmLongitude((String) parseObject.get("lng_ghost"));
                    myMarker.setmIcon((String) parseObject.get("img_info"));
                    myMarker.setmStory((String) parseObject.get("story_ghost"));
                    myMarker.setmRadius((String) parseObject.get("radius"));
                    myMarker.setmColor((String) parseObject.get("zone"));

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
            plotMarkers(myMarkerArrayList);
            // plotCircle(circleAreaArrayList);
        }
    }

}
