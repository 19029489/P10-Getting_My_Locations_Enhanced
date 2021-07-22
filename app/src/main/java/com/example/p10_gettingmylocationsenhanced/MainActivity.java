package com.example.p10_gettingmylocationsenhanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnStop, btnCheck;
    ToggleButton btnMusic;
    TextView tvLat, tvLng;

    LatLng poi_current;

    private GoogleMap map;

    String folderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnCheck = findViewById(R.id.btnCheckRecords);
        btnMusic = findViewById(R.id.btnMusic);

        tvLat = findViewById(R.id.tvLat);
        tvLng = findViewById(R.id.tvLng);

        folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";

        File folder = new File(folderLocation);
        if (folder.exists() == false) {
            boolean result = folder.mkdir();
            if (result == true) {
                Log.d("File Read/Write", "Folder created");
            }
        }

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                //another way to check permissions
                if (checkPermission() == false){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    return;
                }

                Task<Location> task = client.getLastLocation();

                task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            tvLat.setText("Latitude : " + location.getLatitude());
                            tvLng.setText("Longitude : " + location.getLongitude());

                            poi_current = new LatLng(location.getLatitude(), location.getLongitude());

                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_current, 15));

                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(poi_current)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            UiSettings ui = map.getUiSettings();

                            ui.setCompassEnabled(false);

                            ui.setZoomControlsEnabled(true);

                            if(checkPermission() == true) {
                                map.setMyLocationEnabled(true);
                            } else {
                                Log.e("GMap - Permission", "GPS access has not been granted");
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                            }


                        } else {
                            tvLat.setText("Latitude : ");
                            tvLng.setText("Longitude : ");

                            String msg = "No Last Known Location found";
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30000);
        mLocationRequest.setSmallestDisplacement(50);

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();

                    tvLat.setText("Latitude : " + lat);
                    tvLng.setText("Longitude : " + lng);

                    poi_current = new LatLng(lat, lng);
                    map.clear();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_current, 15));

                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(poi_current)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    try {
                        folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";
                        File targetFile_I = new File(folderLocation, "data.txt");
                        FileWriter writer_I = new FileWriter(targetFile_I, true);
                        writer_I.write(lat + ", " + lng + "\n");
                        writer_I.flush();
                        writer_I.close();
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            };
        };


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission() == true){

                    client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);

                    return;
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission() == true){
                    if (mLocationCallback != null){
                        client.removeLocationUpdates(mLocationCallback);
                        String msg = "Location Update removed";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = "No Location Update found";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);

                    return;
                }
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, LocationsActivity.class);
//                startActivity(i);
            }
        });

        btnMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    startService(new Intent(MainActivity.this, MyService.class));
                } else {
                    // The toggle is disabled
                    stopService(new Intent(MainActivity.this, MyService.class));
                }
            }
        });

    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0: {
                //If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
