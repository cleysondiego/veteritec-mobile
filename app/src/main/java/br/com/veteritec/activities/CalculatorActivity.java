package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.Locale;

import br.com.veteritec.R;
import br.com.veteritec.utils.DosageCalculator;
import br.com.veteritec.utils.NavigationDrawer;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Context context;

    private Spinner dosageSpinner;
    private Spinner concentrationSpinner;

    private EditText edtAnimalWeight;
    private EditText edtDosage;
    private EditText edtFarmacoConcentration;

    private TextView txtResult;

    private Button btnCalculate;

    private DrawerLayout drawer;

    private double VALIDATION_FAIL = -1;
    private double INVALID_OPERATORS = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        context = getApplicationContext();

        SharedPreferences language = getSharedPreferences("Language", MODE_PRIVATE);

        Locale locale = new Locale(language.getString("ChoosedLang", "pt"));
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.txtCalculatorTitle));
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_calculator);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        edtAnimalWeight = findViewById(R.id.edtAnimalWeight);
        edtDosage = findViewById(R.id.edtDosage);
        edtFarmacoConcentration = findViewById(R.id.edtFarmacoConcentration);

        txtResult = findViewById(R.id.txtResult);

        btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(this);

        dosageSpinner = findViewById(R.id.spnFarmacoDosage);
        ArrayAdapter<CharSequence> dosageArrayAdapter = ArrayAdapter.createFromResource(this, R.array.dosage_array, android.R.layout.simple_spinner_item);
        dosageArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dosageSpinner.setAdapter(dosageArrayAdapter);

        concentrationSpinner = findViewById(R.id.spnFarmacoConcentration);
        ArrayAdapter<CharSequence> concentrationArrayAdapter = ArrayAdapter.createFromResource(this, R.array.concentration_array, android.R.layout.simple_spinner_item);
        concentrationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        concentrationSpinner.setAdapter(concentrationArrayAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationDrawer navigationDrawer = new NavigationDrawer();
        Intent screen = navigationDrawer.choosedItem(drawer, context, item);

        if (screen != null) {
            startActivity(screen);
            finish();
        } else {
            finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (validateFields()) {
            calculateDosage();
        } else {
            Toast.makeText(this, getResources().getString(R.string.toastCalculatorValidationsError), Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateDosage() {
        double result = calculate();
        DecimalFormat resultFormat = new DecimalFormat("0.0000");

        if (result != VALIDATION_FAIL && result != INVALID_OPERATORS) {
            txtResult.setText(resultFormat.format(result));
        } else {
            Toast.makeText(this, getResources().getString(R.string.toastCalculatorOptionsError), Toast.LENGTH_SHORT).show();
        }
    }

    private double calculate() {
        double animalWeight;
        double dosage;
        double concentration;
        try {
            animalWeight = Double.parseDouble(edtAnimalWeight.getText().toString());
            dosage = Double.parseDouble(edtDosage.getText().toString());
            concentration = Double.parseDouble(edtFarmacoConcentration.getText().toString());
        } catch (NumberFormatException exception) {
            Toast.makeText(this, getResources().getString(R.string.toastCalculatorOptionsError), Toast.LENGTH_SHORT).show();
            return VALIDATION_FAIL;
        }

        String dosageUnit = dosageSpinner.getSelectedItem().toString();
        String concentrationUnit = concentrationSpinner.getSelectedItem().toString();

        DosageCalculator dosageCalculator = new DosageCalculator();
        return dosageCalculator.calculate(animalWeight, dosage, concentration, dosageUnit, concentrationUnit);
    }

    private boolean validateFields() {
        return validateField(edtAnimalWeight) &&
                validateField(edtDosage) &&
                validateField(edtFarmacoConcentration);
    }

    private boolean validateField(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getResources().getString(R.string.setErrorEmptyField));
            editText.requestFocus();
            return false;
        } else if (Double.parseDouble(editText.getText().toString()) <= 0) {
            editText.setError(getResources().getString(R.string.setErrorZeroField));
            editText.requestFocus();
            return false;
        }
        return true;
    }
}
