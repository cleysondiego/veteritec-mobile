package br.com.veteritec.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import br.com.veteritec.R;
import br.com.veteritec.customers.CreateCustomerRequestStructure;
import br.com.veteritec.customers.CreateCustomerUseCase;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class AddClientActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Context context;
    private DrawerLayout drawer;

    private EditText edtCustomerName;
    private EditText edtCustomerCpf;
    private EditText edtCustomerCep;
    private EditText edtCustomerNeighborhood;
    private EditText edtCustomerStreet;
    private EditText edtCustomerNumber;
    private EditText edtCustomerTelephone;
    private EditText edtCustomerCellPhone;
    private EditText edtCustomerEmail;

    private Button btnSave;

    private String userToken;
    private String userClinicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        context = getApplicationContext();

        getUserDataFromSharedPreferences();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txtAddClientTitle);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_add_client);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        edtCustomerName = findViewById(R.id.edtClientName);
        edtCustomerCpf = findViewById(R.id.edtClientCPF);
        edtCustomerCep = findViewById(R.id.edtClientCEP);
        edtCustomerNeighborhood = findViewById(R.id.edtClientNeighborhood);
        edtCustomerStreet = findViewById(R.id.edtClientStreet);
        edtCustomerNumber = findViewById(R.id.edtClientNumber);
        edtCustomerTelephone = findViewById(R.id.edtClientTelephone);
        edtCustomerCellPhone = findViewById(R.id.edtClientCellphone);
        edtCustomerEmail = findViewById(R.id.edtClientEmail);

        btnSave = findViewById(R.id.btnAddCustomerSave);
        btnSave.setOnClickListener(this);
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
                break;
            case R.id.nav_query_client:
                Intent queryClient = new Intent(this, QueryActivity.class);
                queryClient.putExtra("Client", 1);
                startActivity(queryClient);
                finish();
                break;
            case R.id.nav_add_animal:
                Intent addAnimal = new Intent(this, AddAnimalActivity.class);
                startActivity(addAnimal);
                finish();
                break;
            case R.id.nav_add_vaccine:
                Intent addVaccine = new Intent(this, AddVaccineActivity.class);
                startActivity(addVaccine);
                finish();
                break;
            case R.id.nav_query_vaccine:
                Intent queryVaccine = new Intent(this, QueryActivity.class);
                queryVaccine.putExtra("Vaccine", 2);
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

    @Override
    public void onClick(View v) {
        if (validateFields()) {
            createCustomer();
        } else {
            Toast.makeText(context, "Por favor, verifique os dados digitados e tente novamente", Toast.LENGTH_LONG).show();
        }
    }

    public boolean validateFields() {
        return validateField(edtCustomerName) ||
                validateField(edtCustomerCpf) ||
                validateField(edtCustomerCep) ||
                validateField(edtCustomerNeighborhood) ||
                validateField(edtCustomerStreet) ||
                validateField(edtCustomerNumber) ||
                validateField(edtCustomerCellPhone) ||
                validateField(edtCustomerEmail);
    }

    public boolean validateField(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError("Esse campo não pode ser vazio");
            editText.requestFocus();
            return false;
        } else if (editText.getText().toString().length() < 5) {
            editText.setError("O campo deve conter mais que 5 caracteres.");
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private void createCustomer() {
        CreateCustomerRequestStructure createCustomerRequestStructure = new CreateCustomerRequestStructure();
        createCustomerRequestStructure.setName(edtCustomerName.getText().toString());
        createCustomerRequestStructure.setCpf(edtCustomerCpf.getText().toString());
        createCustomerRequestStructure.setNeighborhood(edtCustomerNeighborhood.getText().toString());
        createCustomerRequestStructure.setZipCode(edtCustomerCep.getText().toString());
        createCustomerRequestStructure.setStreet(edtCustomerStreet.getText().toString());
        createCustomerRequestStructure.setNumber(edtCustomerNumber.getText().toString());
        createCustomerRequestStructure.setPhoneNumber(edtCustomerTelephone.getText().toString());
        createCustomerRequestStructure.setCellNumber(edtCustomerCellPhone.getText().toString());
        createCustomerRequestStructure.setEmail(edtCustomerEmail.getText().toString());

        ApiRequest apiRequest = new ApiRequest();
        CreateCustomerUseCase createCustomerUseCase = new CreateCustomerUseCase(ThreadExecutor.getInstance(), apiRequest, createCustomerRequestStructure, userClinicId, userToken);
        createCustomerUseCase.setCallback(new CreateCustomerUseCase.OnCreateCustomer() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Cliente adicionado com sucesso!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, "Não foi possível adicionar o cliente nesse momento, por favor, tente novamente!", Toast.LENGTH_LONG).show();
            }
        });
        createCustomerUseCase.execute();
    }

    private void getUserDataFromSharedPreferences() {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        userToken = sharedPreferencesUtils.getUserToken(context);
        userClinicId = sharedPreferencesUtils.getUserClinicId(context);
    }
}