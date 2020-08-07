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

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class chooseLocationMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location choosenLocation;
    static LatLng curPos;
    GeoFire geoFire;
    DatabaseReference firebaseDataReference, ref;
    FirebaseAuth auth;
    Button searchButton;
    String providerType;

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location l = null;
        Location bestLocation = null;
        for (String provider : providers) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
               ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }
            l = locationManager.getLastKnownLocation(provider);

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
        if(requestCode == 0)
        {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                choosenLocation = getLastKnownLocation();
                curPos = new LatLng(choosenLocation.getLatitude(), choosenLocation.getLongitude());
            }
        }
        else
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                choosenLocation = getLastKnownLocation();
                curPos = new LatLng(choosenLocation.getLatitude(), choosenLocation.getLongitude());
            }
        }
    }}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location_maps);
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
        auth = FirebaseAuth.getInstance();
        firebaseDataReference = FirebaseDatabase.getInstance().getReference("User Requests");
        ref = FirebaseDatabase.getInstance().getReference("Active Providers");
        geoFire = new GeoFire(firebaseDataReference);
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
          searchButton = findViewById(R.id.searchButton);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                choosenLocation = location;
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            choosenLocation = getLastKnownLocation();
            curPos = new LatLng(choosenLocation.getLatitude(),choosenLocation.getLongitude());
        }
        // Add a marker in Sydney and move the camera


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos,12));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                curPos = latLng;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Your chosen location"));
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoFire = new GeoFire(ref);
                searchButton.setText("searching...");
                searchButton.setClickable(false);
                getClosestprovider();

            }
        });
    }
    private boolean providerFound = false;
    private int radius = 1;
    private void getClosestprovider()
    {
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(curPos.latitude,curPos.longitude),radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered( String key, GeoLocation location) {
                if(!providerFound)
                {
                    final String id = key;
                    searchButton.setText("Search");
                    searchButton.setClickable(true);
                    providerFound = true;
                   final Marker Mmarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude,location.longitude)).title("Closest provider"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude,location.longitude),11));
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if(marker.equals(Mmarker))
                            {
                                Intent intent = new Intent(getApplicationContext(),providerProfileActivity.class);
                                intent.putExtra("id",id);
                                startActivity(intent);
                            }
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!providerFound)
                {
                    radius++;
                    getClosestprovider();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

}

