package com.mfsa.cuyahogalivetimetransit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView title = (TextView) findViewById(R.id.homeTitle1);
        title.setText("Favorites");

       populateRoutes();

        BottomNavigationView navigation = findViewById(R.id.navigation);
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

    protected void populateRoutes(){
        //TODO: only open database when making requests, except for Routes open DB
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();


        //TODO: put spinner stuff in seperate class, or in a listener
        ArrayList<String> allRoutes  = databaseAccess.getRoutes();
        ArrayAdapter<String> spinnerArrayAdapterRoute = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, allRoutes);
        spinnerArrayAdapterRoute.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        final Spinner spinRoutes = findViewById(R.id.spinRoute);
        spinRoutes.setAdapter(spinnerArrayAdapterRoute);

        spinRoutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateDirections(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });
    }

    protected void populateDirections(final String route){


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        ArrayList<String> allDirections = databaseAccess.getDirections(route);
        ArrayAdapter<String> spinnerArrayAdapterDirection = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, allDirections);
        spinnerArrayAdapterDirection.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        Spinner spinDirections = findViewById(R.id.spinDirection);
        spinDirections.setAdapter(spinnerArrayAdapterDirection);

        spinDirections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateStops(route,parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });


    }

    protected void populateStops(final String route, final String direction) {


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        ArrayList<String> allStops = databaseAccess.getStops(route, direction);
        ArrayAdapter<String> spinnerArrayAdapterStop = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, allStops);
        spinnerArrayAdapterStop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinStops = findViewById(R.id.spinStop);
        spinStops.setAdapter(spinnerArrayAdapterStop);

        spinStops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String stopURL = databaseAccess.getURL(route,direction,parent.getSelectedItem().toString());
                System.out.println(stopURL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });

    }
}