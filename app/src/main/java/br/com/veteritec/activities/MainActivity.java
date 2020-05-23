package br.com.veteritec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.veteritec.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalculatorFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_calculator);
        }

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_calculator:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalculatorFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_night_mode:
                Toast.makeText(this, "Modo Noturno", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_add_vaccine:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddVaccineFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_query_vaccine:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new QueryVaccineFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Deslogar", Toast.LENGTH_SHORT).show();
                break;
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