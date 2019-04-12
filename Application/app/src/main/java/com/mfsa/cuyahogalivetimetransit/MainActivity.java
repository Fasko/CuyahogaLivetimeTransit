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

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView title = (TextView) findViewById(R.id.homeTitle1);
        title.setText("Favorites");

        //TODO: only open database when making requests, except for Routes open DB
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();


        //TODO: put spinner stuff in seperate class, or in a listener
        ArrayList<String> allRoutes  = databaseAccess.getRoutes();
        ArrayAdapter<String> spinnerArrayAdapterRoute = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, allRoutes);
        spinnerArrayAdapterRoute.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        final Spinner spinRoutes = findViewById(R.id.spinRoute);
        spinRoutes.setAdapter(spinnerArrayAdapterRoute);
        final String[] selectedRoute = new String[1];
        if (selectedRoute[0] == null){
            selectedRoute[0] = spinRoutes.getSelectedItem().toString();
        }


/*      //TODO: make another spinner depending on the selection of the first spinner
        //TODO: move the second spinner out of OnCreate()
        final ArrayList<String>[] allDirections = new ArrayList[]{databaseAccess.getDirections(selectedRoute[0])};

        spinRoutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoute[0] = parent.getItemAtPosition(position).toString();
                selectedRoute[0] = parent.getSelectedItem().toString();
                allDirections[0] = databaseAccess.getDirections(selectedRoute[0]);
                System.out.println(selectedRoute[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
                selectedRoute[0] = parent.toString();
                System.out.println(selectedRoute[0]);
            }
        });


        ArrayAdapter<String> spinnerArrayAdapterDirection = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, allDirections[0]);
        spinnerArrayAdapterDirection.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        Spinner spinDirections = findViewById(R.id.spinDirection);
        spinDirections.setAdapter(spinnerArrayAdapterDirection);
        final String[] selectedDirection = new String[1];

        spinDirections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDirection[0] = parent.getItemAtPosition(position).toString();
                System.out.println(selectedDirection[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
                selectedDirection[0] = parent.toString();
                System.out.println(selectedDirection[0]);
            }
        });*/



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
}