package br.com.veteritec.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import br.com.veteritec.pets.ChangePetRequestStructure;
import br.com.veteritec.pets.ChangePetUseCase;
import br.com.veteritec.pets.CreatePetRequestStructure;
import br.com.veteritec.pets.CreatePetUseCase;
import br.com.veteritec.pets.DeletePetUseCase;
import br.com.veteritec.pets.GetPetsResponseStructure;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.LoadingDialog;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class AddAnimalActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    private int edition = 0;
    private boolean editable = false;

    private String userToken = "";
    private String userClinicId = "";

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

    Spinner spnAnimalOwner;

    private GetPetsResponseStructure.Pet pet;
    private GetCustomersResponseStructure getCustomersResponseStructure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);

        context = getApplicationContext();

        getUserDataFromSharedPreferences(context);

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

        spnAnimalOwner = findViewById(R.id.spnAddAnimalOwner);

        getCustomers();

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
            }
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Toast.makeText(context, getResources().getString(R.string.toastAddAnimalError), Toast.LENGTH_SHORT).show();
            finish();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
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
                showDateDialog(edtAnimalBirthDate);
                break;
            case R.id.btnAddAnimalSave:
                if (validateFields()) {
                    if (!editable) {
                        createPet();
                    } else {
                        changePet();
                    }
                }
                break;
            case R.id.btnAddAnimalEdit:
                if (edition == 0) {
                    setEdition();
                    Toast.makeText(this, getResources().getString(R.string.toastAddAnimalDisabledEdition), Toast.LENGTH_SHORT).show();
                } else {
                    setEdition();
                    Toast.makeText(this, getResources().getString(R.string.toastAddAnimalEnabledEdition), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnAddAnimalDelete:
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);

                confirmDialog.setMessage(getResources().getString(R.string.txtConfirm))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.txtConfirmYes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deletePet();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.txtConfirmNo), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = confirmDialog.create();
                dialog.show();
                break;
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                edtAnimalBirthDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
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
            spnAnimalOwner.setEnabled(false);
            btnDate.setEnabled(false);

            edition = 1;
        } else {
            edtAnimalName.setEnabled(true);
            edtAnimalBirthDate.setEnabled(true);
            edtAnimalSpecies.setEnabled(true);
            edtAnimalRace.setEnabled(true);
            edtAnimalSize.setEnabled(true);
            edtAnimalWeight.setEnabled(true);
            edtAnimalObservation.setEnabled(true);
            spnAnimalOwner.setEnabled(true);
            btnDate.setEnabled(true);

            edition = 0;
        }
    }

    private void getUserDataFromSharedPreferences(Context context) {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        userToken = sharedPreferencesUtils.getUserToken(context);
        userClinicId = sharedPreferencesUtils.getUserClinicId(context);
    }

    private void getCustomers() {
        ApiRequest apiRequest = new ApiRequest();
        GetCustomersUseCase getCustomersUseCase = new GetCustomersUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId, userToken);
        getCustomersUseCase.setCallback(new GetCustomersUseCase.OnGetCustomersCallback() {
            @Override
            public void onSuccess(GetCustomersResponseStructure customersResponseStructure) {
                getCustomersResponseStructure = customersResponseStructure;
                populateCustomersSpinner(getCustomersResponseStructure.getCustomersList());

                if (editable) {
                    setPetFields();
                }
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, getResources().getString(R.string.toastAddAnimalQueryCustomersError), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        getCustomersUseCase.execute();
    }

    private void setPetFields() {
        pet = (GetPetsResponseStructure.Pet) getIntent().getSerializableExtra("PET_OBJECT");
        if (pet != null) {
            edtAnimalName.setText(pet.getName());
            edtAnimalBirthDate.setText(pet.getBirth());
            edtAnimalSpecies.setText(pet.getSpecies());
            edtAnimalRace.setText(pet.getBreed());
            edtAnimalSize.setText(pet.getSize());
            edtAnimalWeight.setText(pet.getWeight());
            edtAnimalObservation.setText(pet.getComments());

            List<GetCustomersResponseStructure.Customer> customerList = getCustomersResponseStructure.getCustomersList();
            for (int i = 0; i < customerList.size(); i++) {
                if (customerList.get(i).getId().equals(pet.getCustomer())) {
                    spnAnimalOwner.setSelection(i, true);
                }
            }
        }
    }

    private void populateCustomersSpinner(List<GetCustomersResponseStructure.Customer> customerList) {
        List<String> name = new ArrayList<>();

        for (GetCustomersResponseStructure.Customer customer : customerList) {
            name.add(customer.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        spnAnimalOwner.setAdapter(arrayAdapter);
    }

    private void createPet() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        CreatePetRequestStructure createPetRequestStructure = new CreatePetRequestStructure();
        createPetRequestStructure.setName(edtAnimalName.getText().toString());
        createPetRequestStructure.setBirth(edtAnimalBirthDate.getText().toString());
        createPetRequestStructure.setSpecies(edtAnimalSpecies.getText().toString());
        createPetRequestStructure.setBreed(edtAnimalRace.getText().toString());
        createPetRequestStructure.setSize(edtAnimalSize.getText().toString());
        createPetRequestStructure.setWeight(edtAnimalWeight.getText().toString());
        createPetRequestStructure.setComments(edtAnimalObservation.getText().toString());

        String customerId = "";

        for (GetCustomersResponseStructure.Customer customer : getCustomersResponseStructure.getCustomersList()) {
            if (customer.getName().equals(spnAnimalOwner.getSelectedItem().toString())) {
                customerId = customer.getId();
                break;
            }
        }

        createPetRequestStructure.setCustomer(customerId);

        ApiRequest apiRequest = new ApiRequest();
        CreatePetUseCase createPetUseCase = new CreatePetUseCase(ThreadExecutor.getInstance(), apiRequest, createPetRequestStructure, userClinicId, userToken);
        createPetUseCase.setCallback(new CreatePetUseCase.OnCreatePet() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddAnimalSuccesfullyAddedAnimal), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddAnimalFailureAddedAnimal), Toast.LENGTH_LONG).show();
            }
        });

        createPetUseCase.execute();
    }

    private void changePet() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        ChangePetRequestStructure changePetRequestStructure = new ChangePetRequestStructure();
        changePetRequestStructure.setId(pet.getId());
        changePetRequestStructure.setName(edtAnimalName.getText().toString());
        changePetRequestStructure.setBirth(edtAnimalBirthDate.getText().toString());
        changePetRequestStructure.setSpecies(edtAnimalSpecies.getText().toString());
        changePetRequestStructure.setBreed(edtAnimalRace.getText().toString());
        changePetRequestStructure.setSize(edtAnimalSize.getText().toString());
        changePetRequestStructure.setWeight(edtAnimalWeight.getText().toString());
        changePetRequestStructure.setComments(edtAnimalObservation.getText().toString());

        String customerId = "";

        for (GetCustomersResponseStructure.Customer customer : getCustomersResponseStructure.getCustomersList()) {
            if (customer.getName().equals(spnAnimalOwner.getSelectedItem().toString())) {
                customerId = customer.getId();
                break;
            }
        }

        changePetRequestStructure.setCustomer(customerId);

        ApiRequest apiRequest = new ApiRequest();
        ChangePetUseCase changePetUseCase = new ChangePetUseCase(ThreadExecutor.getInstance(), apiRequest, changePetRequestStructure, userClinicId, userToken);
        changePetUseCase.setCallback(new ChangePetUseCase.OnChangePet() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddAnimalSuccesfullyEditedAnimal), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddAnimalFailureEditedAnimal), Toast.LENGTH_LONG).show();
            }
        });

        changePetUseCase.execute();
    }

    private void deletePet() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        ApiRequest apiRequest = new ApiRequest();
        DeletePetUseCase deletePetUseCase = new DeletePetUseCase(ThreadExecutor.getInstance(), apiRequest, pet.getId(), userClinicId, userToken);
        deletePetUseCase.setCallback(new DeletePetUseCase.OnDeletePetCallback() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddAnimalSuccesfullyDeletedAnimal), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddAnimalFailureDeletedAnimal), Toast.LENGTH_LONG).show();
            }
        });

        deletePetUseCase.execute();
    }

    public boolean validateFields() {
        return validateField(edtAnimalName) &&
                validateField(edtAnimalBirthDate) &&
                validateField(edtAnimalSpecies) &&
                validateField(edtAnimalRace) &&
                validateField(edtAnimalSize) &&
                validateField(edtAnimalWeight) &&
                validateField(edtAnimalObservation);
    }

    public boolean validateField(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getResources().getString(R.string.setErrorEmptyField));
            editText.requestFocus();
            return false;
        }
        return true;
    }
}