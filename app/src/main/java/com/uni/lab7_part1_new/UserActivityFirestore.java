package com.uni.lab7_part1_new;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserActivityFirestore extends AppCompatActivity {
    private FirebaseFirestore db;

    Bundle bundle;
    String userId;

    EditText editTextName;
    EditText editTextEmail;

    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_firestore);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        bundle = getIntent().getExtras();
        editTextName = findViewById(R.id.editTextNameSP);
        editTextEmail = findViewById(R.id.editTextEmailSP);
        deleteButton = findViewById(R.id.buttonDeleteFB);

        if (bundle != null) {
            userId = bundle.getString("id");
            Toast.makeText(this, "Id элемента " + userId, Toast.LENGTH_SHORT).show();
            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot ds = task.getResult();
                        if (ds.exists()) {
                            String email = ds.getString("email");
                            String name = ds.getString("username");

                            editTextEmail.setText(email);
                            editTextName.setText(name);
                        } else {
                            Toast.makeText(UserActivityFirestore.this, "Поля не заполнены", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UserActivityFirestore.this, "Ошибка при получении данных", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            deleteButton.setEnabled(false);
        }
    }

    public void onSaveClickFB(View view) {
        User user = new User(editTextName.getText().toString(),
                editTextEmail.getText().toString());
        if (bundle != null) {
            db.collection("users").document(userId).set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserActivityFirestore.this, "Изменения прошли успешно", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserActivityFirestore.this, "Ошибка при сохранении данных", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            db.collection("users").add(user)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserActivityFirestore.this, "Пользователь добавлен", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserActivityFirestore.this, "Ошибка при добавлении пользователя", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        goHomeFB();
    }

    public void onDeleteClickFB(View view) {
        db.collection("users").document(userId).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserActivityFirestore.this, "Удаление прошло успешно!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserActivityFirestore.this, "Ошибка при удалении пользователя", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        goHomeFB();
    }

    private void goHomeFB() {
        Intent intent = new Intent(this, MainActivityFirebase.class);
        startActivity(intent);
    }
}