package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.veteritec.R;
import br.com.veteritec.utils.NavigationDrawer;

public class AddVaccineActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private Context context;

    Spinner spnVeterinary;
    Spinner spnClient;

    EditText edtDate;
    EditText edtTime;
    EditText edtDescription;

    Button btnTime;
    Button btnDate;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vaccine);

        context = getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txtAddVaccineTitle);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_add_vaccine);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        spnVeterinary = findViewById(R.id.spnAddVaccineVeterinary);
        spnClient = findViewById(R.id.spnAddVaccineClient);

        edtDate = findViewById(R.id.edtAddVaccineDate);
        edtTime = findViewById(R.id.edtAddVaccineTime);
        edtDescription = findViewById(R.id.edtAddVaccineDescription);

        edtDate.setInputType(InputType.TYPE_NULL);
        edtTime.setInputType(InputType.TYPE_NULL);

        btnTime = findViewById(R.id.btnAddVaccineTime);
        btnDate = findViewById(R.id.btnAddVaccineDate);
        btnSave = findViewById(R.id.btnAddVaccineSave);

        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnAddVaccineDate) {
            showDateDialog(edtDate);
        } else if (id == R.id.btnAddVaccineTime) {
            showTimeDialog(edtTime);
        } else if (id == R.id.btnAddVaccineSave) {

            saveVaccine();
            Log.e("VETERITEC", "CHAMOU O SAVE VACINE");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationDrawer navigationDrawer = new NavigationDrawer();
        Intent screen = navigationDrawer.choosedItem(drawer, context, item);

        if(screen != null) {
            startActivity(screen);
            finish();
        }else{
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

    private void showTimeDialog(final EditText edtTime) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                edtTime.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new TimePickerDialog(AddVaccineActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void showDateDialog(final EditText edtDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                edtDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(AddVaccineActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveVaccine() {}
}
