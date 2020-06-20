package br.com.veteritec.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.veteritec.R;
import br.com.veteritec.clinics.ClinicResponseStructure;
import br.com.veteritec.clinics.ClinicUseCase;
import br.com.veteritec.login.LoginRequestStructure;
import br.com.veteritec.login.LoginUseCase;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;

    Spinner spnClinics;

    EditText etLogin;
    EditText etPassword;

    private ClinicResponseStructure clinicResponseStructure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        clinicResponseStructure = new ClinicResponseStructure();

        spnClinics = findViewById(R.id.spnClinics);

        clinicResponseStructure = (ClinicResponseStructure) getIntent().getSerializableExtra("CLINIC_STRUCTURE");

        if (clinicResponseStructure == null) {
            getClinics();
        } else {
            populateClinicSpinner(clinicResponseStructure.getClinics());
        }

        etLogin = findViewById(R.id.edtEmail);
        etPassword = findViewById(R.id.edtPassword);

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

    private void getClinics() {
        ApiRequest apiRequest = new ApiRequest();
        ClinicUseCase clinicUseCase = new ClinicUseCase(ThreadExecutor.getInstance(), apiRequest);
        clinicUseCase.setCallback(new ClinicUseCase.OnGetClinicCallback() {
            @Override
            public void onSuccess(ClinicResponseStructure clinicsStructure) {
                clinicResponseStructure = clinicsStructure;
                populateClinicSpinner(clinicResponseStructure.getClinics());
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, "Erro ao obter a lista de clínicas cadastradas.", Toast.LENGTH_SHORT).show();
            }
        });

        clinicUseCase.execute();
    }

    private void populateClinicSpinner(List<ClinicResponseStructure.Clinic> clinicList) {
        List<String> name = new ArrayList<>();

        for (ClinicResponseStructure.Clinic clinic : clinicList) {
            name.add(clinic.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        spnClinics.setAdapter(arrayAdapter);
    }

    private void doLogin() {
        LoginRequestStructure loginRequestStructure = new LoginRequestStructure();

        loginRequestStructure.setEmail(etLogin.getText().toString());
        loginRequestStructure.setPassword(etPassword.getText().toString());

        ApiRequest apiRequest = new ApiRequest();

        String clinicId = "";

        for(ClinicResponseStructure.Clinic clinic : clinicResponseStructure.getClinics()) {
            if (clinic.getName().equals(spnClinics.getSelectedItem().toString())) {
                clinicId = clinic.getId();
            }
        }

        LoginUseCase loginUseCase = new LoginUseCase(ThreadExecutor.getInstance(), context, apiRequest, loginRequestStructure, clinicId);
        loginUseCase.setCallback(new LoginUseCase.OnLoginCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Logado com sucesso!", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }

            @Override
            public void onFailure(int statusCode) {
                if (statusCode == 400) {
                    Toast.makeText(context, "Usuário e/ou senha incorretos!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Ocorreu um erro durante o login, verifique os dados etente novamente!" + statusCode, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginUseCase.execute();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
