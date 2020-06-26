package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import br.com.veteritec.R;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class AddEmployeeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Context context;
    private DrawerLayout drawer;

    private EditText edtEmployeeName;
    private EditText edtEmployeeEmail;
    private EditText edtEmployeePassword;
    private EditText edtEmployeeConfirmPassword;

    private String userToken;
    private String userClinicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        context = getApplicationContext();

        SharedPreferences language = getSharedPreferences("Language", MODE_PRIVATE);

        Locale locale = new Locale(language.getString("ChoosedLang", "pt"));
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        getUserDataFromSharedPreferences();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        edtEmployeeName = findViewById(R.id.edtAddEmployeeName);
        edtEmployeeEmail = findViewById(R.id.edtAddEmployeeEmail);
        edtEmployeePassword = findViewById(R.id.edtAddEmployeePassword);
        edtEmployeeConfirmPassword = findViewById(R.id.edtAddEmployeeConfirmPassword);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.txtAddEmployeeTitle));
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_add_employee);
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
        if (validateFields()) {
            createEmployee();
            Toast.makeText(context, "Teste do botão", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Por favor, verifique os dados digitados e tente novamente", Toast.LENGTH_LONG).show();
        }
    }

    public boolean validateFields() {
        return validateField(edtEmployeeName) ||
                validateField(edtEmployeeEmail) ||
                validateField(edtEmployeePassword) ||
                validateField(edtEmployeeConfirmPassword) ||
                validatePassword(edtEmployeePassword, edtEmployeeConfirmPassword);
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

    public boolean validatePassword(EditText editText, EditText editText1) {
        if (editText.getText().toString().equals(editText1.getText().toString())) {
            return true;
        } else {
            Toast.makeText(context, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void createEmployee() {

    }

    private void getUserDataFromSharedPreferences() {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        userToken = sharedPreferencesUtils.getUserToken(context);
        userClinicId = sharedPreferencesUtils.getUserClinicId(context);
    }
}