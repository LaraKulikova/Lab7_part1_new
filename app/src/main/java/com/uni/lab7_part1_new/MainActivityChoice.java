package com.uni.lab7_part1_new;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivityChoice extends AppCompatActivity {

    private CheckBox checkBoxFirestore;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_choice);

        checkBoxFirestore = findViewById(R.id.checkBoxFirestore);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxFirestore.isChecked()) {
                    // Используйте Firestore
                   useFirestore();
                } else {
                    // Используйте Realtime Database
                   useRealtimeDatabase();
                }
            }
        });
    }

    private void useFirestore() {
        Intent intent = new Intent(MainActivityChoice.this, UserActivity.class);
        startActivity(intent);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        // Пример работы с Firestore
//        db.collection("users")
//                .add(new User("UserFirestore","eMailFirestore"))
//                .addOnSuccessListener(documentReference -> {
//                    // Успешно добавлено
//                })
//                .addOnFailureListener(e -> {
//                    // Ошибка при добавлении
//                });
    }

    private void useRealtimeDatabase() {
        Intent intent = new Intent(MainActivityChoice.this, UserActivityFirestore.class);
        startActivity(intent);

//        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
//        User data = new User("UserRealTimeDB","eMailRealTimeDB");
//        database.push().setValue(data)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Добавление прошло успешно", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Не добавлено", Toast.LENGTH_SHORT).show();
//                });
    }
    private void fetchFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Обработка данных документа
                            Log.d("Firestore", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w("Firestore", "Ошибка получения документов.", task.getException());
                    }
                });
    }
}