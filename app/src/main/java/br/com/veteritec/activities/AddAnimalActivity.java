package br.com.veteritec.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.veteritec.R;

public class AddAnimalActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    EditText edtAnimalBirthDate;

    Button btnDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txtAddAnimalTitle);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_menu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        edtAnimalBirthDate = findViewById(R.id.edtAnimalBirthDate);

        edtAnimalBirthDate.setInputType(InputType.TYPE_NULL);

        btnDate = findViewById(R.id.btnDate);

        btnDate.setOnClickListener(this);
    }

    public void onClick(View v) {
        showDateDialog(edtAnimalBirthDate);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_calculator:
                Intent calculator = new Intent(this, CalculatorActivity.class);
                startActivity(calculator);
                finish();
                break;
            case R.id.nav_add_client:
                Intent addClient = new Intent(this, AddClientActivity.class);
                startActivity(addClient);
                finish();
                break;
            case R.id.nav_query_client:
                Intent queryClient = new Intent(this, QueryClientActivity.class);
                startActivity(queryClient);
                finish();
                break;
            case R.id.nav_add_animal:
                break;
            case R.id.nav_add_vaccine:
                Intent addVaccine = new Intent(this, AddVaccineActivity.class);
                startActivity(addVaccine);
                finish();
                break;
            case R.id.nav_query_vaccine:
                Intent queryVaccine = new Intent(this, QueryVaccineActivity.class);
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

    private void showDateDialog(final EditText edtAnimalBirthDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

                edtAnimalBirthDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(AddAnimalActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}