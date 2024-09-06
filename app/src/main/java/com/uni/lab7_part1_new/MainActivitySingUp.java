package com.uni.lab7_part1_new;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivitySingUp extends AppCompatActivity {
    private EditText editTextPasswordSignUp;
    private EditText editText_email_signup;
    private Button buttonSignUp;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_sing_up);
        editTextPasswordSignUp = findViewById(R.id.editTextPasswordSignUp);
        editText_email_signup = findViewById(R.id.editText_email_signup);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        auth = FirebaseAuth.getInstance();
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = editText_email_signup.getText().toString();
                String pass = editTextPasswordSignUp.getText().toString();
                if (user.isEmpty()) {
                    editText_email_signup.setError("Введите email.");
                    return;
                }
                if (pass.isEmpty()) {
                    editTextPasswordSignUp.setError("Введите пароль");

                } else {

                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivitySingUp.this, "Регистрация прошла успешно", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivitySingUp.this, MainActivitySingIn.class));
                            } else {
                                Toast.makeText(MainActivitySingUp.this,
                                        "Регистрация провалена: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void onSignIn(View v) {
        Intent intent = new Intent(this, MainActivitySingIn.class);
        startActivity(intent);
    }

}