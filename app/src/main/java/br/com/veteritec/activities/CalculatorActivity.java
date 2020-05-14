package br.com.veteritec.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.com.veteritec.R;
import br.com.veteritec.utils.DosageCalculator;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner dosageSpinner;
    private Spinner concentrationSpinner;

    private EditText etAnimalWeight;
    private EditText etDosage;
    private EditText etFarmacoConcentration;

    private TextView txtResult;

    private Button btnCalculate;

    private double VALIDATION_FAIL = -1;
    private double INVALID_OPERATORS = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        etAnimalWeight = findViewById(R.id.etAnimalWeight);
        etDosage = findViewById(R.id.etDosage);
        etFarmacoConcentration = findViewById(R.id.etFarmacoConcentration);

        txtResult = findViewById(R.id.txtResult);

        btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(this);

        dosageSpinner = findViewById(R.id.spFarmacoDosage);
        ArrayAdapter<CharSequence> dosageArrayAdapter = ArrayAdapter.createFromResource(this, R.array.dosage_array, android.R.layout.simple_spinner_item);
        dosageArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dosageSpinner.setAdapter(dosageArrayAdapter);

        concentrationSpinner = findViewById(R.id.spFarmacoConcentration);
        ArrayAdapter<CharSequence> concentrationArrayAdapter = ArrayAdapter.createFromResource(this, R.array.concentration_array, android.R.layout.simple_spinner_item);
        concentrationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        concentrationSpinner.setAdapter(concentrationArrayAdapter);
    }

    @Override
    public void onClick(View v) {
        calculateDosage();
    }

    private void calculateDosage() {
        double result = calculate();

        if (result != VALIDATION_FAIL && result != INVALID_OPERATORS) {
            txtResult.setText(String.valueOf(result));
        } else {
            Toast.makeText(this, "Erro com as opções, por favor selecione apenas as opções disponiveis!", Toast.LENGTH_SHORT).show();
        }
    }

    private double calculate() {
        double animalWeight = 0;
        double dosage = 0;
        double concentration = 0;
        try {
            animalWeight = Double.parseDouble(etAnimalWeight.getText().toString());
            dosage = Double.parseDouble(etDosage.getText().toString());
            concentration = Double.parseDouble(etFarmacoConcentration.getText().toString());
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
