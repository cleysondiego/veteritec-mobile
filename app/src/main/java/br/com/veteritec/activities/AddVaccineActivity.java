package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.com.veteritec.R;
import br.com.veteritec.customers.GetCustomersResponseStructure;
import br.com.veteritec.customers.GetCustomersUseCase;
import br.com.veteritec.employees.GetEmployeesResponseStructure;
import br.com.veteritec.employees.GetEmployeesUseCase;
import br.com.veteritec.pets.GetPetsByCustomerUseCase;
import br.com.veteritec.pets.GetPetsResponseStructure;
import br.com.veteritec.usecase.DeleteStructure;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.LoadingDialog;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;
import br.com.veteritec.vaccines.ChangeVaccineRequestStructure;
import br.com.veteritec.vaccines.ChangeVaccineUseCase;
import br.com.veteritec.vaccines.CreateVaccineRequestStructure;
import br.com.veteritec.vaccines.CreateVaccineUseCase;
import br.com.veteritec.vaccines.DeleteVaccineUseCase;
import br.com.veteritec.vaccines.GetVaccinesResponseStructure;

public class AddVaccineActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
    private DrawerLayout drawer;
    private Context context;

    private int edition = 0;
    private boolean editable = false;

    private String userToken = "";
    private String userClinicId = "";
    private String selectedCustomerId = "";

    private GetEmployeesResponseStructure getEmployeesResponseStructure;
    private GetCustomersResponseStructure getCustomersResponseStructure;
    private GetPetsResponseStructure getPetsResponseStructure;
    private GetVaccinesResponseStructure.Vaccine vaccine;

    private Spinner spnAddVaccineVeterinary;
    private Spinner spnAddVaccineClient;
    private Spinner spnAddVaccinePet;

    private EditText edtDate;
    private EditText edtTime;
    private EditText edtDescription;
    private EditText message;

    private Button btnTime;
    private Button btnDate;
    private Button btnSave;
    private Button btnEdit;
    private Button btnDelete;

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

        btnTime = findViewById(R.id.btnAddVaccineTime);
        btnDate = findViewById(R.id.btnAddVaccineDate);
        btnSave = findViewById(R.id.btnAddVaccineSave);
        btnEdit = findViewById(R.id.btnAddVaccineEdit);
        btnDelete = findViewById(R.id.btnAddVaccineDelete);

        edtDate = findViewById(R.id.edtAddVaccineDate);
        edtTime = findViewById(R.id.edtAddVaccineTime);
        edtDescription = findViewById(R.id.edtAddVaccineDescription);

        spnAddVaccineVeterinary = findViewById(R.id.spnAddVaccineVeterinary);
        spnAddVaccineClient = findViewById(R.id.spnAddVaccineClient);
        spnAddVaccinePet = findViewById(R.id.spnAddVaccineAnimal);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Toolbar toolbar = findViewById(R.id.toolbar);

        try {
            if (Objects.requireNonNull(getIntent().getExtras()).getInt("Query", 0) == 0) {
                toolbar.setTitle(R.string.txtAddVaccineAddTitle);
            } else {
                toolbar.setTitle(R.string.txtAddVaccineEditTitle);

                setEdition();
                editable = true;

                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                spnAddVaccineClient.setEnabled(false);
                vaccine = (GetVaccinesResponseStructure.Vaccine) getIntent().getSerializableExtra("VACCINE_OBJECT");
                if (vaccine != null) {
                    edtDate.setText(vaccine.getDate());
                    edtTime.setText(vaccine.getHour());
                    edtDescription.setText(vaccine.getDescription());
                    message = new EditText(AddVaccineActivity.this);
                }
            }
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Toast.makeText(context, getResources().getString(R.string.toastAddVaccineError), Toast.LENGTH_LONG).show();
            finish();
        }

        drawer = findViewById(R.id.drawer_add_vaccine);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        spnAddVaccineClient.setOnItemSelectedListener(this);

        edtDate.setInputType(InputType.TYPE_NULL);
        edtTime.setInputType(InputType.TYPE_NULL);

        getEmployees();
        getCustomers();

        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
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
                if (!editable) {
                    createVaccine();
                } else {
                    changeVaccine();
                }
                break;
            case R.id.btnAddVaccineEdit:
                if (edition == 0) {
                    setEdition();
                    Toast.makeText(context, R.string.toastAddCustomerDisabledEdition, Toast.LENGTH_SHORT).show();
                } else {
                    getPets();
                    setEdition();
                    Toast.makeText(context, R.string.toastAddCustomerEnabledEdition, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnAddVaccineDelete:
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);

                confirmDialog.setMessage(getResources().getString(R.string.txtConfirm))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.txtConfirmYes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteVaccine(message.getText().toString());
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.txtConfirmNo), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = confirmDialog.create();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                message.setLayoutParams(lp);
                dialog.setView(message);
                dialog.show();
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

    private void setEdition() {
        if (edition == 0) {
            edtDate.setEnabled(false);
            edtTime.setEnabled(false);
            edtDescription.setEnabled(false);
            spnAddVaccinePet.setEnabled(false);
            spnAddVaccineVeterinary.setEnabled(false);

            edition = 1;
        } else {
            edtDate.setEnabled(true);
            edtTime.setEnabled(true);
            edtDescription.setEnabled(true);
            spnAddVaccinePet.setEnabled(true);
            spnAddVaccineVeterinary.setEnabled(true);

            edition = 0;
        }
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                edtDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void getEmployees() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        ApiRequest apiRequest = new ApiRequest();

        GetEmployeesUseCase getEmployeesUseCase = new GetEmployeesUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId);
        getEmployeesUseCase.setCallback(new GetEmployeesUseCase.OnGetEmployeesCallback() {
            @Override
            public void onSuccess(GetEmployeesResponseStructure employeesResponseStructure) {
                loadingDialog.dismissLoadingDialog();
                getEmployeesResponseStructure = employeesResponseStructure;
                populateSpinnerEmployees(getEmployeesResponseStructure.getEmployeeList());
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineGetEmployeesFailure), Toast.LENGTH_SHORT).show();
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

    private void getCustomers() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        ApiRequest apiRequest = new ApiRequest();

        GetCustomersUseCase getCustomersUseCase = new GetCustomersUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId, userToken);
        getCustomersUseCase.setCallback(new GetCustomersUseCase.OnGetCustomersCallback() {
            @Override
            public void onSuccess(GetCustomersResponseStructure customersResponseStructure) {
                loadingDialog.dismissLoadingDialog();
                getCustomersResponseStructure = customersResponseStructure;
                populateCustomerListView(getCustomersResponseStructure.getCustomersList());
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineGetCustomersFailure), Toast.LENGTH_SHORT).show();
            }
        });

        getCustomersUseCase.execute();
    }

    private void populateCustomerListView(List<GetCustomersResponseStructure.Customer> customerList) {
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
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        ApiRequest apiRequest = new ApiRequest();

        GetPetsByCustomerUseCase getPetsByCustomerUseCase = new GetPetsByCustomerUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId, userToken, selectedCustomerId);
        getPetsByCustomerUseCase.setCallback(new GetPetsByCustomerUseCase.OnGetPetsByCustomerCallback() {
            @Override
            public void onSuccess(GetPetsResponseStructure petsResponseStructure) {
                loadingDialog.dismissLoadingDialog();
                getPetsResponseStructure = petsResponseStructure;
                populatePetsListView(getPetsResponseStructure.getPets());
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineGetPetsFailure), Toast.LENGTH_SHORT).show();
            }
        });

        getPetsByCustomerUseCase.execute();
    }

    private void populatePetsListView(List<GetPetsResponseStructure.Pet> petList) {
        List<String> name = new ArrayList<>();

        for (GetPetsResponseStructure.Pet pet : petList) {
            name.add(pet.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        spnAddVaccinePet.setAdapter(arrayAdapter);

        if (editable) {
            setFieldsByVaccine();
        }
    }

    private void setFieldsByVaccine() {
        List<GetCustomersResponseStructure.Customer> customerList = getCustomersResponseStructure.getCustomersList();
        List<GetEmployeesResponseStructure.Employee> employeeList = getEmployeesResponseStructure.getEmployeeList();
        List<GetPetsResponseStructure.Pet> petList = getPetsResponseStructure.getPets();

        for (int i = 0; i < customerList.size(); i++) {
            if (customerList.get(i).getId().equals(vaccine.getCustomer())) {
                spnAddVaccineClient.setSelection(i, true);
            }
        }

        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getId().equals(vaccine.getVeterinary())) {
                spnAddVaccineVeterinary.setSelection(i, true);
            }
        }

        for (int i = 0; i < petList.size(); i++) {
            if (petList.get(i).getId().equals(vaccine.getPet())) {
                spnAddVaccinePet.setSelection(i, true);
            }
        }
    }

    private void createVaccine() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

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
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineSuccesfullyAdd), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineFailureAdd), Toast.LENGTH_LONG).show();
            }
        });

        createVaccineUseCase.execute();
    }

    private String getSelectedVeterinaryId() {
        for (GetEmployeesResponseStructure.Employee employee : getEmployeesResponseStructure.getEmployeeList()) {
            if (employee.getName().equals(spnAddVaccineVeterinary.getSelectedItem().toString())) {
                return employee.getId();
            }
        }
        return "";
    }

    private String getSelectedPetId() {
        for (GetPetsResponseStructure.Pet pet : getPetsResponseStructure.getPets()) {
            if (pet.getName().equals(spnAddVaccinePet.getSelectedItem().toString())) {
                return pet.getId();
            }
        }
        return "";
    }

    private void changeVaccine() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        ChangeVaccineRequestStructure changeVaccineRequestStructure = new ChangeVaccineRequestStructure();
        changeVaccineRequestStructure.setId(vaccine.getId());
        changeVaccineRequestStructure.setDate(edtDate.getText().toString());
        changeVaccineRequestStructure.setHour(edtTime.getText().toString());
        changeVaccineRequestStructure.setDescription(edtDescription.getText().toString());
        changeVaccineRequestStructure.setCustomer(selectedCustomerId);
        changeVaccineRequestStructure.setVeterinary(getSelectedVeterinaryId());
        changeVaccineRequestStructure.setPet(getSelectedPetId());

        ApiRequest apiRequest = new ApiRequest();

        ChangeVaccineUseCase changeVaccineUseCase = new ChangeVaccineUseCase(ThreadExecutor.getInstance(), apiRequest, changeVaccineRequestStructure, userClinicId, userToken);
        changeVaccineUseCase.setCallback(new ChangeVaccineUseCase.OnChangeVaccine() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineSuccesfullyEdit), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineFailureEdit), Toast.LENGTH_LONG).show();
            }
        });

        changeVaccineUseCase.execute();
    }

    private void deleteVaccine(String message) {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        DeleteStructure deleteStructure = new DeleteStructure();
        deleteStructure.setMessage(message);

        ApiRequest apiRequest = new ApiRequest();
        DeleteVaccineUseCase deleteVaccineUseCase = new DeleteVaccineUseCase(ThreadExecutor.getInstance(), apiRequest, deleteStructure, vaccine.getId(), userClinicId, userToken);
        deleteVaccineUseCase.setCallback(new DeleteVaccineUseCase.OnDeleteVaccineCallback() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineSuccesfullyDelete), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddVaccineFailureDelete), Toast.LENGTH_LONG).show();
            }
        });

        deleteVaccineUseCase.execute();
    }
}
