package com.nuttwarunyu.ghosthere;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Dell-NB on 18/2/2559.
 */
public class CircleArea {
    Double latitude;
    Double longitude;
    Double radius;

    public void setLatitude(String latitude) {
        this.latitude = Double.parseDouble(latitude);
    }

    public void setLongitude(String longitude) {
        this.longitude = Double.parseDouble(longitude);
    }

    public void setRadius(String radius) {
        this.radius = Double.parseDouble(radius);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getRadius() {
        return radius;
    }
}
