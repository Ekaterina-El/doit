package ka.el.doit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ka.el.doit.Utils.FirebaseHandler;

public class LoginActivity extends AppCompatActivity {

    private TextView lintToReg;
    private EditText loginEmail, loginPassword;
    private MaterialButton loginBtn;

    private FirebaseHandler fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        fb = new FirebaseHandler();

        isLogin();
        init();
    }

    private void isLogin() {
        if (fb.isLogin()) {
            goProfile();
        }
    }

    private void goProfile() {
        startActivity(
                new Intent(
                        LoginActivity.this, ProfileActivity.class
                )
        );
    }

    private void init() {
        // Обработка перехода на экарн регистрации - начало
        lintToReg = findViewById(R.id.linkToReg);
        lintToReg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                new Intent(
                                        LoginActivity.this,
                                        RegistrationActivity.class
                                )
                        );
                    }
                }
        );
        // Обработка перехода на экарн регистрации - конец


        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);

        // Обработка авторизации - начало
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = loginEmail.getText().toString(), pass = loginPassword.getText().toString();

                        if (email.equals("") || pass.equals("")) {
                            Toast.makeText(LoginActivity.this, "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show();

                        } else {
                            auth(email, pass);
                        }
                    }
                }
        );
        // Обработка авторизации - конец

    }

    private void auth(String email, String password) {
        fb.getMAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    goProfile();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }
}