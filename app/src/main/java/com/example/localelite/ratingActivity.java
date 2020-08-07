package com.example.localelite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ratingActivity extends AppCompatActivity {
    int rating;
    Button payButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("providerID");
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = ratingBar.getNumStars();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(id).child("rating");
                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rating);
                startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
            }
        });
        payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                PackageManager managerclock = getPackageManager();
                i = managerclock.getLaunchIntentForPackage("net.one97.paytm");
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
            }
        });
    }
}