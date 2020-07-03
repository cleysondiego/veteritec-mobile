package br.com.veteritec.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.com.veteritec.R;
import br.com.veteritec.customers.GetCustomersResponseStructure;
import br.com.veteritec.customers.GetCustomersUseCase;
import br.com.veteritec.pets.GetPetsResponseStructure;
import br.com.veteritec.pets.GetPetsUseCase;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;
import br.com.veteritec.vaccines.GetVaccinesResponseStructure;
import br.com.veteritec.vaccines.GetVaccinesUseCase;

public class QueryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {
    private Context context;
    private DrawerLayout drawer;
    private int key;

    private String userToken = "";
    private String userClinicId = "";

    private GetCustomersResponseStructure getCustomersResponseStructure;
    private GetPetsResponseStructure getPetsResponseStructure;
    private GetVaccinesResponseStructure getVaccinesResponseStructure;

    private EditText edtSearch;
    private ListView lvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        Toolbar toolbar = findViewById(R.id.toolbar);

        context = getApplicationContext();

        SharedPreferences language = getSharedPreferences("Language", MODE_PRIVATE);

        Locale locale = new Locale(language.getString("ChoosedLang", "pt"));
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        getCustomersResponseStructure = new GetCustomersResponseStructure();
        getPetsResponseStructure = new GetPetsResponseStructure();
        getVaccinesResponseStructure = new GetVaccinesResponseStructure();

        edtSearch = findViewById(R.id.edtCustomer);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        lvResult = findViewById(R.id.lvResult);

        getUserDataFromSharedPreferences(context);

        key = Objects.requireNonNull(getIntent().getExtras()).getInt("Choose", 0);
        if (key == 0) {
            toolbar.setTitle(getResources().getString(R.string.txtQueryCustomerTitle));
            getCustomers();
        } else if (key == 1) {
            toolbar.setTitle(getResources().getString(R.string.txtQueryAnimalTitle));
            getPets();
        } else if (key == 2) {
            toolbar.setTitle(getResources().getString(R.string.txtQueryVaccineTitle));
            getVaccines();
        } else {
            Toast.makeText(context, getResources().getString(R.string.toastQueryError), Toast.LENGTH_SHORT).show();
            finish();
        }

