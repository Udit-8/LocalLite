package com.example.localelite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class providerProfileActivity extends AppCompatActivity {

    TextView phoneText,nameText;
    Button makeRequestButton;
    RatingBar providerRatingBar;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_profile);
        Intent intent = getIntent();
            nameText = findViewById(R.id.nameText);
         phoneText = findViewById(R.id.phoneText);
         makeRequestButton = findViewById(R.id.requestButton);
         providerRatingBar = findViewById(R.id.providerRatingBar);
         id = intent.getStringExtra("id");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameText.setText(dataSnapshot.child("name").getValue().toString());
                phoneText.setText(dataSnapshot.child("phone").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        makeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests").child(id);
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        new GeoLocation(chooseLocationMapsActivity.curPos.latitude,chooseLocationMapsActivity.curPos.longitude));
                startActivity(new Intent(getApplicationContext(),UserMainActivity.class));
            }
        });
        setProviderRating();
    }

    private void setProviderRating() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id).child("rating");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sumRating = 0,numUsers = 0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    sumRating += Integer.valueOf(snapshot.getValue().toString());
                    numUsers++;
                }
                int rating;
                if(numUsers != 0)
                 rating = sumRating/numUsers;
                else
                    rating = 0;
                Log.i("rating",String.valueOf(rating));
                providerRatingBar.setRating((float) (rating*1.0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
