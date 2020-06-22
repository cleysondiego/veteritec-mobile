package br.com.veteritec.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

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
import br.com.veteritec.utils.NavigationDrawer;

public class AddAnimalActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Context context;

    private DrawerLayout drawer;

    EditText edtAnimalBirthDate;

    Button btnDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);

        context = getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txtAddAnimalTitle);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_add_animal);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        edtAnimalBirthDate = findViewById(R.id.edtAddAnimalBirthDate);

        edtAnimalBirthDate.setInputType(InputType.TYPE_NULL);

        btnDate = findViewById(R.id.btnAddAnimalDate);

        btnDate.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnAddAnimalDate) {
            showDateDialog(edtAnimalBirthDate);
        } else if (v.getId() == R.id.btnAddVaccineSave) {
            Toast.makeText(this, "Informações salvas!", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.btnAddAnimalEdit) {
            Toast.makeText(this, "Edição habilitada!", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.btnAddAnimalDelete) {
            Toast.makeText(this, "Informações deletadas!", Toast.LENGTH_SHORT).show();
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

    private void showDateDialog(final EditText edtAnimalBirthDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                edtAnimalBirthDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(AddAnimalActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}