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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    Bundle bundle;
    String userId;

    EditText editTextName;
    EditText editTextEmail;

    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mDatabase = FirebaseDatabase.getInstance("https://lab7-part1-new-default-rtdb.firebaseio.com/").getReference("users");

        bundle = getIntent().getExtras();
        editTextName = findViewById(R.id.editTextNameAU);
        editTextEmail = findViewById(R.id.editTextEmailAU);
        deleteButton = findViewById(R.id.buttonDelete);

        if (bundle != null) {
            userId = bundle.getString("id");
            Toast.makeText(this, "Id элемента " + userId, Toast.LENGTH_SHORT).show();
            mDatabase.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Toast.makeText(UserActivity.this, "Заполнение выполнено успешно", Toast.LENGTH_SHORT).show();
                    if (task.isSuccessful()) {
                        DataSnapshot ds = task.getResult();
                        String email = ds.child("email").getValue(String.class);
                        String name = ds.child("username").getValue(String.class);

                        editTextEmail.setText(email);
                        editTextName.setText(name);
                    } else {
                        Toast.makeText(UserActivity.this, "Поля не заполнены", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            deleteButton.setEnabled(false);
        }
    }

    public void onSaveClick(View view) {
        User user = new User(editTextName.getText().toString(),
                editTextEmail.getText().toString());
        if (bundle!=null){
        mDatabase.child(userId).setValue(user);
            Toast.makeText(this, "Изменения прошли успешно", Toast.LENGTH_SHORT).show();
        }else {
            mDatabase.push().setValue(user);
        }

        goHome();
    }

    public void onDeleteClick(View view) {
        mDatabase.child(userId).removeValue();
        Toast.makeText(this, "Удаление прошло успешно!", Toast.LENGTH_SHORT).show();
        goHome();
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}