        lvResult.setOnItemClickListener(this);

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_query);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (key == 0) {
            getCustomers();
        } else if (key == 1) {
            getPets();
        } else if (key == 2) {
            getVaccines();
        } else {
            Toast.makeText(context, getResources().getString(R.string.toastQueryError), Toast.LENGTH_SHORT).show();
            finish();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (key == 0) {
            goToCustomerActivity(parent, position);
        } else if (key == 1) {
            goToPetsActivity(parent, position);
        } else if (key == 2) {
            goToVaccinesActivity(parent, position);
        }
    }

    private void getUserDataFromSharedPreferences(Context context) {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        userToken = sharedPreferencesUtils.getUserToken(context);
        userClinicId = sharedPreferencesUtils.getUserClinicId(context);
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
                Toast.makeText(context, getResources().getString(R.string.toastQueryCustomersRequestError), Toast.LENGTH_SHORT).show();
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
        lvResult.setAdapter(arrayAdapter);
    }

    public void goToCustomerActivity(AdapterView<?> parent, int position) {
        String customerName = parent.getItemAtPosition(position).toString();

        List<GetCustomersResponseStructure.Customer> customerList = getCustomersResponseStructure.getCustomersList();

        GetCustomersResponseStructure.Customer customer = new GetCustomersResponseStructure.Customer();

        for (GetCustomersResponseStructure.Customer customers : customerList) {
            if (customers.getName().equals(customerName)) {
                customer = customers;
            }
        }

        Intent intent = new Intent(this, AddCustomerActivity.class);
        intent.putExtra("CUSTOMER_OBJECT", customer);
        intent.putExtra("Query", 1);
        startActivity(intent);
    }

    private void filter(String filter) {
        List<String> filteredName = new ArrayList<>();

        if (key == 0) {
            for (GetCustomersResponseStructure.Customer customer : getCustomersResponseStructure.getCustomersList()) {
                if (customer.getName().toLowerCase().contains(filter.toLowerCase())) {
                    filteredName.add(customer.getName());
                }
            }
        } else if (key == 1) {
            for (GetPetsResponseStructure.Pet pet : getPetsResponseStructure.getPets()) {
                if (pet.getName().toLowerCase().contains(filter.toLowerCase())) {
                    filteredName.add(pet.getName());
                }
            }
        } else if (key == 2) {
            for (GetVaccinesResponseStructure.Vaccine vaccine : getVaccinesResponseStructure.getVaccineList()) {
                if (vaccine.getDisplayName().toLowerCase().contains(filter.toLowerCase())) {
                    filteredName.add(vaccine.getDisplayName());
                }
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredName);
        lvResult.setAdapter(arrayAdapter);
    }

    private void getPets() {
        ApiRequest apiRequest = new ApiRequest();

        GetPetsUseCase getPetsUseCase = new GetPetsUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId, userToken);
        getPetsUseCase.setCallback(new GetPetsUseCase.OnGetPetsCallback() {
            @Override
            public void onSuccess(GetPetsResponseStructure petsResponseStructure) {
                getPetsResponseStructure = petsResponseStructure;
                populatePetsListView(getPetsResponseStructure.getPets());
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, getResources().getString(R.string.toastQueryPetsRequestError), Toast.LENGTH_SHORT).show();
            }
        });

        getPetsUseCase.execute();
    }

    public void populatePetsListView(List<GetPetsResponseStructure.Pet> petList) {
        List<String> name = new ArrayList<>();

        for (GetPetsResponseStructure.Pet pet : petList) {
            name.add(pet.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        lvResult.setAdapter(arrayAdapter);
    }

    public void goToPetsActivity(AdapterView<?> parent, int position) {
        String petName = parent.getItemAtPosition(position).toString();

        List<GetPetsResponseStructure.Pet> petList = getPetsResponseStructure.getPets();

        GetPetsResponseStructure.Pet pet = new GetPetsResponseStructure.Pet();

        for (GetPetsResponseStructure.Pet pets : petList) {
            if (pets.getName().equals(petName)) {
                pet = pets;
            }
        }

        Intent intent = new Intent(this, AddAnimalActivity.class);
        intent.putExtra("PET_OBJECT", pet);
        intent.putExtra("Query", 1);
        startActivity(intent);
    }

    public void getVaccines() {
        ApiRequest apiRequest = new ApiRequest();

        GetVaccinesUseCase getVaccinesUseCase = new GetVaccinesUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId, userToken);
        getVaccinesUseCase.setCallback(new GetVaccinesUseCase.OnGetVaccinesCallback() {
            @Override
            public void onSuccess(GetVaccinesResponseStructure vaccinesResponseStructure) {
                getVaccinesResponseStructure = vaccinesResponseStructure;
                populateVaccinesListView(getVaccinesResponseStructure.getVaccineList());
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, getResources().getString(R.string.toastQueryVaccinesRequestError), Toast.LENGTH_SHORT).show();
            }
        });

        getVaccinesUseCase.execute();
    }

    public void populateVaccinesListView(List<GetVaccinesResponseStructure.Vaccine> vaccineList) {
        List<String> name = new ArrayList<>();

        for (GetVaccinesResponseStructure.Vaccine vaccine : vaccineList) {
            name.add(vaccine.getDisplayName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        lvResult.setAdapter(arrayAdapter);
    }

    public void goToVaccinesActivity(AdapterView<?> parent, int position) {
        String vaccineDisplayName = parent.getItemAtPosition(position).toString();

        List<GetVaccinesResponseStructure.Vaccine> vaccineList = getVaccinesResponseStructure.getVaccineList();

        GetVaccinesResponseStructure.Vaccine vaccine = new GetVaccinesResponseStructure.Vaccine();

        for (GetVaccinesResponseStructure.Vaccine vaccines : vaccineList) {
            if (vaccines.getDisplayName().equals(vaccineDisplayName)) {
                vaccine = vaccines;
            }
        }

        Intent intent = new Intent(this, AddVaccineActivity.class);
        intent.putExtra("VACCINE_OBJECT", vaccine);
        intent.putExtra("Query", 1);
        startActivity(intent);
    }
}