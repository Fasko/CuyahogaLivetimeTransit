package com.mfsa.cuyahogalivetimetransit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Routes extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    String stopURL; //used to pass easier to refresh times

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setSelectedItemId(R.id.navigation_schedules);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_favorites:
                        Intent a = new Intent(Routes.this,Favorites.class);
                        startActivity(a);
                        break;
                    case R.id.navigation_schedules:
                        break;
                    case R.id.navigation_alerts:
                        Intent b = new Intent(Routes.this, Alerts.class);
                        startActivity(b);
                        break;
                }
                Routes.this.overridePendingTransition(0,0);
                return false;
            }
        });
        populateRoutes();

    }

    protected void populateRoutes(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        ArrayList<String> allRoutes  = databaseAccess.getRoutes();
        ArrayAdapter<String> spinnerArrayAdapterRoute = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, allRoutes);
        spinnerArrayAdapterRoute.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        final Spinner spinRoutes = findViewById(R.id.spinRoute);
        spinnerArrayAdapterRoute.insert("Select a Route:",0);
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
        spinnerArrayAdapterDirection.insert("Select a Direction:",0);
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

        // Set the Spinner and TextView to default options
        final Spinner spinStops = findViewById(R.id.spinStop);
        spinnerArrayAdapterStop.insert("Select a Stop:",0);

        TextView tv = findViewById(R.id.displayInfoRT);

        tv.setText("");

        spinStops.setAdapter(spinnerArrayAdapterStop);
        spinStops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinStops.getSelectedItem().equals("Select a Stop:")) {
                    return;
                }

                mMap.clear(); //removes previous markers from the map

                stopURL = databaseAccess.getURL(route, direction, parent.getSelectedItem().toString());
                Routes.MyAsyncTask async = new Routes.MyAsyncTask();


                // Set the Google Maps location to the bus stop the user requested
                float[] userLatLong = databaseAccess.getLatLong(spinStops.getSelectedItem().toString());

                /* The data from RTA doesn't match the other database we created with stop names.
                   If the database can't return the latitude and longitude of a stop, set the map to
                   The middle of public square, and pan the camera out */
                if (userLatLong[0] != 0) {
                    LatLng userBusSelection = new LatLng(userLatLong[0], userLatLong[1]);
                    mMap.addMarker(new MarkerOptions().position(userBusSelection).title(spinStops.getSelectedItem().toString()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userBusSelection, 20.0f));
                } else {
                    LatLng defaultSelection = new LatLng(41.4996614, -81.6936739);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultSelection, 10.0f));
                    Toast.makeText(getApplicationContext(), "Couldn't Locate Stop", Toast.LENGTH_LONG).show();
                }


                async.execute(stopURL);
                System.out.println(stopURL);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = (float) 12.0;
        LatLng publicSquareCleveland = new LatLng(41.4996614, -81.6936739);
        //mMap.addMarker(new MarkerOptions().position(publicSquareCleveland).title("Marker in Cleveland"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(publicSquareCleveland, zoomLevel));
    }

    //TODO replace with a button, or way to inform user of this feature
    //If user clicks on TextView, fetch the latest times and display.
    public void refreshTimes(View view) {
        Routes.MyAsyncTask async = new Routes.MyAsyncTask();
        async.execute(stopURL);
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
            TextView displayInfo = findViewById(R.id.displayInfoRT);

            ArrayList<String> stopLabel = new ArrayList<String>();
            Elements scheduledTimes = document.select(".stopLabel");
            for (Element scheduleTime : scheduledTimes){
                String scheduleTimesToText = scheduleTime.text();
                scheduleTimesToText = scheduleTimesToText.replace("&nbsp", "");
                stopLabel.add(scheduleTimesToText);

            }
            //TODO: Make the UI look better, maybe use more than one TextView to change font size etc...
            if (adaClass.get(0).equals("No further buses scheduled for this stop<br>")){
                adaClass.set(0, "No further buses scheduled for this stop.");
                displayInfo.setText(adaClass.get(0));

            } else{
                String string0 = adaClass.get(0) + "\n";
                String string1 = adaClass.get(1) + "\n";
                String stops = "";
                for (int i = 0; i < adatimeClass.size();i++){
                    stops = stops + "\n" + "Will arrive at " + adatimeClass.get(i) + ", " + stopLabel.get(i).toLowerCase();
                }
                displayInfo.setText(string0 + stops + "\n\n" + string1);
            }
            System.out.println(adaClass);
            System.out.println(adatimeClass);
            System.out.println(stopLabel);
        }
    }
}