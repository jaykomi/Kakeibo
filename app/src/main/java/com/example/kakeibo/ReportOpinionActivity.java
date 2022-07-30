package com.example.kakeibo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kakeibo.database.KakeiboDataDBHelper;
import com.example.kakeibo.model.ReportCard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ReportOpinionActivity extends AppCompatActivity {

    TextView walletNameText;
    Button saveReport;
    EditText improvement, improvementHow;
    Spinner goalAchieved;
    KakeiboDataDBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_opinion);

        dbHelper = new KakeiboDataDBHelper(this);

        goalAchieved = findViewById(R.id.spinnerPurchaseCategory);
        improvement = findViewById(R.id.editTextImprove);
        improvementHow = findViewById(R.id.editTextImproveHow);
        saveReport = findViewById(R.id.buttonSaveReport);
        walletNameText = findViewById(R.id.textViewWallet);

        ReportCard r = getIntent().getParcelableExtra("ReportCard");
        String walletName = r.getWalletName();

        walletNameText.setText(walletName);


        saveReport.setOnClickListener(vi -> {

            db = dbHelper.getWritableDatabase();
            ReportCard report = new ReportCard();


            // Zet de waardes in variabelen

            String textImprovementHow = improvement.getText().toString();
            String textImprovement = improvementHow.getText().toString();
            String categoryAchieved = goalAchieved.getSelectedItem().toString();

            try{

                //verbergt het toetsenbord zodra alle variabelen niet leeg  zijn.
                InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(vi.getWindowToken(), 0);


                /*
                Ga  de database verbinding maken en de data daarin wegzetten
                 */

                report.setWalletName(walletName);
                report.setGoalAchieved(categoryAchieved);
                report.setImprovement(textImprovement);
                report.setHowToImprove(textImprovementHow);

                    // zet de waardes in de content values
                    ContentValues values = new ContentValues();

                    values.put("walletname", report.getWalletName());
                    values.put("goalachieved", report.getGoalAchieved());
                    values.put("improvement", report.getImprovement());
                    values.put("improvehow", report.getHowToImprove());

                    // voer de values in de database table users in.
                    long result = db.insert("reportcards", null, values);


                    // reageer op basis van de terugvoer "result" van de database.
                    if (result != -1) {
                        takeScreenshot();
                        Toast.makeText(this, "Report " + report.getWalletName() + " saved! redirecting to main menu in 5 seconds", Toast.LENGTH_LONG).show();
                        improvement.getText().clear();
                        improvementHow.getText().clear();
                        goalAchieved.setSelection(0);


                        new Handler().postDelayed(() -> {
                            final Intent actieMainIntent = new Intent(ReportOpinionActivity.this, MainMenu.class);
                            startActivity(actieMainIntent);
                            finish();
                        }, 5000);

                    }
                    if (result == -1) {
                        Toast.makeText(this, "Failed to save reportcard!", Toast.LENGTH_LONG).show();
                    }
                } catch (SQLiteException | NumberFormatException e ) {
                    Toast.makeText(this, "Error!" + e, Toast.LENGTH_LONG).show();
                } finally {
                    db.close();
                    dbHelper.close();
                }
            });
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
