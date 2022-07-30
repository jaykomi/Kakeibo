package com.example.kakeibo.login;



import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;



public class LoginActivity extends AppCompatActivity {

    // Login activity geeft true of false terug wanneer gegevens overeenkomen in database of niet
    public boolean login(Cursor c,String email,String password) {
        while (c.moveToNext()){
            if (c.getString(0).equals(email)) {
                if (c.getString(3).equals(password)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}