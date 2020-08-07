package com.example.localelite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSignInActivity extends AppCompatActivity {

    EditText emailText,passwordText;
    ImageButton signInButton;
    TextView registerText;
    FirebaseAuth auth;
    ImageView imageview3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_in);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        signInButton = findViewById(R.id.signUpButton);
        registerText = findViewById(R.id.registerText);
        imageview3 = findViewById(R.id.imageView3);
        auth = FirebaseAuth.getInstance();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplicationContext(),"Please enter the email",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(getApplicationContext(),"Please enter the password",Toast.LENGTH_LONG).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"You have been signed in",Toast.LENGTH_LONG).show();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       if(dataSnapshot.child("type").getValue().toString().equals("Provider"))
                                        startActivity(new Intent(getApplicationContext(),providerMainActivity.class));
                                        else
                                           startActivity(new Intent(getApplicationContext(),UserMainActivity.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Log in unsuccessful "+task.getException(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        imageview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }
}
