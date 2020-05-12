package br.com.veteritec.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import br.com.veteritec.R;

public class CalculatorActivity extends AppCompatActivity {
    Spinner dosage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        dosage = findViewById(R.id.spDosage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dosage_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dosage.setAdapter(adapter);
    }
}
