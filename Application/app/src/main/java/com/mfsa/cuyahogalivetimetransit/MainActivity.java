package com.mfsa.cuyahogalivetimetransit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        TextView title = (TextView) findViewById(R.id.homeTitle1);
        title.setText("Favorites");


        //TODO Implement Favorites here, maybe another SQL table?

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