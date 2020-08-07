package com.example.localelite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserMainActivity extends AppCompatActivity {
    Button searchButton,editButton,historyButton,signOutButton,acceptedRequestsButton;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        searchButton = findViewById(R.id.searchButton);
        editButton = findViewById(R.id.editButton);
        historyButton = findViewById(R.id.historyButton);
        signOutButton = findViewById(R.id.signOutButton);
        acceptedRequestsButton= findViewById(R.id.acceptedRequestsButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),chooseLocationMapsActivity.class));
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accepted users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(),dataSnapshot.getKey() + " cancelled your request.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        acceptedRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> acceptedProviderId = new ArrayList<>();
                reference = FirebaseDatabase.getInstance().getReference("Accepted users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            acceptedProviderId.add(snapshot.getKey());


                        }
                        Intent intent = new Intent(getApplicationContext(),providerListActivity.class);
                        intent.putStringArrayListExtra("providerID",acceptedProviderId);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            ArrayList<String> ids = new ArrayList<>();
            @Override
            public void onClick(View view) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("History").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren())
                        {
                            ids.add(snapshot.getKey());
                        }
                        Intent intent = new Intent(getApplicationContext(),historyActivity.class);
                        intent.putStringArrayListExtra("ids",ids);
                        startActivity(intent);
                        databaseReference.removeEventListener(new ValueEventListener() {
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}
