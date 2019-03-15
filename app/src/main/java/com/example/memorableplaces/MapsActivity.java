package com.example.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    SharedPreferences sharedPreferences;
    LocationManager locationManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location preLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                centerMapOnLocation(preLocation, "Here");
            }
        }
    }

    public  void centerMapOnLocation(Location location, String title){
        LatLng now = new LatLng(location.getLatitude(), location.getLongitude());

        if(title != "Here")
            mMap.addMarker(new MarkerOptions().position(now));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 10));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent main = getIntent();
        Log.e("ListPosistion", ""+main.getIntExtra("position",-1));
        sharedPreferences = this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);
        if(main.getIntExtra("position", -1) == -1){
        for(LatLng la:MainActivity.locations){
            mMap.addMarker(new MarkerOptions().position(la));

         }
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location preLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(preLocation, "Here");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                MainActivity.latitude.add(Double.toString(latLng.latitude));
                MainActivity.longitude.add(Double.toString(latLng.longitude));

                try {
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);

                    String result = "";
                    result += addresses.get(0).getLocality()+" ";
                    MainActivity.places.add((result));
                    MainActivity.adapter.notifyDataSetChanged();

                    sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.places)).apply();
                    sharedPreferences.edit().putString("latitude", ObjectSerializer.serialize(MainActivity.latitude)).apply();
                    sharedPreferences.edit().putString("longitude", ObjectSerializer.serialize(MainActivity.longitude)).apply();
                  } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        if(main.getIntExtra("position",-1) != -1) {

            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLongitude(Double.parseDouble(MainActivity.longitude.get(main.getIntExtra("position",-1))));
            location.setLatitude(Double.parseDouble(MainActivity.latitude.get(main.getIntExtra("position",-1))));

            centerMapOnLocation(location, "review");
        }
    }
}
