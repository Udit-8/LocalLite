package com.example.localelite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Switch userSwitch = findViewById(R.id.userSwitch);
        ImageButton proceedButton = findViewById(R.id.proceedButton);
        userSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    userSwitch.setText("Provider");
                }
                else
                {
                    userSwitch.setText("User");
                }
            }
        });
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),UserSignUpActivity.class);
                intent.putExtra("userOrProvider",userSwitch.isChecked());
                startActivity(intent);
                finish();
            }
        });
    }
}