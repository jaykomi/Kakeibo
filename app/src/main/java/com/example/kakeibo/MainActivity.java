package com.example.kakeibo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kakeibo.database.KakeiboDataDBHelper;
import com.example.kakeibo.login.HashingActivity;
import com.example.kakeibo.login.LoginActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText password, email;
    CheckBox showPassword;
    KakeiboDataDBHelper dbHelper;
    SQLiteDatabase db;
    Button buttonLogin;
    TextView textviewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new KakeiboDataDBHelper(this);

        email = findViewById(R.id.textUsernameEmail);
        password = findViewById(R.id.textPassword);
        showPassword = findViewById(R.id.showPassword);
        textviewRegister = findViewById(R.id.textCreateUser);
        buttonLogin = findViewById(R.id.buttonLogin);

        show_hide_pass();



        /* Intent die dagelijks gebroadcast wordt om 12:35. Zowel wanneer de applicatie actief is als wanneer deze afgesloten is.
        android doet moeilijk over batterij verbruik dus er zijn veel bugs bekend met het systeem op dit moment. Het systeem stelt een vast tijdstip aan. koppelt hier een intent aan die op
        een later tijdstip uitgevoerd moet worden en geeft deze een request code (Moest uniek zijn). De alerm manager wakkert het systeem aan, registreert de kalender tijd en voert de pending intent uit.
        Het is een vage constructie die regelmatig crasht na updates of systeem changes. Een app zoals firebase biedt een betere uitkomst. Uiteindelijk werkend gekeregen door de applicatie na opstarten
        te laten controleren of de tijd al voorbij is en daarna gewoon de interval-day te gebruiken.
         */

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, NotificationHandler.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(  this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
        alarmManager.cancel(pendingIntent);

        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 12);
        alarmStartTime.set(Calendar.MINUTE, 35);
        alarmStartTime.set(Calendar.SECOND, 0);
        if (now.after(alarmStartTime)) {
            Log.d("Hey","Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("Alarm","Alarms set for everyday 12:35.");


        // open deze activity wanneer de gebruiker een account wilt registreren.
        textviewRegister.setOnClickListener(
                v -> {
                    Intent actieCreate = new Intent(v.getContext(), CreateAccount.class);
                    startActivity(actieCreate);
                });
    }

    // verberg of show het password
    public void show_hide_pass() {
        showPassword.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                // hide password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            } else {
                // show password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });


        // login functie aangepast vanaf vorige applicatie. aanpassing zit vooral in de hashing
        buttonLogin.setOnClickListener(v -> {

            db = dbHelper.getReadableDatabase();
            Cursor c = db.rawQuery("Select * from users ", null);

            try {
                LoginActivity Log = new LoginActivity();
                String usernameString = email.getText().toString();
                String passwordHashed = HashingActivity.main(password.getText().toString());

                if (Log.login(c, usernameString, passwordHashed)) {

                    Intent actieCreate = new Intent(v.getContext(), MainMenu.class);
                    startActivity(actieCreate);
                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this, "Invalid login credentials!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception se) {

                Toast.makeText(MainActivity.this, "Error!" + se, Toast.LENGTH_LONG).show();
            } finally {
                email.getText().clear();
                password.getText().clear();
                db.close();
                dbHelper.close();
            }
        });
    }


}







