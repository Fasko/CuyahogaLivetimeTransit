package com.mfsa.cuyahogalivetimetransit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Favorites extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        TextView title = findViewById(R.id.homeTitle1);
        title.setText("Favorites");
        TextView tv = findViewById(R.id.displayInfoFav);
        Favorites.MyAsyncTask async = new Favorites.MyAsyncTask();

        // Display the list of user favorite in Spinner
        // Spinner, on selection, query the URL and display the info on the favorites screen


        //TODO Implement Favorites here, maybe another SQL table?
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_favorites:
                        break;
                    case R.id.navigation_schedules:
                        Intent a = new Intent(Favorites.this, Routes.class);
                        startActivity(a);
                        break;
                    case R.id.navigation_alerts:
                        Intent b = new Intent(Favorites.this, Alerts.class);
                        startActivity(b);
                        break;
                }
                Favorites.this.overridePendingTransition(0, 0);

                return false;
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
            TextView displayInfo = findViewById(R.id.displayInfoRT);

            ArrayList<String> stopLabel = new ArrayList<String>();
            Elements scheduledTimes = document.select(".stopLabel");
            for (Element scheduleTime : scheduledTimes) {
                String scheduleTimesToText = scheduleTime.text();
                scheduleTimesToText = scheduleTimesToText.replace("&nbsp", "");
                stopLabel.add(scheduleTimesToText);

            }
            //TODO: Make the UI look better, maybe use more than one TextView to change font size etc...
            if (adaClass.get(0).equals("No further buses scheduled for this stop<br>")) {
                adaClass.set(0, "No further buses scheduled for this stop.");
                displayInfo.setText(adaClass.get(0));

            } else {
                String string0 = adaClass.get(0) + "\n";
                String string1 = adaClass.get(1) + "\n";
                String stops = "";
                for (int i = 0; i < adatimeClass.size(); i++) {
                    stops = stops + "\n" + "Will arrive at " + adatimeClass.get(i) + ", " + stopLabel.get(i).toLowerCase();
                }
                displayInfo.setText(string0 + stops + "\n\n" + string1);
            }
        }
    }
}
