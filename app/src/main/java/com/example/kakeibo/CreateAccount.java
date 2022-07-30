package com.example.kakeibo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.kakeibo.database.KakeiboDataDBHelper;
import com.example.kakeibo.login.HashingActivity;
import com.example.kakeibo.model.User;
import java.util.Calendar;


public class CreateAccount extends AppCompatActivity {

    Calendar calendar;
    EditText dateText, passwordText, nameText, emailText;
    DatePickerDialog dateDialog;
    CheckBox showPassword;
    Button createUser;
    KakeiboDataDBHelper dbHelper;
    SQLiteDatabase db;
    TextView textviewReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        dbHelper = new KakeiboDataDBHelper(this);

        dateText = findViewById(R.id.textDateOfBirth);
        passwordText = findViewById(R.id.textPassword);
        nameText = findViewById(R.id.textFullName);
        emailText = findViewById(R.id.textUsernameEmail);
        showPassword = findViewById(R.id.showPassword);
        createUser = findViewById(R.id.buttonRegister);
        textviewReturn = findViewById(R.id.textReturnToMain);

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // activeer verberg / laat wachtwoord functie zien
        show_hide_pass();

        textviewReturn.setOnClickListener(
                v -> {
                    Intent actieCreate = new Intent(v.getContext(), MainActivity.class);
                    startActivity(actieCreate);
                });



        // initialitiseer de date picker dialog.
        dateDialog = new DatePickerDialog(CreateAccount.this);


        // click op datum edittext om de waarde te zetten
        dateText.setOnClickListener(v -> {
            dateDialog = new DatePickerDialog(CreateAccount.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view, year1, month1, day1) -> {
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
    }


    // Verberg of laat het wachtwoord zien
    public void show_hide_pass() {
        showPassword.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                // hide password
                passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

            } else {
                // show password
                passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        // functionaliteit nadat gebruiker op de knop registreer gebruiker drukt
        createUser.setOnClickListener(v -> {

            db = dbHelper.getWritableDatabase();
            User createUser = new User();

            // Zet de waardes in variabelen om controle op de regex uit te voeren.

            String email = emailText.getText().toString();
            String dateOfBirth = dateText.getText().toString();
            String name = nameText.getText().toString();
            String password = passwordText.getText().toString();

            /*
             * voer regex controle vanuit de classe User uit en geef aan of variabele voldoet of niet
             */

            boolean result1 = User.isValidName(name);
            boolean result2 = User.isValidEmail(email);
            boolean result3 = User.isValidPassword(password);
            boolean result4 = User.isValidDateOfBirth(dateOfBirth);

            /*
            Wanneer de variabelen niet aan de regex voldoen of leeg zijn geef een setError op de editText en vertel de gebruiker
            wat er mis gaat. In eerste instantie besloten om geen regex voor geboortedatum uit te voeren totdat ik merkte dat je via een
            emulator in het dateveld kan tabben en zelf data in kan voeren.
             */


            if (!result1) {
                nameText.setError("Invalid Name!");
                return;
            }
            if (!result2) {
                emailText.setError("Invalid email!");
                return;
            }
            if (!result3) {
                passwordText.setError("Invalid Password!" + "\n" + "- Min 8 characters" + "\n" + "- Min 1 uppercase letter" + "\n" + "- Min 1 lowercase letter" + "\n" + "- Min 1 special character");
                return;
            }

            if (!result4) {
                dateText.setError("Date of birth is incorrect! format should be dd-mm-yyyy");

            } else {

                try {

                /*
                zijn er geen fouten aangetroffen in de user input? ga dan de database verbinding maken en de data
                daarin wegzetten
                 */

                    createUser.setName(name);
                    createUser.setEmail(email);
                    createUser.setDateOfBirth(dateOfBirth);
                    createUser.setPassword(password);

                    //voer de hash functie uit op het wachtwoord en zet deze in variabele x. Dit kan direct in de setPassword gedaan worden. heb dit niet gedaan vanwege verduidelijking.
                    String x = HashingActivity.main(createUser.getPassword());

                    // zet de waardes in de values
                    ContentValues values = new ContentValues();

                    values.put("EMAIL", createUser.getEmail());
                    values.put("NAME", createUser.getName());
                    values.put("DATEOFBIRTH", createUser.getDateOfBirth());
                    values.put("PASSWORD", x);

                    // voer de values in de database table users in.
                    long result = db.insert("USERS", null, values);


                    // reageer op basis van de terugvoer "result" van de database.
                    if (result != -1) {
                        Intent actieCreate = new Intent(v.getContext(), MainActivity.class);
                        startActivity(actieCreate);
                        Toast.makeText(CreateAccount.this, "Account created, you can now login!", Toast.LENGTH_LONG).show();
                        nameText.getText().clear();
                        dateText.getText().clear();
                        emailText.getText().clear();
                        passwordText.getText().clear();
                    }
                    // dit is een aanname omdat dit volgens mij het enige is wat fout kan gaan op dit moment.
                    if (result == -1) {
                        Toast.makeText(CreateAccount.this, "Email address already in use!", Toast.LENGTH_LONG).show();
                    }
                } catch (SQLiteException ee) {
                    Toast.makeText(CreateAccount.this, "Error!" + ee, Toast.LENGTH_LONG).show();
                } finally {
                    db.close();
                    dbHelper.close();
                }
            }
        });
    }
}



