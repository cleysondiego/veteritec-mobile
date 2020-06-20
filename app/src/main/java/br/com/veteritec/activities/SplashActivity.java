package br.com.veteritec.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import br.com.veteritec.R;
import br.com.veteritec.clinics.ClinicResponseStructure;
import br.com.veteritec.clinics.ClinicUseCase;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;

public class SplashActivity extends AppCompatActivity {
    private ClinicResponseStructure clinicResponseStructure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getClinics();

        try {
            getSupportActionBar().hide();
        } catch (NullPointerException ignored) {
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void changeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("CLINIC_STRUCTURE", clinicResponseStructure);
        startActivity(intent);

        finish();
    }

    private void getClinics() {
        ApiRequest apiRequest = new ApiRequest();
        ClinicUseCase clinicUseCase = new ClinicUseCase(ThreadExecutor.getInstance(), apiRequest);
        clinicUseCase.setCallback(new ClinicUseCase.OnGetClinicCallback() {
            @Override
            public void onSuccess(ClinicResponseStructure clinicsStructure) {
                clinicResponseStructure = clinicsStructure;
                changeActivity();
            }

            @Override
            public void onFailure(int statusCode) {
                changeActivity();
            }
        });

        clinicUseCase.execute();
    }
}
