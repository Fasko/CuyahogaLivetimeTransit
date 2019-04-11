package com.mfsa.cuyahogalivetimetransit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView title = (TextView) findViewById(R.id.homeTitle1);
        title.setText("Favorites");

        //TODO: only open database when making requests, except for Routes open DB
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();



        //TODO: put spinner stuff in seperate class, or in a listener

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, databaseAccess.getRoutes());
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        Spinner spinRoutes = findViewById(R.id.spinRoute);
        spinRoutes.setAdapter(spinnerArrayAdapter);

        String selectedRoute = spinRoutes.getSelectedItem().toString();

/*
        System.out.println(selectedRoute);
*/

/*
        databaseAccess.getDirections("55 - Cleveland State Line");
        databaseAccess.getStops("55 - Cleveland State Line", "East");
        databaseAccess.getURL("55 - Cleveland State Line", "East", "CLIFTON BLVD & COVE AV");
*/

        databaseAccess.close();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_favorites:
                        break;
                    case R.id.navigation_schedules:
                        Intent a = new Intent(MainActivity.this, Routes.class);
                        startActivity(a);
                        finish();

                        break;
                    case R.id.navigation_alerts:
                        Intent b = new Intent(MainActivity.this, Alerts.class);
                        startActivity(b);
                        finish();

                        break;
                }
                MainActivity.this.overridePendingTransition(0,0);

                return false;
            }
        });
    }
}