package com.example.localelite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class providerMainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastLocation;
    DatabaseReference ref;
    FirebaseAuth auth;
    GeoFire geoFire;

    Button profileButton,takeRequestButton,requestButton,signOutButton;
    static  boolean isDriverActive = false;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                lastLocation = getLastKnownLocation();
                geoFire.setLocation(auth.getCurrentUser().getUid(),new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()));
            }
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location l = null;
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_main);
        ref = FirebaseDatabase.getInstance().getReference("Active Providers");
        auth = FirebaseAuth.getInstance();
        geoFire = new GeoFire(ref);
        takeRequestButton = findViewById(R.id.takeRequestButton);
        profileButton = findViewById(R.id.profileButton);
        requestButton = findViewById(R.id.requestButton);
        signOutButton = findViewById(R.id.signOutButton);
       takeRequestButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               isDriverActive = true;
               locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
               locationListener = new LocationListener() {
                   @Override
                   public void onLocationChanged(Location location) {
                       lastLocation = location;
                        if(isDriverActive)
                       geoFire.setLocation(auth.getCurrentUser().getUid(),new GeoLocation(location.getLatitude(),location.getLongitude()));
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
               if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
               {
                   ActivityCompat.requestPermissions(providerMainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
               }
               else
               {
                   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                   lastLocation = getLastKnownLocation();
                   geoFire.setLocation(auth.getCurrentUser().getUid(),new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()));

               }
           }
       });
       requestButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

            final ArrayList<String> ids = new ArrayList<>();
               final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests").child(auth.getCurrentUser().getUid());
               reference.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       for(DataSnapshot snapshot : dataSnapshot.getChildren())
                       {
                           String id = snapshot.getKey();
                           ids.add(id);

                       }
                       Log.i("size",String.valueOf(ids.size()));
                       Intent intent = new Intent(getApplicationContext(),requestsActivity.class);

                       intent.putStringArrayListExtra("names",ids);
                       startActivity(intent);
                       reference.removeEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

           }
       });
       signOutButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               FirebaseAuth.getInstance().signOut();
               startActivity(new Intent(getApplicationContext(),UserSignInActivity.class));
               finish();
           }
       });
    }
}
