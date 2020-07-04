package br.com.veteritec.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import br.com.veteritec.utils.LoadingDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;

    private Spinner spnClinics;

    private TextView txtTitle;
    private TextView txtClinic;
    private TextView txtEmail;
    private TextView txtPassword;
    private TextView txtNoConnection;

    private EditText etLogin;
    private EditText etPassword;

    private Button btnLogin;

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

        txtTitle = findViewById(R.id.txtTitle);
        txtClinic = findViewById(R.id.txtClinic);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtNoConnection = findViewById(R.id.txtNoConnection);

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
            boolean isEmailValid = validateField(etLogin);
            if (isEmailValid) {
                boolean isPasswordValid = validateField(etPassword);
                if (isPasswordValid) {
                    doLogin();
                    closeKeyboard();
                }
            }
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private boolean validateField(EditText field) {
        if (field.getText().toString().isEmpty()) {
            field.setError(getResources().getString(R.string.setErrorEmptyField));
            field.requestFocus();
            return false;
        } else if (field.getText().toString().length() < 5) {
            field.setError(getResources().getString(R.string.setErrorMinimumCharacters));
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
                txtTitle.setVisibility(View.INVISIBLE);
                txtClinic.setVisibility(View.INVISIBLE);
                txtEmail.setVisibility(View.INVISIBLE);
                txtPassword.setVisibility(View.INVISIBLE);
                spnClinics.setVisibility(View.INVISIBLE);
                etLogin.setVisibility(View.INVISIBLE);
                etPassword.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                txtNoConnection.setVisibility(View.VISIBLE);
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
        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        LoginRequestStructure loginRequestStructure = new LoginRequestStructure();

        loadingDialog.startLoadingDialog();

        loginRequestStructure.setEmail(etLogin.getText().toString());
        loginRequestStructure.setPassword(etPassword.getText().toString());

        ApiRequest apiRequest = new ApiRequest();

        String clinicId = "";

        for (ClinicResponseStructure.Clinic clinic : clinicResponseStructure.getClinics()) {
            if (clinic.getName().equals(spnClinics.getSelectedItem().toString())) {
                clinicId = clinic.getId();
            }
        }

        LoginUseCase loginUseCase = new LoginUseCase(ThreadExecutor.getInstance(), context, apiRequest, loginRequestStructure, clinicId);
        loginUseCase.setCallback(new LoginUseCase.OnLoginCallback() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastLoginSuccesfullyLogin), Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                if (statusCode == 400) {
                    Toast.makeText(context, getResources().getString(R.string.toastLoginEmailPasswordCheck), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.toastLoginError) + statusCode, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginUseCase.execute();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
