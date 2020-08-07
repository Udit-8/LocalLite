package com.example.localelite;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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


public class providerLocationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location userLocation;
    TextView distanceText;
    Button cancelButton,completedButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_location_map);
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
        Intent intent = getIntent();

        distanceText = findViewById(R.id.distanceText);
        cancelButton = findViewById(R.id.cancelButton);
        completedButton = findViewById(R.id.completedButton);
        final String providerID = intent.getStringExtra("id");
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Working Providers").child(providerID).child("l");
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests").child(providerID);
            userLocation = new Location("");
            GeoFire geoFire;

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestMapActivity.isDriverWorking = false;
                    GeoFire fire = new GeoFire(ref);
                    fire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(new Intent(getApplicationContext(),UserMainActivity.class));
                }
            });
            completedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GeoFire fire = new GeoFire(ref);
                    fire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("History").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference1.child(providerID).setValue(true);
                    Intent intent1 = new Intent(getApplicationContext(),ratingActivity.class);
                    intent1.putExtra("providerID",providerID);
                    startActivity(intent1);
                }
            });
            geoFire = new GeoFire(ref);
            geoFire.getLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {
                            if(location == null)
                            {
                                Toast.makeText(getApplicationContext(),"The User cancelled your request",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),providerMainActivity.class));
                            }
                            else {
                                if(location!=null) {
                                    userLocation.setLatitude(location.latitude);
                                    userLocation.setLongitude(location.longitude);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 11));
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"The User cancelled your request",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),providerMainActivity.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GeoFire geo = new GeoFire(FirebaseDatabase.getInstance().getReference().child("Working Providers"));
                    final Location providerLocation = new Location("");
                    geo.getLocation(providerID, new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {
                            if(location == null)
                            {
                                Toast.makeText(getApplicationContext(),"Your request was cancelled",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),UserMainActivity.class));
                                reference.removeEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                if(location!=null) {
                                    providerLocation.setLatitude(location.latitude);
                                    providerLocation.setLongitude(location.longitude);
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(providerLocation.getLatitude(), providerLocation.getLongitude())).title("provider Location"));
                                    double distance = providerLocation.distanceTo(userLocation);
                                    if(distance>100)
                                    distanceText.setText("Distance of driver " + String.valueOf(distance));
                                    else
                                        distanceText.setText("Your driver has arrived.");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mMap.addMarker(new MarkerOptions().position(new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).title("Your Location"));



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


}
