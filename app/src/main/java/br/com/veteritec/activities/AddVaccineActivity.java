package br.com.veteritec.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import br.com.veteritec.R;

public class AddVaccineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vaccine);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txtAddVaccineTitle);
        setSupportActionBar(toolbar);
    }
}
