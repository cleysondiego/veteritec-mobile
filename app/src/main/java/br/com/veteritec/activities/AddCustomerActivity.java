package br.com.veteritec.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import java.util.Locale;
import java.util.Objects;

import br.com.veteritec.R;
import br.com.veteritec.customers.ChangeCustomerUseCase;
import br.com.veteritec.customers.CustomerRequestStructure;
import br.com.veteritec.customers.CreateCustomerUseCase;
import br.com.veteritec.customers.DeleteCustomerUseCase;
import br.com.veteritec.customers.GetCustomersResponseStructure;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.LoadingDialog;
import br.com.veteritec.utils.MaskUtils;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class AddCustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Context context;
    private DrawerLayout drawer;
    private int edition = 0;
    private boolean editable = false;

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

    private GetCustomersResponseStructure.Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        context = getApplicationContext();

        SharedPreferences language = getSharedPreferences("Language", MODE_PRIVATE);

        Locale locale = new Locale(language.getString("ChoosedLang", "pt"));
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        getUserDataFromSharedPreferences();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
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
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        try {
            if (Objects.requireNonNull(getIntent().getExtras()).getInt("Query", 0) == 0) {
                toolbar.setTitle(getResources().getString(R.string.txtAddCustomerAddTitle));
            } else {
                toolbar.setTitle(getResources().getString(R.string.txtAddCustomerEditTitle));

                setEdition();
                editable = true;

                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                customer = (GetCustomersResponseStructure.Customer) getIntent().getSerializableExtra("CUSTOMER_OBJECT");
                if (customer != null) {
                    edtCustomerName.setText(customer.getName());
                    edtCustomerCpf.setText(customer.getCpf());
                    edtCustomerCep.setText(customer.getZipCode());
                    edtCustomerNeighborhood.setText(customer.getNeighborhood());
                    edtCustomerStreet.setText(customer.getStreet());
                    edtCustomerNumber.setText(customer.getNumber());
                    edtCustomerTelephone.setText(customer.getPhoneNumber());
                    edtCustomerCellPhone.setText(customer.getCellPhoneNumber());
                    edtCustomerEmail.setText(customer.getEmail());
                }
            }
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Toast.makeText(context, getResources().getString(R.string.toastAddCustomerQueryCustomersError), Toast.LENGTH_SHORT).show();
            finish();
        }

        drawer = findViewById(R.id.drawer_add_customer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCustomerSave:
                if (validateFields()) {
                    if (!editable) {
                        createCustomer();
                    } else {
                        changeCustomer();
                    }
                } else {
                    Toast.makeText(context, getResources().getString(R.string.toastAddCustomerFieldsCheck), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnAddCustomerEdit:
                if (edition == 0) {
                    setEdition();
                    Toast.makeText(context, getResources().getString(R.string.toastAddCustomerDisabledEdition), Toast.LENGTH_SHORT).show();
                } else {
                    setEdition();
                    Toast.makeText(context, getResources().getString(R.string.toastAddCustomerEnabledEdition), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnAddCustomerDelete:
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);

                confirmDialog.setMessage(getResources().getString(R.string.txtConfirm))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.txtConfirmYes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteCustomer();
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

    private boolean validateFields() {
        return validateField(edtCustomerName) &&
                validateField(edtCustomerCpf) &&
                validateField(edtCustomerCep) &&
                validateField(edtCustomerNeighborhood) &&
                validateField(edtCustomerStreet) &&
                validateField(edtCustomerCellPhone) &&
                validateField(edtCustomerEmail);
    }

    private boolean validateField(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getResources().getString(R.string.setErrorEmptyField));
            editText.requestFocus();
            return false;
        } else if (editText.getText().toString().length() < 5) {
            editText.setError(getResources().getString(R.string.setErrorMinimumCharacters));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private void createCustomer() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        CustomerRequestStructure customerRequestStructure = createCustomerRequestStructure();
        ApiRequest apiRequest = new ApiRequest();
        CreateCustomerUseCase createCustomerUseCase = new CreateCustomerUseCase(ThreadExecutor.getInstance(), apiRequest, customerRequestStructure, userClinicId, userToken);
        createCustomerUseCase.setCallback(new CreateCustomerUseCase.OnCreateCustomer() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddCustomerSuccesfullyAddedCustomer), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddCustomerFailureAddedCustomer), Toast.LENGTH_LONG).show();
            }
        });

        createCustomerUseCase.execute();
    }

    private void getUserDataFromSharedPreferences() {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        userToken = sharedPreferencesUtils.getUserToken(context);
        userClinicId = sharedPreferencesUtils.getUserClinicId(context);
    }

    private void deleteCustomer() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        ApiRequest apiRequest = new ApiRequest();

        DeleteCustomerUseCase deleteCustomerUseCase = new DeleteCustomerUseCase(ThreadExecutor.getInstance(), apiRequest, customer.getId(), userClinicId, userToken);
        deleteCustomerUseCase.setCallback(new DeleteCustomerUseCase.OnDeleteCustomerCallback() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddCustomerSuccesfullyDeletedCustomer), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddCustomerFailureDeletedCustomer), Toast.LENGTH_LONG).show();
            }
        });

        deleteCustomerUseCase.execute();
    }

    private CustomerRequestStructure createCustomerRequestStructure() {
        CustomerRequestStructure customerRequestStructure = new CustomerRequestStructure();
        customerRequestStructure.setName(edtCustomerName.getText().toString());
        customerRequestStructure.setCpf(edtCustomerCpf.getText().toString());
        customerRequestStructure.setNeighborhood(edtCustomerNeighborhood.getText().toString());
        customerRequestStructure.setZipCode(edtCustomerCep.getText().toString());
        customerRequestStructure.setStreet(edtCustomerStreet.getText().toString());
        customerRequestStructure.setNumber(edtCustomerNumber.getText().toString());
        customerRequestStructure.setPhoneNumber(edtCustomerTelephone.getText().toString());
        customerRequestStructure.setCellNumber(edtCustomerCellPhone.getText().toString());
        customerRequestStructure.setEmail(edtCustomerEmail.getText().toString());

        return customerRequestStructure;
    }

    private void changeCustomer() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        CustomerRequestStructure customerRequestStructure = createCustomerRequestStructure();

        ApiRequest apiRequest = new ApiRequest();

        ChangeCustomerUseCase changeCustomerUseCase = new ChangeCustomerUseCase(ThreadExecutor.getInstance(), apiRequest, customerRequestStructure, userClinicId, userToken);
        changeCustomerUseCase.setCallback(new ChangeCustomerUseCase.OnChangeCustomer() {
            @Override
            public void onSuccess() {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddCustomerSuccesfullyEditedCustomer), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(context, getResources().getString(R.string.toastAddCustomerFailureEditedCustomer), Toast.LENGTH_LONG).show();
            }
        });

        changeCustomerUseCase.execute();
    }
}