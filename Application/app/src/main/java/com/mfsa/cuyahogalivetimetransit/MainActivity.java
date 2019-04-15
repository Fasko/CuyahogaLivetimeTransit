package com.mfsa.cuyahogalivetimetransit;

import android.content.Intent;
import android.os.AsyncTask;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
                MyAsyncTask async = new MyAsyncTask();
                async.execute(stopURL);
                System.out.println(stopURL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });
    }

    public class MyAsyncTask extends AsyncTask<String, Void, Document> {

        // Fetch the HTML source from the website
        @Override
        protected Document doInBackground(String... strings) {
            Document document = null;
            try {
                document = Jsoup.connect(strings[0]).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return document;
        }

        @Override
        protected void onPostExecute(Document document) {
            Elements nextVehicles = document.select(".ada");
            List<String> adaClass = nextVehicles.eachAttr("title");

            Elements arrivalTime = document.select(".adatime");
            List<String> adatimeClass = arrivalTime.eachAttr("title");

            ArrayList<String> stopLabel = new ArrayList<String>();
            Elements scheduledTimes = document.select(".stopLabel");
            for (Element scheduleTime : scheduledTimes){
                String scheduleTimesToText = scheduleTime.text();
                scheduleTimesToText = scheduleTimesToText.replace("&nbsp", "");
                stopLabel.add(scheduleTimesToText);

            }
            if (adaClass.get(0).equals("No further buses scheduled for this stop<br>")){
                adaClass.set(0, "No further buses scheduled for this stop.");
            }
            //TODO: add UI to display each of those lists.
            System.out.println(adaClass);
            System.out.println(adatimeClass);
            System.out.println(stopLabel);
        }
    }

}