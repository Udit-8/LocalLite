package com.example.localelite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class chooseActivity extends AppCompatActivity {

    String choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        Spinner spinner = findViewById(R.id.spinner2);
        ArrayAdapter <CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.provider_type,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice = adapterView.getItemAtPosition(i).
                        toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button proceedButton = findViewById(R.id.proceedButton);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),chooseLocationMapsActivity.class);
                intent.putExtra("type",choice);
                startActivity(intent);
            }
        });
    }
}
