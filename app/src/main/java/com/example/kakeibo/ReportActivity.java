package com.example.kakeibo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.kakeibo.database.KakeiboDataDBHelper;
import com.example.kakeibo.model.ReportCard;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class ReportActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigation;
    Spinner walletSpinner;
    Button loadReportData, nextStep;
    SQLiteDatabase db;
    KakeiboDataDBHelper dbHelper;
    EditText savingsActual, savingsExpected, spendingWants, spendingNeeds, spendingCultural, spendingUnexpected;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Link tussen activiteit en visueele ID's leggen
        nextStep = findViewById(R.id.buttonNextStep);
        bottomNavigation = findViewById(R.id.bottomNavigationReport);
        walletSpinner = findViewById(R.id.spinnerWalletName);
        loadReportData = findViewById(R.id.buttonLoadData);
        savingsActual = findViewById(R.id.editTextNumberSavingsActual);
        savingsExpected = findViewById(R.id.editTextNumberSavingsGoal);
        spendingWants = findViewById(R.id.editTextSpendingWants);
        spendingNeeds = findViewById(R.id.editTextSpendingNeeds);
        spendingCultural = findViewById(R.id.editTextSpendingCultural);
        spendingUnexpected = findViewById(R.id.editTextSpendingUnexpected);

        dbHelper = new KakeiboDataDBHelper(this);
        bottomNavigation.setSelectedItemId(R.id.report);

        getWalletNames();

        savingsActual.setEnabled(false);
        savingsExpected.setEnabled(false);
        spendingWants.setEnabled(false);
        spendingNeeds.setEnabled(false);
        spendingCultural.setEnabled(false);
        spendingUnexpected.setEnabled(false);
        nextStep.setEnabled(false);

        String walletSpinnerResult = walletSpinner.getSelectedItem().toString();
        ReportCard r = new ReportCard(walletSpinnerResult);


        /* Zelfs voor mij een lastige stuk code waar ik gebruik heb moeten maken van meerdere cursors. Dit komt vooral door de lokale database.
        De individuele cursors halen de data op en plaatsen deze in de variabelen. Waar nodig worden berekeningen uitgevoerd en word data met een delay
        in een textveld gezet. Dit zodat het voor de gebruiker lijkt alsof er op de achtergrond echt iets gebeurt. Daarna wordt aan de hand van berekeningen
        tekstvakken op positief (groen) of negatief (rood) gezet aan de hand van de resultaten.
         */

        loadReportData.setOnClickListener(v -> {

            db = dbHelper.getReadableDatabase();


            ReportCard reportCard = new ReportCard(walletSpinnerResult);

            reportCard.setWalletName(walletSpinner.getSelectedItem().toString());

            try {

                loadData(v);


                Cursor cSavings = db.rawQuery("SELECT savingsgoal, spendingcash FROM wallets Where walletname = '" + reportCard.getWalletName() + "';", null);

                cSavings.moveToFirst();
                int goal = cSavings.getInt(0);
                double spending = cSavings.getDouble(1);
                savingsExpected.setText(String.valueOf(goal));


                Cursor cNeeds = db.rawQuery("SELECT SUM(p.amount) as total FROM purchases p, wallets w Where  w.walletname = '" + reportCard.getWalletName() + "' AND p.purchasecategory = \"Needs\" AND p.date BETWEEN w.startdate AND w.enddate;", null);

                cNeeds.moveToFirst();
                double totalNeeds = cNeeds.getDouble(0);

                spendingNeeds.postDelayed(() -> spendingNeeds.setText(String.valueOf(totalNeeds)), 1500);

                Cursor cWants = db.rawQuery("SELECT SUM(p.amount) as total FROM purchases p, wallets w Where  w.walletname = '" + reportCard.getWalletName() + "' AND p.purchasecategory = \"Wants\" AND p.date BETWEEN w.startdate AND w.enddate;", null);

                cWants.moveToFirst();
                double totalWants = cWants.getDouble(0);

                spendingWants.postDelayed(() -> spendingWants.setText(String.valueOf(totalWants)), 3000);

                Cursor cCultural = db.rawQuery("SELECT SUM(p.amount) as total FROM purchases p, wallets w Where  w.walletname = '" + reportCard.getWalletName() + "' AND p.purchasecategory = \"Cultural\" AND p.date BETWEEN w.startdate AND w.enddate;", null);

                cCultural.moveToFirst();
                double totalCultural = cCultural.getDouble(0);

                spendingCultural.postDelayed(() -> spendingCultural.setText(String.valueOf(totalCultural)), 4500);

                Cursor cUnexpected = db.rawQuery("SELECT SUM(p.amount) as total FROM purchases p, wallets w Where  w.walletname = '" + reportCard.getWalletName() + "' AND p.purchasecategory = \"Unexpected\" AND p.date BETWEEN w.startdate AND w.enddate;", null);

                cUnexpected.moveToFirst();
                double totalUnexpected = cUnexpected.getDouble(0);

                spendingUnexpected.postDelayed(() -> spendingUnexpected.setText(String.valueOf(totalUnexpected)), 6000);

                double totalAll = totalUnexpected + totalCultural + totalNeeds + totalWants;
                double actualSavings = spending - totalAll;
                double real = goal + actualSavings;

                savingsActual.postDelayed(() -> savingsActual.setText(String.valueOf(real)), 8000);

                if (goal <= real) {
                    savingsActual.postDelayed(() -> {
                        savingsActual.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.green));
                        savingsActual.setTextColor(ContextCompat.getColor(this,
                                R.color.white));
                    }, 8000);
                } else {
                    savingsActual.postDelayed(() -> {
                        savingsActual.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.red));
                        savingsActual.setTextColor(ContextCompat.getColor(this,
                                R.color.white));

                    }, 8000);
                }

                /* controleer of voor de individuele categorien er meer dan 25% van het spendeergeeld besteed is en presenteer
                    de gebruiker met een melding hiervan.
                 */
                double quarter = spending / 4;

                if (totalNeeds > quarter) spendingNeeds.postDelayed(() -> {
                    spendingNeeds.setBackgroundColor(ContextCompat.getColor(this,
                            R.color.red));
                    spendingNeeds.setTextColor(ContextCompat.getColor(this,
                            R.color.white));
                }, 1500);


                if (totalWants > quarter) spendingWants.postDelayed(() -> {
                    spendingWants.setBackgroundColor(ContextCompat.getColor(this,
                            R.color.red));
                    spendingWants.setTextColor(ContextCompat.getColor(this,
                            R.color.white));
                }, 3000);


                if (totalCultural > quarter) spendingCultural.postDelayed(() -> {
                    spendingCultural.setBackgroundColor(ContextCompat.getColor(this,
                            R.color.red));
                    spendingCultural.setTextColor(ContextCompat.getColor(this,
                            R.color.white));
                }, 4500);


                if (totalUnexpected > quarter) spendingUnexpected.postDelayed(() -> {
                    spendingUnexpected.setBackgroundColor(ContextCompat.getColor(this,
                            R.color.red));
                    spendingUnexpected.setTextColor(ContextCompat.getColor(this,
                            R.color.white));

                }, 6000);


                Handler handler = new Handler();
                handler.postDelayed(this::takeScreenshot, 9000);  // 9000 milliseconds


                ActiveProgressDialog();


                cSavings.close();
                cWants.close();
                cNeeds.close();
                cCultural.close();
                cUnexpected.close();

                nextStep.setEnabled(true);


            } catch (SQLiteException ee) {
                Toast.makeText(ReportActivity.this, "Error!" + ee, Toast.LENGTH_LONG).show();
            } finally {

                db.close();
                dbHelper.close();
            }

        });


        nextStep.setOnClickListener(
                v -> {
                    Intent actieNextStep = new Intent(this, ReportOpinionActivity.class);
                    actieNextStep.putExtra("ReportCard", r);
                    startActivity(actieNextStep);
                });

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
    }

    /* het ophalen van de verschillende portemonnee namen waarvan de einddatum verstrek is en deze in de spinner zetten.
     */

    public void getWalletNames() {

        db = dbHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("SELECT walletname FROM wallets WHERE CURRENT_DATE NOT BETWEEN startdate AND enddate AND walletname NOT IN(SELECT walletname FROM reportcards);", null);
        String[] wallet = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < wallet.length; i++) {
            wallet[i] = cursor.getString(0);
            cursor.moveToNext();
        }
        ArrayAdapter<String> walletAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, wallet);
        walletSpinner.setAdapter(walletAdapter);
        walletSpinner.setSelection(0);

        db.close();
        dbHelper.close();
        cursor.close();
    }

    /* verourderde functie alleen doet wel precies wat ik graag wil en dat is de gebruiker presenteren met iets visueels
        De functie laat een pprogress dialog spinner zien die na 8500 ms wordt afgesloten. dit is nadat alle delays van de generate report card button zijn verlopen.
     */
    @SuppressWarnings("deprecation")
    public void loadData(View view) {
        ProgressDialog progressGeneratingReport = new ProgressDialog(this);
        progressGeneratingReport.setMessage("Generating Report");
        progressGeneratingReport.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressGeneratingReport.setIndeterminate(true);
        progressGeneratingReport.setCanceledOnTouchOutside(false);
        progressGeneratingReport.show();


        new Thread(() -> {
            try {
                Thread.sleep(8500);
                if (progressGeneratingReport.isShowing())
                    progressGeneratingReport.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // info na genereren rapport om gebruiker duidelijk te geven over betekenis van kleuren etc.
    protected void ActiveProgressDialog() {
        AlertDialog.Builder builderReportHelp = new AlertDialog.Builder(this);
        builderReportHelp.setTitle("Report Info");
        builderReportHelp.setMessage("If the actual savings light up green that means you've reached your savings goal of that particular month." +
                "\n" +
                "\n" +
                "Anything above the original savings goal are because of spending less than you're reserved spending cash. " +
                "\n" +
                "\n" +
                "Should one of the purchase categories light up red that means you've spent over 25% of your 30 day budget on that category." +
                "\n" +
                "\n" +
                "Try to pay more attention to that purchase category during the next period!." +
                "\n" +
                "\n" +
                "Press the next step button to continue building your report card!"
        );
        builderReportHelp.setIcon(android.R.drawable.ic_dialog_info);
        builderReportHelp.setNegativeButton("Close", (dialogPurchases, i) -> dialogPurchases.dismiss() );

        Handler handler = new Handler();
        handler.postDelayed(builderReportHelp::show, 10000);  // 1500 milliseconds
    }


    /* screenshot functionaliteit. een grote tering zooi met permissions vanaf bepaalde SDK's en vanaf andere weer niet.
    bij sommige SDK's zelfs met rooten van device access denied.
     */
    private void takeScreenshot() {


            Date screenshotToday = new Date();

            //naam wordt bepaald aan de hand van yyyy mm dd + hh mm ss zodat iedere foto uniek word.
            CharSequence today = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", screenshotToday);

            try{

            String screenshotName = Environment.getExternalStorageDirectory().toString() +"/" + today + ".jpg";


            View rootView = getWindow().getDecorView().getRootView();
            rootView.setDrawingCacheEnabled(true);

            Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);

            File imageFile = new File(screenshotName);

                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

            } catch (IOException e) {
                Log.e("GREC", e.getMessage(), e);
            }
    }
    }






