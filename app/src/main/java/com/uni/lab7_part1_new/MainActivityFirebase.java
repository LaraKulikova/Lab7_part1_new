package com.uni.lab7_part1_new;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivityFirebase extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView userList;
    private List<String> uids;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_firebase);

        db = FirebaseFirestore.getInstance();
        userList = findViewById(R.id.userList);
        addButton = findViewById(R.id.buttonAddUser);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivityFirestore.class);
                intent.putExtra("id", uids.get(position));
                startActivity(intent);
            }
        });
        loadUsers();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserActivityFirestore.class);
                startActivity(intent);
            }
        });
    }

    private void loadUsers() {
        CollectionReference usersRef = db.collection("users");
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> dataList = new ArrayList<>();
                    uids = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        uids.add(document.getId());
                        String email = document.getString("email");
                        String username = document.getString("username");
                        dataList.add(email + " " + username);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivityFirebase.this,
                            android.R.layout.simple_list_item_1, dataList);
                    userList.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(MainActivityFirebase.this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
