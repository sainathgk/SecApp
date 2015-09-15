/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.activities.rentalapp;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Demonstrates how to instantiate a SupportMapFragment programmatically and add a marker to it.
 */
public class SelectLocationActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationSource, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private static final String MAP_FRAGMENT_TAG = "map";
    private OnLocationChangedListener mListener;
    private GoogleMap mGoogleMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // It isn't possible to set a fragment's id programmatically so we set a tag instead and
        // search for it using that.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);

        // We only create a fragment if it doesn't already exist.
        if (mapFragment == null) {
            // To programmatically add the map, we first create a SupportMapFragment.
            mapFragment = SupportMapFragment.newInstance();

            // Then we add it using a FragmentTransaction.
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(android.R.id.content, mapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        map.setLocationSource(this);
        map.setOnMapLongClickListener(this);
        map.setMyLocationEnabled(true);

        Location loc = getIntent().getParcelableExtra("current_Location");
        if (loc != null) {
            LatLng lng = new LatLng(loc.getLatitude(), loc.getLongitude());

            CameraPosition Location =
                    new CameraPosition.Builder().target(lng)
                            .zoom(17f)
                            .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(Location));
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mListener != null) {
            Location location = new Location("LongPressLocationProvider");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            location.setAccuracy(100);
            if (mGoogleMap != null) {
                mGoogleMap.addMarker(new MarkerOptions().position(latLng));
            }
            mListener.onLocationChanged(location);
            setResult(Activity.RESULT_OK, new Intent().putExtra("new_Location", location));
            finish();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mListener != null) {
            Location location = new Location("LongPressLocationProvider");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            location.setAccuracy(100);
            mListener.onLocationChanged(location);
            if (mGoogleMap != null) {
                mGoogleMap.addMarker(new MarkerOptions().position(latLng));
                Toast.makeText(getApplicationContext(), "Press Back to take the Selected Location", Toast.LENGTH_SHORT).show();
            }
            setResult(Activity.RESULT_OK, new Intent().putExtra("new_Location", location));
        }
    }
}
