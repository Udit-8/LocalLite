package com.example.localelite;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class requestMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static boolean isDriverWorking = false;
    LatLng userLoc;
    LocationManager locationManager;
    LocationListener locationListener;
    Location curloc;
    Button acceptRequestButton;

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location l = null;
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

                l = locationManager.getLastKnownLocation(provider);
            }

            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                curloc = getLastKnownLocation();
                mMap.addMarker(new MarkerOptions().position(new LatLng(curloc.getLatitude(),curloc.getLongitude())).title("your location"));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_map);
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
        acceptRequestButton = findViewById(R.id.acceptRequestButton);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GeoFire geoFire = new GeoFire(ref);

        geoFire.getLocation(id, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if(location == null)
                {
                    Toast.makeText(getApplicationContext(),"The user cancelled his request",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),providerProfileActivity.class));
                }
                userLoc = new LatLng(location.latitude,location.longitude);
                mMap.addMarker(new MarkerOptions().position(userLoc).title("User Location"));
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(userLoc,11));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                curloc = location;
                mMap.addMarker(new MarkerOptions().position(new LatLng(curloc.getLatitude(),curloc.getLongitude())).title("your location"));
                mMap.addMarker(new MarkerOptions().position(userLoc).title("User Location"));
                if(!isDriverWorking) {
                    DatabaseReference freference = FirebaseDatabase.getInstance().getReference();
                    GeoFire geo = new GeoFire(freference.child("Working Providers"));
                    geo.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    GeoFire fire = new GeoFire(reference.child("Active Providers"));
                    fire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(curloc.getLatitude(), curloc.getLongitude()));
                }
                else
                {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    GeoFire fire = new GeoFire(reference.child("Working Providers"));
                    fire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(curloc.getLatitude(), curloc.getLongitude()));
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
          if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
          {
              ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
          }
          else
          {
              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
              curloc = getLastKnownLocation();
              mMap.addMarker(new MarkerOptions().position(new LatLng(curloc.getLatitude(),curloc.getLongitude())).title("your location"));
          }
          acceptRequestButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if(!isDriverWorking) {
                      DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                      GeoFire geo = new GeoFire(reference.child("Active Providers"));
                      geo.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
                      GeoFire fire = new GeoFire(reference.child("Working Providers"));
                      fire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(curloc.getLatitude(), curloc.getLongitude()));
                      acceptRequestButton.setText("Cancel Request");
                      isDriverWorking = true;
                      providerMainActivity.isDriverActive = false;
                      reference.child("Accepted users").child(id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                  }
                  else
                  {
                      DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                      GeoFire geo = new GeoFire(reference.child("Working Providers"));
                      geo.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
                      GeoFire fire = new GeoFire(reference.child("Active Providers"));
                      fire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(curloc.getLatitude(), curloc.getLongitude()));
                      acceptRequestButton.setText("Accept Request");
                      isDriverWorking = false;
                      reference.child("Accepted users").child(id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                  }
              }
          });
    }

    }
