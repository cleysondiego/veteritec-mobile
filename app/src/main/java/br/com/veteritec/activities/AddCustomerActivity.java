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

import java.util.Objects;

import br.com.veteritec.R;
import br.com.veteritec.customers.CreateCustomerRequestStructure;
import br.com.veteritec.customers.CreateCustomerUseCase;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.MaskUtils;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class AddCustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Context context;
    private DrawerLayout drawer;
    private int edition = 0;

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
    private Button btnEdit;
    private Button btnDelete;

    private String userToken;
    private String userClinicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        context = getApplicationContext();

        getUserDataFromSharedPreferences();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        edtCustomerName = findViewById(R.id.edtAddCustomerName);
        edtCustomerCpf = findViewById(R.id.edtAddCustomerCPF);
        edtCustomerCep = findViewById(R.id.edtAddCustomerCEP);
        edtCustomerNeighborhood = findViewById(R.id.edtAddCustomerNeighborhood);
        edtCustomerStreet = findViewById(R.id.edtAddCustomerStreet);
        edtCustomerNumber = findViewById(R.id.edtAddCustomerNumber);
        edtCustomerTelephone = findViewById(R.id.edtAddCustomerTelephone);
        edtCustomerCellPhone = findViewById(R.id.edtAddCustomerCellphone);
        edtCustomerEmail = findViewById(R.id.edtAddCustomerEmail);

        edtCustomerCpf.addTextChangedListener(MaskUtils.mask(edtCustomerCpf, MaskUtils.FORMAT_CPF));
        edtCustomerTelephone.addTextChangedListener(MaskUtils.mask(edtCustomerTelephone, MaskUtils.FORMAT_TELEPHONE));
        edtCustomerCellPhone.addTextChangedListener(MaskUtils.mask(edtCustomerCellPhone, MaskUtils.FORMAT_CELLPHONE));
        edtCustomerCep.addTextChangedListener(MaskUtils.mask(edtCustomerCep, MaskUtils.FORMAT_CEP));

        btnSave = findViewById(R.id.btnAddCustomerSave);
        btnEdit = findViewById(R.id.btnAddCustomerEdit);
        btnDelete = findViewById(R.id.btnAddCustomerDelete);

        btnSave.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        try {
            if (Objects.requireNonNull(getIntent().getExtras()).getInt("Query", 0) == 0) {
                toolbar.setTitle(R.string.txtAddCustomerAddTitle);
            } else {
                toolbar.setTitle(R.string.txtAddCustomerEditTitle);

                setEdition();

                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Toast.makeText(context, "Houve um erro ao abrir a tela! Tente novamente!", Toast.LENGTH_SHORT).show();
            finish();
        }

        drawer = findViewById(R.id.drawer_add_customer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_calculator:
                Intent calculator = new Intent(this, CalculatorActivity.class);
                startActivity(calculator);
                finish();
                break;
            case R.id.nav_add_customer:
                break;
            case R.id.nav_query_customer:
                Intent queryCustomer = new Intent(this, QueryActivity.class);
                queryCustomer.putExtra("Choose", 0);
                startActivity(queryCustomer);
                finish();
                break;
            case R.id.nav_add_animal:
                Intent addAnimal = new Intent(this, AddAnimalActivity.class);
                startActivity(addAnimal);
                finish();
                break;
            case R.id.nav_query_animal:
                Intent queryAnimal = new Intent(this, QueryActivity.class);
                queryAnimal.putExtra("Choose", 1);
                startActivity(queryAnimal);
                finish();
                break;
            case R.id.nav_add_vaccine:
                Intent addVaccine = new Intent(this, AddVaccineActivity.class);
                startActivity(addVaccine);
                finish();
                break;
            case R.id.nav_query_vaccine:
                Intent queryVaccine = new Intent(this, QueryActivity.class);
                queryVaccine.putExtra("Choose", 2);
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
        if (v.getId() == R.id.btnAddCustomerSave) {
            if (validateFields()) {
                createCustomer();
            } else {
                Toast.makeText(context, "Por favor, verifique os dados digitados e tente novamente", Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.btnAddCustomerEdit) {
            if (edition == 0) {
                setEdition();
                Toast.makeText(context, "Edição desabilitada!", Toast.LENGTH_SHORT).show();
            }
            else {
                setEdition();
                Toast.makeText(context, "Edição habilitada!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Cliente excluído com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEdition() {
        if (edition == 0) {
            edtCustomerName.setEnabled(false);
            edtCustomerCpf.setEnabled(false);
            edtCustomerCep.setEnabled(false);
            edtCustomerNeighborhood.setEnabled(false);
            edtCustomerStreet.setEnabled(false);
            edtCustomerNumber.setEnabled(false);
            edtCustomerTelephone.setEnabled(false);
            edtCustomerCellPhone.setEnabled(false);
            edtCustomerEmail.setEnabled(false);

            edition = 1;
        } else {
            edtCustomerName.setEnabled(true);
            edtCustomerCpf.setEnabled(true);
            edtCustomerCep.setEnabled(true);
            edtCustomerNeighborhood.setEnabled(true);
            edtCustomerStreet.setEnabled(true);
            edtCustomerNumber.setEnabled(true);
            edtCustomerTelephone.setEnabled(true);
            edtCustomerCellPhone.setEnabled(true);
            edtCustomerEmail.setEnabled(true);

            edition = 0;
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