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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.com.veteritec.R;
import br.com.veteritec.customers.GetCustomersResponseStructure;
import br.com.veteritec.customers.GetCustomersUseCase;
import br.com.veteritec.employees.GetEmployeesResponseStructure;
import br.com.veteritec.employees.GetEmployeesUseCase;
import br.com.veteritec.pets.GetPetsByCustomerUseCase;
import br.com.veteritec.pets.GetPetsResponseStructure;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;
import br.com.veteritec.vaccines.CreateVaccineRequestStructure;
import br.com.veteritec.vaccines.CreateVaccineUseCase;

public class AddVaccineActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
    private DrawerLayout drawer;
    private Context context;

    private String userToken = "";
    private String userClinicId = "";
    private String selectedCustomerId = "";

    private GetEmployeesResponseStructure getEmployeesResponseStructure;
    private GetCustomersResponseStructure getCustomersResponseStructure;
    private GetPetsResponseStructure getPetsResponseStructure;

    private Spinner spnAddVaccineVeterinary;
    private Spinner spnAddVaccineClient;
    private Spinner spnAddVaccinePet;

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

        SharedPreferences language = getSharedPreferences("Language", MODE_PRIVATE);

        getUserDataFromSharedPreferences();

        getEmployeesResponseStructure = new GetEmployeesResponseStructure();
        getCustomersResponseStructure = new GetCustomersResponseStructure();
        getPetsResponseStructure = new GetPetsResponseStructure();

        Locale locale = new Locale(language.getString("ChoosedLang", "pt"));
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.txtAddVaccineTitle));
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_add_vaccine);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        spnAddVaccineVeterinary = findViewById(R.id.spnAddVaccineVeterinary);
        spnAddVaccineClient = findViewById(R.id.spnAddVaccineClient);
        spnAddVaccinePet = findViewById(R.id.spnAddVaccineAnimal);

        spnAddVaccineClient.setOnItemSelectedListener(this);

        edtDate = findViewById(R.id.edtAddVaccineDate);
        edtTime = findViewById(R.id.edtAddVaccineTime);
        edtDescription = findViewById(R.id.edtAddVaccineDescription);

        edtDate.setInputType(InputType.TYPE_NULL);
        edtTime.setInputType(InputType.TYPE_NULL);

        btnTime = findViewById(R.id.btnAddVaccineTime);
        btnDate = findViewById(R.id.btnAddVaccineDate);
        btnSave = findViewById(R.id.btnAddVaccineSave);

        getEmployees();
        getCustomers();

        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddVaccineDate:
                showDateDialog(edtDate);
                break;
            case R.id.btnAddVaccineTime:
                showTimeDialog(edtTime);
                break;
            case R.id.btnAddVaccineSave:
                createVaccine();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String customerName = parent.getItemAtPosition(position).toString();

        List<GetCustomersResponseStructure.Customer> customerList = getCustomersResponseStructure.getCustomersList();

        GetCustomersResponseStructure.Customer customer = new GetCustomersResponseStructure.Customer();

        for (GetCustomersResponseStructure.Customer customers : customerList) {
            if (customers.getName().equals(customerName)) {
                customer = customers;
            }
        }

        selectedCustomerId = customer.getId();
        getPets();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getUserDataFromSharedPreferences() {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        userToken = sharedPreferencesUtils.getUserToken(context);
        userClinicId = sharedPreferencesUtils.getUserClinicId(context);
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

    private void getEmployees() {
        ApiRequest apiRequest = new ApiRequest();

        GetEmployeesUseCase getEmployeesUseCase = new GetEmployeesUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId);
        getEmployeesUseCase.setCallback(new GetEmployeesUseCase.OnGetEmployeesCallback() {
            @Override
            public void onSuccess(GetEmployeesResponseStructure employeesResponseStructure) {
                getEmployeesResponseStructure = employeesResponseStructure;
                populateSpinnerEmployees(getEmployeesResponseStructure.getEmployeeList());
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, "Erro ao obter a lista de Veterinários, por favor, tente novamente!", Toast.LENGTH_SHORT).show();
            }
        });

        getEmployeesUseCase.execute();
    }

    private void populateSpinnerEmployees(List<GetEmployeesResponseStructure.Employee> employeeList) {
        List<String> name = new ArrayList<>();

        for (GetEmployeesResponseStructure.Employee employee : employeeList) {
            name.add(employee.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        spnAddVaccineVeterinary.setAdapter(arrayAdapter);
    }

    public void getCustomers() {
        ApiRequest apiRequest = new ApiRequest();

        GetCustomersUseCase getCustomersUseCase = new GetCustomersUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId, userToken);
        getCustomersUseCase.setCallback(new GetCustomersUseCase.OnGetCustomersCallback() {
            @Override
            public void onSuccess(GetCustomersResponseStructure customersResponseStructure) {
                getCustomersResponseStructure = customersResponseStructure;
                populateCustomerListView(getCustomersResponseStructure.getCustomersList());
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, "Erro ao obter a lista de Clientes, por favor, tente novamente!", Toast.LENGTH_SHORT).show();
            }
        });

        getCustomersUseCase.execute();
    }

    public void populateCustomerListView(List<GetCustomersResponseStructure.Customer> customerList) {
        List<String> name = new ArrayList<>();

        for (GetCustomersResponseStructure.Customer customer : customerList) {
            name.add(customer.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        spnAddVaccineClient.setAdapter(arrayAdapter);

        for (GetCustomersResponseStructure.Customer customer : customerList) {
            if (customer.getName().equals(spnAddVaccineClient.getSelectedItem().toString())) {
                selectedCustomerId = customer.getId();
            }
        }

        getPets();
    }

    private void getPets() {
        ApiRequest apiRequest = new ApiRequest();

        GetPetsByCustomerUseCase getPetsByCustomerUseCase = new GetPetsByCustomerUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId, userToken, selectedCustomerId);
        getPetsByCustomerUseCase.setCallback(new GetPetsByCustomerUseCase.OnGetPetsByCustomerCallback() {
            @Override
            public void onSuccess(GetPetsResponseStructure petsResponseStructure) {
                getPetsResponseStructure = petsResponseStructure;
                populatePetsListView(getPetsResponseStructure.getPets());
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, "Erro ao obter a lista de Pets, por favor, tente novamente!", Toast.LENGTH_SHORT).show();
            }
        });

        getPetsByCustomerUseCase.execute();
    }

    public void populatePetsListView(List<GetPetsResponseStructure.Pet> petList) {
        List<String> name = new ArrayList<>();

        for (GetPetsResponseStructure.Pet pet : petList) {
            name.add(pet.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        spnAddVaccinePet.setAdapter(arrayAdapter);
    }

    private void createVaccine() {
        CreateVaccineRequestStructure createVaccineRequestStructure = new CreateVaccineRequestStructure();
        createVaccineRequestStructure.setDate(edtDate.getText().toString());
        createVaccineRequestStructure.setHour(edtTime.getText().toString());
        createVaccineRequestStructure.setDescription(edtDescription.getText().toString());
        createVaccineRequestStructure.setCustomer(selectedCustomerId);
        createVaccineRequestStructure.setVeterinary(getSelectedVeterinaryId());
        createVaccineRequestStructure.setPet(getSelectedPetId());

        ApiRequest apiRequest = new ApiRequest();
        CreateVaccineUseCase createVaccineUseCase = new CreateVaccineUseCase(ThreadExecutor.getInstance(), apiRequest, createVaccineRequestStructure, userClinicId, userToken);
        createVaccineUseCase.setCallback(new CreateVaccineUseCase.OnCreateVaccineCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Nova vacina criada com sucesso!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, "Não foi possível criar a vacina! Tente novamente!", Toast.LENGTH_LONG).show();
            }
        });

        createVaccineUseCase.execute();
    }

    public String getSelectedVeterinaryId() {
        for (GetEmployeesResponseStructure.Employee employee : getEmployeesResponseStructure.getEmployeeList()) {
            if (employee.getName().equals(spnAddVaccineVeterinary.getSelectedItem().toString())) {
                return employee.getId();
            }
        }
        return "";
    }

    public String getSelectedPetId() {
        for (GetPetsResponseStructure.Pet pet : getPetsResponseStructure.getPets()) {
            if (pet.getName().equals(spnAddVaccinePet.getSelectedItem().toString())) {
                return pet.getId();
            }
        }
        return "";
    }
}
