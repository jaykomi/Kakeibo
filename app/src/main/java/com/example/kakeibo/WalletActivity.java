package com.example.kakeibo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kakeibo.database.KakeiboDataContract;
import com.example.kakeibo.database.KakeiboDataDBHelper;
import com.example.kakeibo.model.Wallet;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class WalletActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    SQLiteDatabase db;
    KakeiboDataDBHelper dbHelper;
    ListView listWallets;
    ImageButton createWallet, updateWallet;
    EditText nameText, incomeText, expensesText, savingsText, savingForText;
    Double amountIncome, amountExpense, amountSpending;
    Integer amountSavings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        // Link tussen activiteit en visueele ID's leggen
        bottomNavigation = findViewById(R.id.bottomNavigationWallet);
        bottomNavigation.setSelectedItemId(R.id.invisibleWallet);
        listWallets = findViewById(R.id.listviewWallets);
        createWallet = findViewById(R.id.buttonCreateWallet);
        updateWallet = findViewById(R.id.buttonUpdateWallet);

        nameText = findViewById(R.id.editTextWalletName);
        incomeText = findViewById(R.id.editTextWalletIncome);
        expensesText = findViewById(R.id.editTextWalletExpenses);
        savingsText = findViewById(R.id.editTextWalletSavings);
        savingForText = findViewById(R.id.editTextSavingFor);

        dbHelper = new KakeiboDataDBHelper(this);

        // zet de variabelen die returnen uit viewWallets in de adapter en zet deze in de list
        SimpleCursorAdapter simpleCursorAdapter = viewWallets();
        listWallets.setAdapter(simpleCursorAdapter);

        // andere manier van het toetsenbord verbergen
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /* klik op een specifieke wallet uit de list en laad deze gegevens en variabelen en zet deze in de edittexts.
        op deze manier kan een update sneller uitgevoerd worden.
         */
        listWallets.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);

            String walletName = cursor.getString(1);
            double walletIncome = cursor.getDouble(4);
            double walletExpenses = cursor.getDouble(5);
            double walletSavings = cursor.getDouble(6);
            String walletSavingFor = cursor.getString(8);


            nameText.setText(walletName);
            incomeText.setText(String.valueOf(walletIncome));
            expensesText.setText(String.valueOf(walletExpenses));
            savingsText.setText(String.valueOf(walletSavings));
            savingForText.setText(walletSavingFor);



            Toast.makeText(WalletActivity.this, nameText.getText(), Toast.LENGTH_LONG).show();
        });

        // update een bestaande wallet uit de listview.
       updateWallet.setOnClickListener(vp -> {
           db = dbHelper.getWritableDatabase();
           Wallet walletUpdate = new Wallet();

           // Zet de waardes in variabelen om controle op de regex uit te voeren.

           String walletName = nameText.getText().toString();
           String walletIncomeString = incomeText.getText().toString();
           String walletExpenseString = expensesText.getText().toString();
           String walletSavingsString = savingsText.getText().toString();
           String walletSavingFor = savingForText.getText().toString();

           /*
            * voer regex controle vanuit de classe Wallet uit en geef aan of variabele voldoet of niet
            */

           boolean result1 = Wallet.isValidAmount(walletIncomeString);
           boolean result2 = Wallet.isValidAmount(walletExpenseString);
           boolean result3 = Wallet.isValidAmount(walletSavingsString);


            /*
            Wanneer de variabelen niet aan de regex voldoen of leeg zijn geef een setError op de editText en vertel de gebruiker
            wat er mis gaat. Veel data gaat buiten invloed van de gebruiker om en hoeft dus niet gecontroleerd te worden. Wanner onderstaande waardes voldoen
            kan het systeem zelf deze waardes invullen

             */

           if (!result1) {
               incomeText.setError("Invalid income amount entered!");
               return;
           }
           if (!result2) {
               expensesText.setError("Invalid expenses amount entered!");
               return;
           }
           if (!result3) {
               savingsText.setError("Invalid savings amount entered!");
               return;
           }

           // result 4 wordt hier pas aangemaakt en uitgevoerd anders crasht het programma doordat er een null value aan een double wordt gegeven in de functie isValidSavingsGoal

           boolean result4 = Wallet.isValidSavingsGoal(walletIncomeString, walletExpenseString, walletSavingsString);


           if (!result4) {
               savingsText.setError("Savings goal + expenses exceeds income!");
               expensesText.setError("Expenses + savings goal exceeds income!");

               AlertDialog.Builder builderWallets = new AlertDialog.Builder(this);
               builderWallets.setTitle(" Information");
               builderWallets.setMessage("\n" +
                       "According to the information you've supplied your monthly expenses and savings goal exceeds your monthly income" +
                       "\n" +
                       "\n" +
                       "Should your monthly expenses alone exceed your monthly income we advise you to seek direct financial help from local authority" +
                       "\n" +
                       "\n" +
                       "Don't wait! The sooner you ask for help, the less damage. You're now going to be redirected to the Stepchange website for debt info. "
               );
               builderWallets.setIcon(android.R.drawable.ic_dialog_info);
               builderWallets.setNegativeButton("Close", (dialogPurchases, i) -> dialogPurchases.dismiss());
               builderWallets.setOnDismissListener(dialog -> {
                   String url = "https://www.stepchange.org/debt-info/emergency-funding.aspx";
                   Intent webFinancialHelp = new Intent(Intent.ACTION_VIEW);
                   webFinancialHelp.setData(Uri.parse(url));
                   startActivity(webFinancialHelp);
               });
               builderWallets.show();
               return;
           }

           if (savingForText.getText().toString().isEmpty()) {
               savingForText.setError("Savings goal must be given!");
           } else {

               //verbergt het toetsenbord zodra alle variabelen niet leeg en goedgekeurd zijn.
               InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
               inputManager.hideSoftInputFromWindow(vp.getWindowToken(), 0);


               try {

                    /* is het veld niet leeg en voldoet het aan de regex?
                    parse het dan pas als double. Een null value op een double laat de applicatie crashen
                    */

                   amountIncome = Double.parseDouble(incomeText.getText().toString());
                   amountExpense = Double.parseDouble(expensesText.getText().toString());
                   amountSavings = Integer.parseInt(savingsText.getText().toString());
                   amountSpending = amountIncome - amountExpense - amountSavings;

                /*
                zijn er geen fouten aangetroffen in de user input? ga dan de database verbinding maken en de data
                daarin wegzetten
                 */

                   walletUpdate.setWalletName(walletName);
                   walletUpdate.setIncome(amountIncome);
                   walletUpdate.setFixedExpenses(amountExpense);
                   walletUpdate.setSavingsGoal(amountSavings);
                   walletUpdate.setSpendingCash(amountSpending);
                   walletUpdate.setSavingFor(walletSavingFor);


                   // zet de waardes in de values
                   ContentValues values = new ContentValues();


                   values.put("income", walletUpdate.getIncome());
                   values.put("fixedexpenses", walletUpdate.getFixedExpenses());
                   values.put("savingsgoal", walletUpdate.getSavingsGoal());
                   values.put("spendingcash", walletUpdate.getSpendingCash());
                   values.put("savingfor", walletUpdate.getSavingFor());


                   // voer de values in de database table users in.
                   long result = db.update("wallets", values, "walletname = ?", new String[]{walletUpdate.getWalletName()});



                   // reageer op basis van de terugvoer "result" van de database.

                   if (result != -1) {
                       Toast.makeText(this, "Wallet " + walletUpdate.getWalletName() + " updated!", Toast.LENGTH_LONG).show();
                       nameText.getText().clear();
                       incomeText.getText().clear();
                       expensesText.getText().clear();
                       savingsText.getText().clear();
                       savingForText.getText().clear();


                       /* refresh the adapter aswell as the listview - Had niet het gewenste effect. ergens in de memory blijft de vorige variatie van die portemonnee naam bestaan.
                       de functionaliteit werkt alleen de gebruiker krijgt niet de geupdate versie te zien. Daarom gekozen om de pagina compleet te verversen. wat wel het gewenste effect heeft.
                        */

                       Intent actieCreate = new Intent(vp.getContext(), WalletActivity.class);
                       startActivity(actieCreate);

                   }
                   if (result == -1) {
                       Toast.makeText(this, "Failed to update wallet!", Toast.LENGTH_LONG).show();
                   }
               } catch (SQLiteException | NumberFormatException e) {
                   Toast.makeText(this, "Error!" + e, Toast.LENGTH_LONG).show();
               } finally {
                   db.close();
                   dbHelper.close();
               }
           }
       });

        // maak een nieuwe wallet aan
        createWallet.setOnClickListener(vi -> {


            db = dbHelper.getWritableDatabase();
            Wallet wallet = new Wallet();

            // Zet de waardes in variabelen om controle op de regex uit te voeren.

            String walletName = nameText.getText().toString();
            String walletIncomeString = incomeText.getText().toString();
            String walletExpenseString = expensesText.getText().toString();
            String walletSavingsString = savingsText.getText().toString();
            String walletSavingFor = savingForText.getText().toString();

            /*
             * voer regex controle vanuit de classe Wallet uit en geef aan of variabele voldoet of niet
             */

            boolean result1 = Wallet.isValidAmount(walletIncomeString);
            boolean result2 = Wallet.isValidAmount(walletExpenseString);
            boolean result3 = Wallet.isValidAmount(walletSavingsString);


            /*
            Wanneer de variabelen niet aan de regex voldoen of leeg zijn geef een setError op de editText en vertel de gebruiker
            wat er mis gaat. Veel data gaat buiten invloed van de gebruiker om en hoeft dus niet gecontroleerd te worden. Wanner onderstaande waardes voldoen
            kan het systeem zelf deze waardes invullen
             */


            if (nameText.getText().toString().isEmpty()) {
                nameText.setError("Wallet must be named for future reference!");
            }

            if (!result1) {
                incomeText.setError("Invalid income amount entered!");
                return;
            }
            if (!result2) {
                expensesText.setError("Invalid expenses amount entered!");
                return;
            }
            if (!result3) {
                savingsText.setError("Invalid savings amount entered!");
                return;
            }

            // result 4 wordt hier pas aangemaakt en uitgevoerd anders crasht het programma doordat er een null value aan een double wordt gegeven in de functie isValidSavingsGoal

            boolean result4 = Wallet.isValidSavingsGoal(walletIncomeString, walletExpenseString, walletSavingsString);

            /* geeft een error wanneer er iets structureels mis is met de financien en wijst de mensen door naar een website voor nood schuld hulpverlening
             */
            if (!result4) {
                savingsText.setError("Savings goal + expenses exceeds income!");
                expensesText.setError("Expenses + savings goal exceeds income!");

                AlertDialog.Builder builderWallets = new AlertDialog.Builder(this);
                builderWallets.setTitle(" Information");
                builderWallets.setMessage("\n" +
                        "According to the information you've supplied your monthly expenses and savings goal exceeds your monthly income" +
                        "\n" +
                        "\n" +
                        "Should your monthly expenses alone exceed your monthly income we advise you to seek direct financial help from local authority" +
                        "\n" +
                        "\n" +
                        "Don't wait! The sooner you ask for help, the less damage. You're now going to be redirected to the Stepchange website for debt info. "
                );
                builderWallets.setIcon(android.R.drawable.ic_dialog_info);
                builderWallets.setNegativeButton("Close", (dialogPurchases, i) -> dialogPurchases.dismiss());
                builderWallets.setOnDismissListener(dialog -> {
                    String url = "https://www.stepchange.org/debt-info/emergency-funding.aspx";
                    Intent webFinancialHelp = new Intent(Intent.ACTION_VIEW);
                    webFinancialHelp.setData(Uri.parse(url));
                    startActivity(webFinancialHelp);
                });
                builderWallets.show();
                return;
            }

            if (savingForText.getText().toString().isEmpty()) {
                savingForText.setError("Savings goal must be given!");
            } else {

                //verbergt het toetsenbord zodra alle variabelen niet leeg en goedgekeurd zijn.
                InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(vi.getWindowToken(), 0);

                try {

                    /* is het veld niet leeg en voldoet het aan de regex?
                    parse het dan pas als double. Een null value op een double laat de applicatie crashen
                    */
                    amountIncome = Double.parseDouble(incomeText.getText().toString());
                    amountExpense = Double.parseDouble(expensesText.getText().toString());
                    amountSavings = Integer.parseInt(savingsText.getText().toString());
                    amountSpending = amountIncome - amountExpense - amountSavings;
                /*
                zijn er geen fouten aangetroffen in de user input? ga dan de database verbinding maken en de data
                daarin wegzetten
                 */

                    wallet.setWalletName(walletName);
                    wallet.setIncome(amountIncome);
                    wallet.setFixedExpenses(amountExpense);
                    wallet.setSavingsGoal(amountSavings);
                    wallet.setSpendingCash(amountSpending);
                    wallet.setSavingFor(walletSavingFor);


                    // zet de waardes in de values
                    ContentValues values = new ContentValues();

                    values.put("walletname", wallet.getWalletName());
                    values.put("income", wallet.getIncome());
                    values.put("fixedexpenses", wallet.getFixedExpenses());
                    values.put("savingsgoal", wallet.getSavingsGoal());
                    values.put("spendingcash", wallet.getSpendingCash());
                    values.put("savingfor", wallet.getSavingFor());


                    // voer de values in de database table users in.
                    long result = db.insert("WALLETS", null, values);


                    // reageer op basis van de terugvoer "result" van de database.
                    if (result != -1) {
                        Toast.makeText(this, "Wallet " + wallet.getWalletName() + " registered!", Toast.LENGTH_LONG).show();
                        nameText.getText().clear();
                        incomeText.getText().clear();
                        expensesText.getText().clear();
                        savingsText.getText().clear();
                        savingForText.getText().clear();


                        //refresh the adapter aswell as the listview
                        SimpleCursorAdapter simpleCursorAdapter1 = viewWallets();
                        listWallets.setAdapter(simpleCursorAdapter1);

                    }
                    if (result == -1) {
                        Toast.makeText(this, "Failed to register wallet!", Toast.LENGTH_LONG).show();
                    }
                } catch (SQLiteException | NumberFormatException e) {
                    Toast.makeText(this, "Error!" + e, Toast.LENGTH_LONG).show();
                } finally {
                    db.close();
                    dbHelper.close();
                }
            }
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

    @SuppressWarnings("deprecation")
    public SimpleCursorAdapter viewWallets() {

        db = dbHelper.getReadableDatabase();

        /* Verwerk de data van de tabel purchases in de veldnamen en voor ieder nieuwe _id creeer een nieuw view ID.
         Order de purchases op descending ID zodat de laatste bovenaan komt te staan en laat alleen de laatste 20 zien.
         */

        Cursor cursor = db.rawQuery("SELECT  rowid _id, walletname, startdate, enddate, income, fixedexpenses, savingsgoal, spendingcash, savingfor FROM wallets", null);
        String[] fromFieldNames = new String[]{
                KakeiboDataContract.Wallets.COLUMN_NAME_WALLETNAME, KakeiboDataContract.Wallets.COLUMN_NAME_STARTDATE, KakeiboDataContract.Wallets.COLUMN_NAME_ENDDATE, KakeiboDataContract.Wallets.COLUMN_NAME_INCOME, KakeiboDataContract.Wallets.COLUMN_NAME_FIXEDEXPENSES, KakeiboDataContract.Wallets.COLUMN_NAME_SAVINGSGOAL, KakeiboDataContract.Wallets.COLUMN_NAME_SPENDINGCASH, KakeiboDataContract.Wallets.COLUMN_NAME_SAVINGFOR};
        int[] toViewIDs = new int[]{R.id.walletName, R.id.walletStartDate, +R.id.walletEndDate, R.id.walletIncome, R.id.walletExpenses, R.id.walletSavingsGoal, R.id.walletSpendingCash, R.id.walletSavingFor};
        return new SimpleCursorAdapter(
                this,
                R.layout.single_wallet,
                cursor,
                fromFieldNames,
                toViewIDs
        ) {
        };
    }
}





