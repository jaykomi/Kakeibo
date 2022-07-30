package com.example.kakeibo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.paypal.checkout.PayPalCheckout;

import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.PaymentButtonIntent;
import com.paypal.checkout.config.SettingsConfig;

import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.ProcessingInstruction;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;

import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButton;


import java.util.ArrayList;


public class MainMenu extends AppCompatActivity {

    private static final String YOUR_CLIENT_ID ="Adszrsi42Q8C9XekDu02kITKwFWmLovkfIyepnSowaMAeEeCXXNQD-jQ5HxSYBChNvUqkSsjlhHf5Txs";

    BottomNavigationView bottomNavigation;
    CardView cardviewWallet, cardviewExpenses, cardviewReport;
    ImageView helpImage;
    PaymentButton paypalButton;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        // Link tussen activiteit en visueele ID's leggen
        cardviewWallet = findViewById(R.id.cardviewWallet);
        cardviewExpenses = findViewById(R.id.cardviewExpenses);
        cardviewReport = findViewById(R.id.cardviewReport);
        bottomNavigation = findViewById(R.id.bottomNavigationMainMenu);
        bottomNavigation.setSelectedItemId(R.id.home);
        helpImage = findViewById(R.id.mainMenuHelp);

        paypalButton = findViewById(R.id.paypalButton);

        // Sandbox Login gegevens - sb-w1ags14625199@business.example.com
        // 7o'/UIPg


        // Checkout functie voor paypal
        // Configuratie voor de betaling

        CheckoutConfig donationConfig = new CheckoutConfig(
                getApplication(),
                YOUR_CLIENT_ID,
               Environment.SANDBOX,
                String.format("%s://paypalpay", "com.example.kakeibo"),
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                PaymentButtonIntent.CAPTURE,
        new SettingsConfig(
                true, false)
);
        PayPalCheckout.setConfig(donationConfig);

        // Klik op de knop, order wordt aangemaakt met naam. hoeveelheid, valuta en hoeveelheid in die valuta
        // daarna wordt de order aangemaakt. Voor deze keer alleen een on approval. het is mogelijk om een on cancel functionaliteit te maken.

                paypalButton.setup(
                        createOrderActions -> {
                            ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                            purchaseUnits.add(
                                    new PurchaseUnit.Builder()
                                            .amount(
                                                    new Amount.Builder()
                                                            .currencyCode(CurrencyCode.USD)
                                                            .value("10.00")
                                                            .build()
                                            )
                                            .build()
                            );
                            Order order = new Order(
                                    OrderIntent.CAPTURE,
                                    new AppContext.Builder()
                                            .userAction(UserAction.PAY_NOW)
                                            .build(),
                                    purchaseUnits,
                                    ProcessingInstruction.NO_INSTRUCTION
                            );
                            createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                        },
                        approval -> approval.getOrderActions().capture(result -> {
                            Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                        })
                );


        /* Perform item selected listener voor de bottom navigation. Staat op alle paginas uit de main menu zodat het juiste item geselecteerd wordt.
           Perform item selected listener. Zou vervangen moeten worden door if statements bijvoorbeeld: if item.getItemId() == R.id.Home {
           }. niet gedaan omdat ik met switch statements wou werken.
         */
        bottomNavigation.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), MainMenu.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.expense:
                    startActivity(new Intent(getApplicationContext(), PurchaseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.report:
                    startActivity(new Intent(getApplicationContext(), ReportActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        // hulp knop druk erop geeft een alert dialog met wat  er verwacht wordt en hoe de applicatie gebruikt wordt.
        helpImage.setOnClickListener(v ->
        {
            AlertDialog.Builder builderPurchases = new AlertDialog.Builder(this);
            builderPurchases.setTitle(" Information");
            builderPurchases.setMessage("\n" +
                    "Step 1: Create a virtual wallet. As soon as the wallet has been registered the 30 day timer starts. " +
                    "\n" +
                    "\n" +
                    "Step 2: Over the course of the next 30 days register any purchase that you make. Categorize them as needs, wants, cultural or unexpected expenses." +
                    "\n" +
                    "\n" +
                    "Step 3: At the end of the 30 days your report card will become visible in the report card section of the application."
            );
            builderPurchases.setIcon(android.R.drawable.ic_dialog_info);
            builderPurchases.setNegativeButton("Close", (dialogPurchases, i) -> dialogPurchases.dismiss()
            );
            builderPurchases.show();
        });


        // actie voor het klikken op de items in main menu in plaats van de bottom navigation bar.
        cardviewWallet.setOnClickListener(
                v -> {
                    Intent actieCreate = new Intent(v.getContext(), WalletActivity.class);
                    startActivity(actieCreate);
                });


        cardviewExpenses.setOnClickListener(
                v -> {
                    Intent actieCreate = new Intent(v.getContext(), PurchaseActivity.class);
                    startActivity(actieCreate);
                });

        cardviewReport.setOnClickListener(
                v -> {
                    Intent actieCreate = new Intent(v.getContext(), ReportActivity.class);
                    startActivity(actieCreate);
                });
    }


}
