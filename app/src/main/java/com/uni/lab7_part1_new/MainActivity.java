package com.uni.lab7_part1_new;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    TextView textViewUserInfo;
    TextView textViewuserName;
    ListView userList;
    List<String> uids;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        textViewUserInfo = findViewById(R.id.textViewUserInfo);
        textViewuserName = findViewById(R.id.textViewUserName);
        userList = findViewById(R.id.userList);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", uids.get(position));
                startActivity(intent);
            }
        });

        mDatabase = FirebaseDatabase.getInstance("https://lab7-part1-new-default-rtdb.firebaseio.com/").getReference("users");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewUserInfo.setText(auth.getCurrentUser().getEmail().toString());
        textViewuserName.setText(auth.getCurrentUser().getDisplayName().toString());

//        User testUser = new User("username", "email"); //создание usera в бд
//        mDatabase.push().setValue(testUser);
    }

    public void onClickExit(View v) {
//        User testUser = new User("username", "email@mail.ru");//проверка работы БД, создается ли юзер
//        mDatabase.push().setValue(testUser);

        auth.signOut();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(MainActivity.this, "Выход произошел успешно", Toast.LENGTH_SHORT).show();

                }
            }
        });
        startActivity(new Intent(this, MainActivitySingIn.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            List<String> dataList = new ArrayList<>();
            uids = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                uids.add(ds.getKey());
                dataList.add(ds.child("email").getValue(String.class) + " " +
                        ds.child("username").getValue(String.class));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, dataList);
            userList.setAdapter(arrayAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    public void onAddClick(View v){
        Intent intent = new Intent(getApplicationContext(), UserActivity.class);

        startActivity(intent);
    }
}
