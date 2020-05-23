package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.veteritec.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etLogin;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);

        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean isEmailValid = validateField(etLogin);
        if (isEmailValid) {
            boolean isPasswordValid = validateField(etPassword);
            if (isPasswordValid) {
                doLogin();
            }
        }
    }

    public void nextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateField(EditText field) {
        if (field.getText().toString().isEmpty()) {
            field.setError("Esse campo não pode ser vazio");
            field.requestFocus();
            return false;
        } else if (field.getText().toString().length() < 5) {
            field.setError("O campo deve conter mais que 5 caracteres.");
            field.requestFocus();
            return false;
        }
        return true;
    }

    private void doLogin() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(etLogin.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    nextActivity();
                } else {
//                    try {
//
//                    } catch (FirebaseException)
//                    String errorCode = task.getException().getMessage();
                    Log.w("VETERITEC", "signInWithEmail:failure", task.getException());
                }
            }
        });
    }
}
