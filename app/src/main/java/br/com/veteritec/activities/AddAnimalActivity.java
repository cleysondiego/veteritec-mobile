package br.com.veteritec.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import java.util.Objects;

import br.com.veteritec.R;
import br.com.veteritec.pets.GetPetsResponseStructure;
import br.com.veteritec.utils.NavigationDrawer;

public class AddAnimalActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    private int edition = 0;
    private boolean editable = false;

    private DrawerLayout drawer;

    EditText edtAnimalName;
    EditText edtAnimalBirthDate;
    EditText edtAnimalSpecies;
    EditText edtAnimalRace;
    EditText edtAnimalSize;
    EditText edtAnimalWeight;
    EditText edtAnimalObservation;

    Button btnDate;
    Button btnAnimalSave;
    Button btnAnimalEdit;
    Button btnAnimalDelete;

    private GetPetsResponseStructure.Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);

        context = getApplicationContext();

        SharedPreferences language = getSharedPreferences("Language", MODE_PRIVATE);

        Locale locale = new Locale(language.getString("ChoosedLang", "pt"));
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        edtAnimalName = findViewById(R.id.edtAddAnimalName);
        edtAnimalBirthDate = findViewById(R.id.edtAddAnimalBirthDate);
        edtAnimalSpecies = findViewById(R.id.edtAddAnimalSpecies);
        edtAnimalRace = findViewById(R.id.edtAddAnimalRace);
        edtAnimalSize = findViewById(R.id.edtAddAnimalSize);
        edtAnimalWeight = findViewById(R.id.edtAddAnimalWeight);
        edtAnimalObservation = findViewById(R.id.edtAddAnimalObservation);

        btnDate = findViewById(R.id.btnAddAnimalDate);
        btnAnimalSave = findViewById(R.id.btnAddAnimalSave);
        btnAnimalEdit = findViewById(R.id.btnAddAnimalEdit);
        btnAnimalDelete = findViewById(R.id.btnAddAnimalDelete);

        btnDate.setOnClickListener(this);
        btnAnimalSave.setOnClickListener(this);
        btnAnimalEdit.setOnClickListener(this);
        btnAnimalDelete.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        try {
            if (Objects.requireNonNull(getIntent().getExtras()).getInt("Query", 0) == 0) {
                toolbar.setTitle(getResources().getString(R.string.txtAddAnimalAddTitle));
            } else {
                toolbar.setTitle(getResources().getString(R.string.txtAddAnimalEditTitle));

                setEdition();
                editable = true;

                btnAnimalEdit.setVisibility(View.VISIBLE);
                btnAnimalDelete.setVisibility(View.VISIBLE);
                pet = (GetPetsResponseStructure.Pet) getIntent().getSerializableExtra("PET_OBJECT");
                if (pet != null) {
                    edtAnimalName.setText(pet.getName());
                    edtAnimalBirthDate.setText(pet.getBirth());
                    edtAnimalSpecies.setText(pet.getSpecies());
                    edtAnimalRace.setText(pet.getBreed());
//                    edtAnimalSize.setText();
                    edtAnimalWeight.setText(pet.getWeight());
                    edtAnimalObservation.setText(pet.getComments());
                }
            }
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Toast.makeText(context, "Houve um erro ao abrir a tela! Tente novamente!", Toast.LENGTH_SHORT).show();
            finish();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_add_animal);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        edtAnimalBirthDate.setInputType(InputType.TYPE_NULL);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddAnimalDate:
                if (!editable) {
                    showDateDialog(edtAnimalBirthDate);
                }
            case R.id.btnAddVaccineSave:
                Toast.makeText(this, "Informações salvas!", Toast.LENGTH_SHORT).show();
            case R.id.btnAddAnimalEdit:
                if (edition == 0) {
                    setEdition();
                    Toast.makeText(this, "Edição desabilitada!", Toast.LENGTH_SHORT).show();
                } else {
                    setEdition();
                    Toast.makeText(this, "Edição habilitada!", Toast.LENGTH_SHORT).show();
                }
            case R.id.btnAddAnimalDelete:
                Toast.makeText(this, "Informações deletadas!", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
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

    private void setEdition() {
        if (edition == 0) {
            edtAnimalName.setEnabled(false);
            edtAnimalBirthDate.setEnabled(false);
            edtAnimalSpecies.setEnabled(false);
            edtAnimalRace.setEnabled(false);
            edtAnimalSize.setEnabled(false);
            edtAnimalWeight.setEnabled(false);
            edtAnimalObservation.setEnabled(false);

            edition = 1;
        } else {
            edtAnimalName.setEnabled(true);
            edtAnimalBirthDate.setEnabled(true);
            edtAnimalSpecies.setEnabled(true);
            edtAnimalRace.setEnabled(true);
            edtAnimalSize.setEnabled(true);
            edtAnimalWeight.setEnabled(true);
            edtAnimalObservation.setEnabled(true);

            edition = 0;
        }
    }
}