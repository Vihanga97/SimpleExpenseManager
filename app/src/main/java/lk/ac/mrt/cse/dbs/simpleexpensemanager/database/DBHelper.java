package lk.ac.mrt.cse.dbs.simpleexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "170275K";

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                DBConstant.EXPENSE_TYPE_TABLE_NAME + "(" +
                DBConstant.EXPENSE_TYPE_NAME + " VARCHAR(64) PRIMARY KEY " +
                ");"
        );

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                DBConstant.ACCOUNT_TABLE_NAME + "(" +
                DBConstant.ACCOUNT_ACCOUNT_NUM + " VARCHAR PRIMARY KEY, " +
                DBConstant.ACCOUNT_BANK_NAME + " VARCHAR NOT NULL, " +
                DBConstant.ACCOUNT_HOLDER_NAME + " VARCHAR NOT NULL, " +
                DBConstant.ACCOUNT_BALANCE + " DECIMAL(128,2) NOT NULL" +
                ");"
        );

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                DBConstant.TRANSACTION_TABLE_NAME + "(" +
                DBConstant.TRANSACTION_ID + " INTEGER PRIMARY KEY, " +
                DBConstant.TRANSACTION_ACCOUNT_NUM + " VARCHAR NOT NULL, " +
                DBConstant.TRANSACTION_TYPE + " VARCHAR NOT NULL, " +
                DBConstant.TRANSACTION_DATE + " TIMESTAMP NOT NULL, " +
                DBConstant.TRANSACTION_AMOUNT + " DECIMAL(128,2) NOT NULL, " +
                " FOREIGN KEY (" + DBConstant.TRANSACTION_ACCOUNT_NUM + ") REFERENCES " +
                DBConstant.ACCOUNT_TABLE_NAME + "(" + DBConstant.ACCOUNT_ACCOUNT_NUM + ")," +
                " FOREIGN KEY (" + DBConstant.TRANSACTION_TYPE + ") REFERENCES " +
                DBConstant.EXPENSE_TYPE_TABLE_NAME + "(" + DBConstant.EXPENSE_TYPE_NAME + ")" +
                ");"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
