package com.capa.capa.mobilecomputingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText userNameEditText, userPasswordEditText,userEmailEditText;
    String userName, userPassword, userEmail;
    Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        TextView signIn_text = findViewById(R.id.signIn_text);
        signIn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        userNameEditText = findViewById(R.id.userName);
        userEmailEditText = findViewById(R.id.userEmail);
        userPasswordEditText = findViewById(R.id.userPassword);



        signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = userNameEditText.getText().toString();
                userPassword = userPasswordEditText.getText().toString();
                userEmail = userEmailEditText.getText().toString();
                if(!userEmail.isEmpty()&&!userPassword.isEmpty()&&!userName.isEmpty()){
                    Toast.makeText(Registration.this, "Button works fine", Toast.LENGTH_LONG).show();
                    mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                openMenu();
                            }else {
                                Toast.makeText(Registration.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }
    public void openLogin(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    public void openMenu(){
        Intent intent = new Intent(this, MapsWeatherActivity.class);
        startActivity(intent);
    }

}
