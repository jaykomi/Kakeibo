package com.example.kakeibo.database;


public final class KakeiboDataContract {


    private KakeiboDataContract() {

    }
        public static class Users {
            public static final String TABLE_NAME = "users";
            public static final String COLUMN_NAME_EMAIL = "email";
            public static final String COLUMN_NAME_NAME = "name";
            public static final String COLUMN_NAME_DATEOFBIRTH = "dateofBirth";
            public static final String COLUMN_NAME_PASSWORD = "password";
        }
        public static class Wallets {
            public static final String TABLE_NAME = "wallets";
            public static final String COLUMN_NAME_WALLETNAME = "walletname";
            public static final String COLUMN_NAME_STARTDATE = "startdate";
            public static final String COLUMN_NAME_ENDDATE = "enddate";
            public static final String COLUMN_NAME_INCOME = "income";
            public static final String COLUMN_NAME_FIXEDEXPENSES = "fixedexpenses";
            public static final String COLUMN_NAME_SAVINGSGOAL = "savingsgoal";
            public static final String COLUMN_NAME_SPENDINGCASH = "spendingcash";
            public static final String COLUMN_NAME_SAVINGFOR = "savingfor";
        }
        public static class Purchases  {
            public static final String TABLE_NAME = "purchases";
            public static final String COLUMN_NAME_PURCHASE_ID = "_id";
            public static final String COLUMN_NAME_DATE = "date";
            public static final String COLUMN_NAME_PURCHASE = "purchase";
            public static final String COLUMN_NAME_AMOUNT = "amount";
            public static final String COLUMN_NAME_PURCHASECATEGORY = "purchasecategory";
        }

        public static class ReportCards {
            public static final String TABLE_NAME = "reportcards";
            public static final String COLUMN_NAME_WALLETNAME = "walletname";
            public static final String COLUMN_NAME_GOALACHIEVED = "goalachieved";
            public static final String COLUMN_NAME_IMPROVEMENT = "improvement";
            public static final String COLUMN_NAME_IMPROVEHOW = "improvehow";
        }
}
