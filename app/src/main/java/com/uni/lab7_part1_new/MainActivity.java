package com.uni.lab7_part1_new;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    TextView textViewUserInfo;
    TextView textViewuserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        textViewUserInfo = findViewById(R.id.textViewUserInfo);
        textViewuserName = findViewById(R.id.textViewUserName);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewUserInfo.setText(auth.getCurrentUser().getEmail().toString());
        textViewuserName.setText(auth.getCurrentUser().getDisplayName().toString());
    }

    public void onClickExit(View v){
        auth.signOut();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null){
                    Toast.makeText(MainActivity.this, "Выход произошел успешно", Toast.LENGTH_SHORT).show();

                }
            }
        });
        startActivity(new Intent(this, MainActivitySingIn.class));
    }
}