package com.uni.lab7_part1_new;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivitySingIn extends AppCompatActivity {
    private EditText editTextText_password, editText_email;
    private Button buttonLogin, buttonSingInWithGoogle;
    private FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_sing_in);
        editText_email = findViewById(R.id.editText_email);
        editTextText_password = findViewById(R.id.editTextText_password);
        auth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(MainActivitySingIn.this, googleSignInOptions);

    }

    public void onRegisterClick(View v) {
        Intent intent = new Intent(this, MainActivitySingUp.class);
        startActivity(intent);
    }

    public void onLogin(View v) {
        String email = editText_email.getText().toString();
        String pass = editTextText_password.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            if (!pass.isEmpty()) {
                auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(MainActivitySingIn.this, "Доступ разрешен.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivitySingIn.this, MainActivity.class));
                        finish();//заканчиваем работу этого метода
                    }
                }).addOnFailureListener(new OnFailureListener() {//
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivitySingIn.this, "Введены неверные данные, либо такого пользователя не существует!", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                editTextText_password.setError("Пожалуйста введите пароль");
            }
        } else {
            editText_email.setError("Пожалуйста введите email");
        }
    }

    public void onForgotPass(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivitySingIn.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
        EditText emailInForgotPass = dialogView.findViewById(R.id.editTextEmailInForgotPass);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.buttonResetPass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPassReset = emailInForgotPass.getText().toString();
                //проверяем, что поле не пустое и соответствует шаблону email
                if (emailPassReset.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailPassReset).matches()) {
                    Toast.makeText(MainActivitySingIn.this, "Введите корректный email", Toast.LENGTH_LONG).show();
                    return;
                }
                auth.sendPasswordResetEmail(emailPassReset).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivitySingIn.this, "Вам отправлено письмо на электронную почту.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivitySingIn.this, "Не отправлено", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//кнопкой отмена закрываем диалог
            }
        });
        if (dialog.getWindow() != null) {
            //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable());//делает окно прозрачным
        }
        dialog.show();
    }

    public void onGoogleLogIn(View v) {
        Intent intent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(MainActivitySingIn.this, MainActivity.class));
                            }else {
                                Toast.makeText(MainActivitySingIn.this,"Ошибка аутентификации Google",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    Toast.makeText(MainActivitySingIn.this, "Error", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    });

}