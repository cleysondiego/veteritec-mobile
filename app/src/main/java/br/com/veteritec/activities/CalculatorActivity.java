package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
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

import br.com.veteritec.R;
import br.com.veteritec.utils.DosageCalculator;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txtCalculatorTitle);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_calculator);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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
        switch (item.getItemId()) {
            case R.id.nav_calculator:
                break;
            case R.id.nav_add_customer:
                Intent addCustomer = new Intent(this, AddCustomerActivity.class);
                addCustomer.putExtra("Query", 0);
                startActivity(addCustomer);
                finish();
                break;
            case R.id.nav_query_customer:
                Intent queryCustomer = new Intent(this, QueryActivity.class);
                queryCustomer.putExtra("Customer", 1);
                startActivity(queryCustomer);
                finish();
                break;
            case R.id.nav_add_animal:
                Intent addAnimal = new Intent(this, AddAnimalActivity.class);
                startActivity(addAnimal);
                finish();
                break;
            case R.id.nav_add_vaccine:
                Intent addVaccine = new Intent(this, AddVaccineActivity.class);
                startActivity(addVaccine);
                finish();
                break;
            case R.id.nav_query_vaccine:
                Intent queryVaccine = new Intent(this, QueryActivity.class);
                queryVaccine.putExtra("Vaccine", 2);
                startActivity(queryVaccine);
                finish();
                break;
            case R.id.nav_logout:
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
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
        calculateDosage();
    }

    private void calculateDosage() {
        double result = calculate();
        DecimalFormat resultFormat = new DecimalFormat("0.0000");

        System.out.println("RESULTADO: " + resultFormat.format(result));

        if (result != VALIDATION_FAIL && result != INVALID_OPERATORS) {
            txtResult.setText(resultFormat.format(result));
        } else {
            Toast.makeText(this, "Erro com as opções, por favor selecione apenas as opções disponiveis!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Erro na validação dos dados digitados, por favor verifique-os e tente novamente!", Toast.LENGTH_SHORT).show();
            return VALIDATION_FAIL;
        }

        String dosageUnit = dosageSpinner.getSelectedItem().toString();
        String concentrationUnit = concentrationSpinner.getSelectedItem().toString();

        if (!validateFields(animalWeight, dosage, concentration, dosageUnit, concentrationUnit)) {
            Toast.makeText(this, "Erro na validação dos dados digitados, por favor digite apenas números!", Toast.LENGTH_SHORT).show();
            return VALIDATION_FAIL;
        }

        DosageCalculator dosageCalculator = new DosageCalculator();
        return dosageCalculator.calculate(animalWeight, dosage, concentration, dosageUnit, concentrationUnit);
    }

    private boolean validateFields(double animalWeight, double dosage, double concentration, String dosageUnit, String concentrationUnit) {
        return animalWeight > 0 || dosage > 0 || concentration > 0 || dosageUnit.equals("") || concentrationUnit.equals("");
    }
}
