package com.example.kakeibo.database;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.kakeibo.database.KakeiboDataContract.Purchases;
import com.example.kakeibo.database.KakeiboDataContract.ReportCards;
import com.example.kakeibo.database.KakeiboDataContract.Users;
import com.example.kakeibo.database.KakeiboDataContract.Wallets;


public class KakeiboDataDBHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "KakeiboData.db";



    private static final String SQL_CREATE_USERS = "create table " + Users.TABLE_NAME + " (" +
            Users.COLUMN_NAME_EMAIL + " text primary key," +
            Users.COLUMN_NAME_NAME + " text NOT NULL," +
            Users.COLUMN_NAME_DATEOFBIRTH + " text NOT NULL," +
            Users.COLUMN_NAME_PASSWORD + " text NOT NULL)";

    private static final String SQL_CREATE_WALLETS = "create table " + Wallets.TABLE_NAME + " (" +
            Wallets.COLUMN_NAME_WALLETNAME + " text primary key," +
            Wallets.COLUMN_NAME_STARTDATE + " text DEFAULT (date('now'))," +
            Wallets.COLUMN_NAME_ENDDATE + " text DEFAULT (date('now', '+30 days'))," +
            Wallets.COLUMN_NAME_INCOME + " real NOT NULL," +
            Wallets.COLUMN_NAME_FIXEDEXPENSES + " real NOT NULL," +
            Wallets.COLUMN_NAME_SAVINGSGOAL + " integer NOT NULL," +
            Wallets.COLUMN_NAME_SPENDINGCASH + " real NOT NULL," +
            Wallets.COLUMN_NAME_SAVINGFOR + " text NOT NULL)";

    private static final String SQL_CREATE_PURCHASES = "create table " + Purchases.TABLE_NAME + " (" +
            Purchases.COLUMN_NAME_PURCHASE_ID + " integer primary key AUTOINCREMENT," +
            Purchases.COLUMN_NAME_PURCHASE + " text NOT NULL," +
            Purchases.COLUMN_NAME_DATE + " text NOT NULL," +
            Purchases.COLUMN_NAME_AMOUNT + " real NOT NULL," +
            Purchases.COLUMN_NAME_PURCHASECATEGORY + " text NOT NULL)";

    private static final String SQL_CREATE_REPORTCARDS = "create table " + ReportCards.TABLE_NAME + " (" +
            ReportCards.COLUMN_NAME_WALLETNAME + " text PRIMARY KEY," +
            ReportCards.COLUMN_NAME_GOALACHIEVED + " text NOT NULL," +
            ReportCards.COLUMN_NAME_IMPROVEMENT + " text NOT NULL," +
            ReportCards.COLUMN_NAME_IMPROVEHOW + " text NOT NULL," +
            "CONSTRAINT fk_wallets_walletname FOREIGN KEY(walletname) REFERENCES wallets(walletname))";

    public KakeiboDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_WALLETS);
        db.execSQL(SQL_CREATE_PURCHASES);
        db.execSQL(SQL_CREATE_REPORTCARDS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            for (int i = oldVersion; i < newVersion; ++i) {

            }
        } catch (Exception exception) {
            Log.d("Exception running upgrade script:", String.valueOf(exception));
        }

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);

    }


}