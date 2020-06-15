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

import com.google.android.material.navigation.NavigationView;

import br.com.veteritec.R;

public class QueryVaccineActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_vaccine);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txtQueryVaccineTitle);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_query_vaccine);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                Intent addClient = new Intent(this, AddClientActivity.class);
                startActivity(addClient);
                finish();
                break;
            case R.id.nav_query_client:
                Intent queryClient = new Intent(this, QueryClientActivity.class);
                startActivity(queryClient);
                break;
            case R.id.nav_add_animal:
                Intent addAnimal = new Intent(this, AddAnimalActivity.class);
                startActivity(addAnimal);
                finish();
                break;
            case R.id.nav_query_animal:
                /*Intent queryAnimal = new Intent(this, QueryAnimalActivity.class);
                startActivity(queryAnimal);
                finish();*/
                break;
            case R.id.nav_add_vaccine:
                Intent addVaccine = new Intent(this, AddVaccineActivity.class);
                startActivity(addVaccine);
                finish();
                break;
            case R.id.nav_query_vaccine:
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
}
