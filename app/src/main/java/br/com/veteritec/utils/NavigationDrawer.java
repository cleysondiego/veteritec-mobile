package br.com.veteritec.utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import br.com.veteritec.R;
import br.com.veteritec.activities.AddAnimalActivity;
import br.com.veteritec.activities.AddCustomerActivity;
import br.com.veteritec.activities.AddEmployeeActivity;
import br.com.veteritec.activities.AddVaccineActivity;
import br.com.veteritec.activities.CalculatorActivity;
import br.com.veteritec.activities.QueryActivity;

public class NavigationDrawer extends AppCompatActivity {
    private Intent intent;

    public Intent choosedItem(DrawerLayout drawer, Context context, MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_calculator:
                intent = new Intent(context, CalculatorActivity.class);
                break;
            case R.id.nav_add_customer:
                intent = new Intent(context, AddCustomerActivity.class);
                intent.putExtra("Query", 0);
                break;
            case R.id.nav_query_customer:
                intent = new Intent(context, QueryActivity.class);
                intent.putExtra("Choose", 0);
                break;
            case R.id.nav_add_animal:
                intent = new Intent(context, AddAnimalActivity.class);
                break;
            case R.id.nav_query_animal:
                intent = new Intent(context, QueryActivity.class);
                intent.putExtra("Choose", 1);
                break;
            case R.id.nav_add_vaccine:
                intent = new Intent(context, AddVaccineActivity.class);
                break;
            case R.id.nav_query_vaccine:
                intent = new Intent(context, QueryActivity.class);
                intent.putExtra("Choose", 2);
                break;
            case R.id.nav_add_employee:
                intent = new Intent(context, AddEmployeeActivity.class);
                break;
            case R.id.nav_logout:
                intent = null;
                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
                sharedPreferencesUtils.setLogoff(context);
                break;
        }
        return intent;
    }
}