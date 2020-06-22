package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import br.com.veteritec.R;
import br.com.veteritec.clinics.ClinicResponseStructure;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    private DrawerLayout drawer;

    private boolean isLogged = false;
    @SuppressWarnings("unused")
    private String userName = "";
    private String userToken = "";
    @SuppressWarnings("unused")
    private String userClinicId = "";

    private ClinicResponseStructure clinicResponseStructure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clinicResponseStructure = (ClinicResponseStructure) getIntent().getSerializableExtra("CLINIC_STRUCTURE");

        context = getApplicationContext();

        getUserDataFromSharedPreferences(context);

        if (!isLogged || userToken.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("CLINIC_STRUCTURE", clinicResponseStructure);
            startActivity(intent);
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_menu);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_menu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isLogged || userToken.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("CLINIC_STRUCTURE", clinicResponseStructure);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationDrawer navigationDrawer = new NavigationDrawer();
        Intent screen = navigationDrawer.choosedItem(drawer, context, item);

        if(screen != null) {
            startActivityForResult(screen, 0);
        }else{
            recreate();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        isLogged = sharedPreferencesUtils.isLogged(context);

        if (requestCode == 0 && !isLogged || userToken.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("CLINIC_STRUCTURE", clinicResponseStructure);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void getUserDataFromSharedPreferences(Context context) {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        isLogged = sharedPreferencesUtils.isLogged(context);
        userName = sharedPreferencesUtils.getUserName(context);
        userToken = sharedPreferencesUtils.getUserToken(context);
        userClinicId = sharedPreferencesUtils.getUserClinicId(context);
    }
}