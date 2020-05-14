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
import com.google.firebase.auth.FirebaseUser;

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
        if (isValidFields()) {
            doLogin();
        } else {
            Toast.makeText(this, "Erro no email/senha. Por favor, verifique os dados digitados e tente novamente!", Toast.LENGTH_SHORT).show();
        }
    }

    public void nextActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isValidFields() {
        return !etLogin.toString().isEmpty() || etLogin.toString().length() < 5 || !etPassword.toString().isEmpty() || etPassword.toString().length() < 5;
    }

    private void doLogin() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Log.d("VETERITEC", etLogin.getText().toString() + " pass: " + etPassword.getText().toString());
        mAuth.signInWithEmailAndPassword(etLogin.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("VETERITEC", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    nextActivity();
                } else {
                    Log.w("VETERITE", "signInWithEmail:failure", task.getException());
                }
            }
        });

        // TODO: Call api to login user. if success, call nextAtivity method. Else, show message error.
    }
}
