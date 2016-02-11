package com.nuttwarunyu.ghosthere;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    List<Address> geocodeMatches = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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

            mMap.addMarker(new MarkerOptions().position(new LatLng(15.098282, 104.31069927))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.chucky)).title("This Second").snippet("This is Snippet"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(15.095070, 104.31064720))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cemetery)).title("This Third").snippet("This is Snippet"));


        } else {
            Log.d("checkSelfPermission", "out condition");
        }
    }

    private void onLocationChange(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost)).title("I'm Here").snippet("This is Snippet"));
        Log.d("LatLong", "Lat : " + latitude + " Lng : " + longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        Toast.makeText(MapsActivity.this, "Latitude : " + latitude + " Longitude : " + longitude, Toast.LENGTH_LONG).show();
    }

}
