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
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import br.com.veteritec.R;
import br.com.veteritec.utils.NavigationDrawer;

public class TranslateActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Context context;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        context = getApplicationContext();

        SharedPreferences language = getSharedPreferences("Language", MODE_PRIVATE);

        Locale locale = new Locale(language.getString("ChoosedLang", "pt"));
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.txtTranslateTitle));
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_translate);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onClick(View v) {
        String language;

        if (v.getId() == R.id.imgBR) {
            language = "pt";
        } else if (v.getId() == R.id.imgUS) {
            language = "en";
        } else if (v.getId() == R.id.imgES) {
            language = "es";
        } else if (v.getId() == R.id.imgFR) {
            language = "fr";
        } else if (v.getId() == R.id.imgHI) {
            language = "hi";
        } else if (v.getId() == R.id.imgRU) {
            language = "ru";
        } else if (v.getId() == R.id.imgZH) {
            language = "zh";
        } else if (v.getId() == R.id.imgJA) {
            language = "ja";
        } else {
            language = "pt";
        }

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Context context = this;
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());

        SharedPreferences.Editor data = getSharedPreferences("Language", MODE_PRIVATE).edit();
        data.putString("ChoosedLang", language);
        data.apply();

        Toast.makeText(context, getResources().getString(R.string.toastTranslateLanguageChange), Toast.LENGTH_SHORT).show();
        recreate();
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
}