package com.mfsa.cuyahogalivetimetransit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class Alerts extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        TextView title = (TextView) findViewById(R.id.activityTitle2);
        title.setText("Alerts");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_alerts);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_favorites:
                        Intent a = new Intent(Alerts.this, MainActivity.class);
                        startActivity(a);
                        finish();

                        break;
                    case R.id.navigation_schedules:
                        Intent b = new Intent(Alerts.this, Routes.class);
                        startActivity(b);
                        finish();

                        break;
                    case R.id.navigation_alerts:
                        break;
                }
                Alerts.this.overridePendingTransition(0,0);
                return false;
            }
        });
    }
}