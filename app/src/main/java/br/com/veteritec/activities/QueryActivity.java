package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.veteritec.R;
import br.com.veteritec.clinics.ClinicResponseStructure;
import br.com.veteritec.customers.GetCustomersResponseStructure;
import br.com.veteritec.customers.GetCustomersUseCase;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class QueryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private Context context;
    private DrawerLayout drawer;
    private int key;

    private String userToken = "";
    private String userClinicId = "";

    private GetCustomersResponseStructure getCustomersResponseStructure;

    private EditText edtSearch;
    private Button btnSearch;
    private ListView lvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        Toolbar toolbar = findViewById(R.id.toolbar);

        context = getApplicationContext();

        getCustomersResponseStructure = new GetCustomersResponseStructure();

        edtSearch = findViewById(R.id.edtCustomer);
        btnSearch = findViewById(R.id.btnSearch);
        lvResult = findViewById(R.id.lvResult);

        btnSearch.setOnClickListener(this);

        getUserDataFromSharedPreferences(context);

        key = getIntent().getExtras().getInt("Choose", 0);
        if (key == 0) {
            toolbar.setTitle(R.string.txtQueryCustomerTitle);
            getCustomers();
        } else if (key == 1) {
            toolbar.setTitle(R.string.txtQueryAnimalTitle);
        } else {
            toolbar.setTitle(R.string.txtQueryVaccineTitle);
        }

        lvResult.setOnItemClickListener(this);

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_query);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public void onClick(View v) {
        if (key == 1) {
            //TODO: FILTRAR LISTA DE CUSTOMERS
        } else {
            //TODO: FILTRAR LISTA DE PETS
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        goToCustomerActivity(parent, position);
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
                populateListView(getCustomersResponseStructure.getCustomersList());
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, "Erro ao obter a lista de Clientes, por favor, tente novamente!", Toast.LENGTH_SHORT).show();
            }
        });

        getCustomersUseCase.execute();
    }

    public void populateListView(List<GetCustomersResponseStructure.Customer> customerList) {
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
}