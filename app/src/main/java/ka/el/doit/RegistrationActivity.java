package ka.el.doit;

import android.content.Intent;
import android.os.Bundle;
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

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseHandler fb;

    private TextView lintToAuth;
    private MaterialButton regBtn;
    private EditText regEmail, regPassword, regRepeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().hide();

        fb = new FirebaseHandler();

        isLogin();
        init();
    }

    private void isLogin() {
        if  (fb.isLogin()) {
            goProfile();
        }
    }

    private void init() {
        // Обработка перехода на экран авторизации - начало
        lintToAuth = findViewById(R.id.linkToLogin);
        lintToAuth.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                new Intent(
                                        RegistrationActivity.this,
                                        LoginActivity.class
                                )
                        );
                    }
                }
        );
        // Обработка перехода на экран авторизации - конец

        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regRepeatPassword = findViewById(R.id.regRepeatPassword);

        // Обработка авторизации - начало
        regBtn = findViewById(R.id.regBtn);
        regBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = regEmail.getText().toString(),
                                pass = regPassword.getText().toString(),
                                repPass = regRepeatPassword.getText().toString();

                        if (!pass.equals(repPass)) {
                            Toast.makeText(RegistrationActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                        } else if (email.equals("") || pass.equals("") || repPass.equals("")) {
                            Toast.makeText(RegistrationActivity.this, "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            reg(email, pass);
                        }
                    }
                }
        );
        // Обработка авторизации - конец
    }

    private void reg(String email, String pass) {
        fb.getMAuth().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    goProfile();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Что-то пошло не так...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }

    private void goProfile() {
        startActivity(
                new Intent(
                        RegistrationActivity.this, ProfileActivity.class
                )
        );
    }
}