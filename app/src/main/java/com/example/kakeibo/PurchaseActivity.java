package com.example.kakeibo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;


import com.google.android.material.bottomnavigation.BottomNavigationView;


public class PurchaseActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        // Link tussen activiteit en visueele ID's leggen
        bottomNavigation = findViewById(R.id.bottomNavigationPurchase);
        bottomNavigation.setSelectedItemId(R.id.expense);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


         /* Perform item selected listener voor de bottom navigation. Staat op alle paginas uit de main menu zodat het juiste item geselecteerd wordt.
           Perform item selected listener. Zou vervangen moeten worden door if statements bijvoorbeeld: if item.getItemId() == R.id.Home {
           }. niet gedaan omdat ik met switch statements wou werken.
         */
        bottomNavigation.setOnItemSelectedListener(item -> {

            switch(item.getItemId())
            {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(),MainMenu.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.expense:
                    startActivity(new Intent(getApplicationContext(),PurchaseActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.report:
                    startActivity(new Intent(getApplicationContext(),ReportActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

    }
}