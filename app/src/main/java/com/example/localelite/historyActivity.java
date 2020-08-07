package com.example.localelite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class historyActivity extends AppCompatActivity {
    ArrayList<String> names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        final ArrayList<String> ids ;
        names = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
        ListView listView = findViewById(R.id.ListView);
        listView.setAdapter(arrayAdapter);
        Intent intent = getIntent();
        ids = intent.getStringArrayListExtra("ids");
        if(ids!=null) {
            for (String items : ids) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(items);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        names.add(dataSnapshot.child("name").getValue().toString());
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1 = new Intent(getApplicationContext(),providerProfileActivity.class);
                intent1.putExtra("id",ids.get(i));
                startActivity(intent1);
            }
        });
    }
}
