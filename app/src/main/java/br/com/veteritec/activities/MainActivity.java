package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import br.com.veteritec.R;
import br.com.veteritec.clinics.ClinicResponseStructure;
import br.com.veteritec.utils.NavigationDrawer;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    private DrawerLayout drawer;

    private boolean isLogged = false;
    private String userToken = "";

    private ImageView imgLogo;

    private ClinicResponseStructure clinicResponseStructure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences language = getSharedPreferences("Language", MODE_PRIVATE);

        Locale locale = new Locale(language.getString("ChoosedLang", "pt"));
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgLogo = findViewById(R.id.imgMainMenuLogo);

        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        if (sharedPreferencesUtils.getIsDarkMode(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            imgLogo.setImageResource(R.drawable.veteritec_logo_night);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            imgLogo.setImageResource(R.drawable.veteritec_logo_day);
        }

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
        toolbar.setTitle(getResources().getString(R.string.main_menu));
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_menu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
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

        if (screen != null) {
            startActivityForResult(screen, 0);
        } else {
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
        } else {
            recreate();
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
        userToken = sharedPreferencesUtils.getUserToken(context);
    }
}