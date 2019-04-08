package com.mfsa.cuyahogalivetimetransit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class Routes extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);


        TextView title = (TextView) findViewById(R.id.activityTitle1);
        title.setText("Routes");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setSelectedItemId(R.id.navigation_schedules);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_favorites:
                        Intent a = new Intent(Routes.this,MainActivity.class);
                        startActivity(a);

                        finish();

                        break;
                    case R.id.navigation_schedules:
                        break;
                    case R.id.navigation_alerts:
                        Intent b = new Intent(Routes.this, Alerts.class);
                        startActivity(b);
                        finish();
                        break;
                }
                return false;
            }
        });
    }
}