package com.example.kakeibo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.kakeibo.database.KakeiboDataDBHelper;
import com.example.kakeibo.model.Purchase;

import java.text.SimpleDateFormat;
import java.util.Calendar;



public class PurchaseFragment extends Fragment {

    Calendar calendar;
    EditText dateText, nameText, amountText;
    DatePickerDialog dateDialog;
    Spinner categorySpinner;
    ImageButton createPurchase, helpButton;
    KakeiboDataDBHelper dbHelper;
    SQLiteDatabase db;
    Double amount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate de layout van het fragment op het geheel.
        View v = inflater.inflate(R.layout.fragment_purchase, container, false);

        dbHelper = new KakeiboDataDBHelper(getActivity());

        // Link tussen activiteit en visueele ID's leggen
        dateText = v.findViewById(R.id.editTextPurchaseDate);
        nameText = v.findViewById(R.id.editTextTextPurchaseName);
        amountText = v.findViewById(R.id.editTextPurchasePrice);
        categorySpinner = v.findViewById(R.id.spinnerPurchaseCategory);
        createPurchase = v.findViewById(R.id.addPurchaseButton);
        helpButton = v.findViewById(R.id.purchaseHelpButton);

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hulp knop wanneer informatie eventueel onduidelijk is.
        helpButton.setOnClickListener(vo ->
        {
            AlertDialog.Builder builderPurchaseHelp = new AlertDialog.Builder(getContext());
            builderPurchaseHelp.setTitle(" Information");
            builderPurchaseHelp.setMessage("Remember! the more active you are with registering your purchases the better the results. The individual definitions of the purchase category's are explained below." +
                    "\n" +
                    "\n" +
                    "Category wants: Things you can’t live without, like food, toilet paper, and shampoo.  " +
                    "\n" +
                    "\n" +
                    "Category needs: Things you can’t live without, like food, toilet paper, and shampoo." +
                    "\n" +
                    "\n" +
                    "Category cultural: Things like books and museum visits." +
                    "\n" +
                    "\n" +
                    "Category unexpected: Expenses you were not anticipating, like a doctor’s visit or car repair."
            );
            builderPurchaseHelp.setIcon(android.R.drawable.ic_dialog_info);
            builderPurchaseHelp.setNegativeButton("Close", (dialogPurchases, i) -> dialogPurchases.dismiss()
            );
            builderPurchaseHelp.show();
        });


        // initialising de date picker dialog
        dateDialog = new DatePickerDialog(getActivity());

        // click op datum edittext om de waarde te zetten
        dateText.setOnClickListener(va -> {
            dateDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view, year1, month1, day1) -> {
                // voeg de datum toe aan de date picker dialog. Wanneer de dag of maand bestaan uit 1 getal voeg een leidende 0 toe.
                month1 = month1 +1;
                String formatMonth=""+month1;
                String formatDay=""+day1;
                if(month1<10){
                    formatMonth ="0"+month1;
                }
                if (day1<10){
                    formatDay="0"+day1;
                }
                String date= ""+year1+"-"+formatMonth+"-"+formatDay;
                dateText.setText(date);
            }, year, month, day);

            // maximale datum is de datum van vandaag.
            dateDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

            // laat de dialog zien
            dateDialog.show();
        });




        // functionaliteit nadat gebruiker op de knop registreer gebruiker drukt
        createPurchase.setOnClickListener(vi -> {

            db = dbHelper.getWritableDatabase();
            Purchase purchase = new Purchase();



            // Zet de waardes in variabelen om controle op de regex uit te voeren.

            String name = nameText.getText().toString();
            String date = dateText.getText().toString();
            String amountString = amountText.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();


            /*
             * voer regex controle vanuit de classe User uit en geef aan of variabele voldoet of niet
             */

            boolean result1 = Purchase.isValidDateOfBirth(date);
            boolean result2 = Purchase.isValidAmount(amountString);

            /*
            Wanneer de variabelen niet aan de regex voldoen of leeg zijn geef een setError op de editText en vertel de gebruiker
            wat er mis gaat. In eerste instantie besloten om geen regex voor geboortedatum uit te voeren totdat ik merkte dat je via een
            emulator in het dateveld kan tabben en zelf data in kan voeren.
             */


            if (!result1) {
                dateText.setError("Date of birth is incorrect! format should be dd-mm-yyyy");
                return;
            }
            if (!result2) {
                amountText.setError("Invalid purchase amount!");
                return;
            }
            if (nameText.getText().toString().isEmpty()) {
                nameText.setError("Purchase must be named!");

            } else {

                //verbergt het toetsenbord zodra alle variabelen niet leeg en goedgekeurd zijn.
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(vi.getWindowToken(), 0);

                try {


                    /* is het veld niet leeg en voldoet het aan de regex?
                    parse het dan pas als double. Een null value op een double laat de applicatie crashen
                    */
                    amount = Double.parseDouble(amountText.getText().toString());
                /*
                zijn er geen fouten aangetroffen in de user input? ga dan de database verbinding maken en de data
                daarin wegzetten
                 */

                    purchase.setPurchase(name);
                    purchase.setPurchaseAmount(amount);
                    purchase.setPurchaseDate(date);
                    purchase.setPurchaseCategory(category);

                    // zet de waardes in de values
                    ContentValues values = new ContentValues();

                    values.put("DATE", purchase.getPurchaseDate());
                    values.put("PURCHASE", purchase.getPurchase());
                    values.put("AMOUNT", purchase.getPurchaseAmount());
                    values.put("PURCHASECATEGORY", purchase.getPurchaseCategory());

                    // voer de values in de database table users in.
                    long result = db.insert("PURCHASES", null, values);


                    // reageer op basis van de terugvoer "result" van de database.
                    if (result != -1) {
                        Toast.makeText(getActivity(), "Purchase " + purchase.getPurchase() + " registered!", Toast.LENGTH_LONG).show();
                        nameText.getText().clear();
                        dateText.getText().clear();
                        amountText.getText().clear();
                        categorySpinner.setSelection(0);

                        /* extreem slechte oplossing alleen om de een of andere reden kreeg ik
                            de functie voor het herladen van de adapter in de listview hier niet werkend.
                         */

                        Intent actieCreate = new Intent(v.getContext(), PurchaseActivity.class);
                        startActivity(actieCreate);

                    }
                    if (result == -1) {
                        Toast.makeText(getActivity(), "Failed to register purchase!", Toast.LENGTH_LONG).show();
                    }
                } catch (SQLiteException | NumberFormatException e ) {
                    Toast.makeText(getActivity(), "Error!" + e, Toast.LENGTH_LONG).show();
                } finally {
                  db.close();
                    dbHelper.close();
                }
            }
        });
        return v;

    }
}