package com.example.kakeibo;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.kakeibo.database.KakeiboDataContract;
import com.example.kakeibo.database.KakeiboDataDBHelper;


public class PurchaseHistoryFragment extends Fragment {

    SQLiteDatabase db;
    KakeiboDataDBHelper dbHelper;
    ListView listPurchases;
    Button createPurchase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate de layout van het fragment.
        View v = inflater.inflate(R.layout.fragment_purchase_history, container, false);

        dbHelper = new KakeiboDataDBHelper(getActivity());

        // Link tussen activiteit en visueele ID's leggen
        listPurchases = v.findViewById(R.id.listviewPurchaseHistory);
        createPurchase = v.findViewById(R.id.addPurchaseButton);
        SimpleCursorAdapter simpleCursorAdapter = viewPurchaseHistory();
        listPurchases.setAdapter(simpleCursorAdapter);




        listPurchases.setOnItemLongClickListener((purchase, v1, position, id) -> {

            /* druk voor een aantal seconden op een item uit de listview en er wordt een alert geopend
            waarin gevraagd word of je een item echt wilt verwijderen of niet
             */

            AlertDialog.Builder builderPurchases = new AlertDialog.Builder(getActivity());
            builderPurchases.setTitle("Delete purchase");
            builderPurchases.setMessage("Are you sure you want to delete this purchase?");
            builderPurchases.setIcon(android.R.drawable.ic_dialog_info);

            /* is er op ja geklikt? registreer de positie van van de aankoop in een cursor.
            zet deze in een long en verwijder daarna uit de database wat overenkomt met het
            kolom _id uit de tabel purchases.
             */

            builderPurchases.setPositiveButton("Yes, I'm sure!", (dialogPurchases, i) -> {
                db = dbHelper.getWritableDatabase();
                Cursor cursor = (Cursor) purchase.getItemAtPosition(position);

                // Get the state's capital from this listview_item_row in the database.

                cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                db.delete("purchases", KakeiboDataContract.Purchases.COLUMN_NAME_PURCHASE_ID + "=" + id, null);



                //geef bevestiging wat verwijderd is en refresh de adapter en zet deze in de listview zodat de meest up to date data wordt getoond.

                Toast.makeText(getActivity(), "Purchases deleted!", Toast.LENGTH_LONG).show();
                SimpleCursorAdapter simpleCursorAdapter1 = viewPurchaseHistory();
                listPurchases.setAdapter(simpleCursorAdapter1);
            });
            builderPurchases.setNegativeButton("Cancel", (dialogPurchases, i) -> dialogPurchases.dismiss()
            );
            builderPurchases.show();

            return true;
        });


        return v;



    }



    @SuppressWarnings("deprecation")
    public SimpleCursorAdapter viewPurchaseHistory() {

        db = dbHelper.getReadableDatabase();

        /* Verwerk de data van de tabel purchases in de veldnamen en voor ieder nieuwe _id creeer een nieuw view ID.
         Order de purchases op descending ID zodat de laatste bovenaan komt te staan en laat alleen de laatste 20 zien.
         */

        Cursor cursor = db.rawQuery("SELECT _id, purchase, date, amount, purchasecategory FROM purchases ORDER BY _id DESC LIMIT 20", null);
        String[] fromFieldNames = new String[]{
                KakeiboDataContract.Purchases.COLUMN_NAME_PURCHASE_ID, KakeiboDataContract.Purchases.COLUMN_NAME_PURCHASE, KakeiboDataContract.Purchases.COLUMN_NAME_DATE, KakeiboDataContract.Purchases.COLUMN_NAME_AMOUNT, KakeiboDataContract.Purchases.COLUMN_NAME_PURCHASECATEGORY};
        int[] toViewIDs = new int[]{R.id.purchase_id, R.id.purchaseName, +R.id.purchaseDate, R.id.purchaseAmount, R.id.purchaseCategory};
        return new SimpleCursorAdapter(
                getContext(),
                R.layout.single_purchase,
                cursor,
                fromFieldNames,
                toViewIDs
        );


    }
}


