package com.example.localelite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSignUpActivity extends AppCompatActivity {

    EditText emailText,passwordText,phoneText,nameText;
    Spinner spinner;
    TextView typeText;
    String choice;
    ImageButton signUpButton;
    private FirebaseAuth auth;
    DatabaseReference firebaseDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        phoneText = findViewById(R.id.phoneText);
        nameText = findViewById(R.id.nameText);
        spinner = findViewById(R.id.spinner);
        typeText = findViewById(R.id.typeTextView);
        firebaseDataReference = FirebaseDatabase.getInstance().getReference("Users");
        signUpButton = findViewById(R.id.signUpButton);
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        final Boolean isProvider = intent.getBooleanExtra("userOrProvider",false);
        ArrayAdapter <CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.provider_type,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        if(isProvider == false)
        {
            typeText.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
        }
        else {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    choice = adapterView.getItemAtPosition(i).toString();
                    Log.i("choice", choice);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                final String name = nameText.getText().toString().trim();
                final String phone = phoneText.getText().toString().trim();
                if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(getApplicationContext(),"Enter your name!!!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplicationContext(),"Enter your email!!!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(getApplicationContext(),"Enter your password!!!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(getApplicationContext(),"Enter your phone number!!!",Toast.LENGTH_LONG).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(UserSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {

                            Toast.makeText(getApplicationContext(),"Unsuccessful registration "+task.getException(),Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            if(isProvider) {

                                Providers provider = new Providers(email,name,phone,"Provider",choice);
                                firebaseDataReference.child(auth.getCurrentUser().getUid()).setValue(provider);
                                firebaseDataReference.child("type").child("Provider").child(choice).child(auth.getCurrentUser().getUid()).setValue(true);
                                Toast.makeText(getApplicationContext(),"User registration successful",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),providerMainActivity.class));
                            }
                            else
                            {
                                Users user = new Users(email,name,phone,"User");
                                firebaseDataReference.child(auth.getCurrentUser().getUid()).setValue(user);
                                firebaseDataReference.child("type").child("User").child(auth.getCurrentUser().getUid()).setValue(true);
                                Toast.makeText(getApplicationContext(),"User registration successful",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),UserMainActivity.class));
                            }

                        }
                    }

                });
            }
        });
    }
}

