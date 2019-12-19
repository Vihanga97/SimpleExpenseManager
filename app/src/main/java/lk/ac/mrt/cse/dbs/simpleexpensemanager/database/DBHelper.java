package lk.ac.mrt.cse.dbs.simpleexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
                DBConstant.ACCOUNT_BALANCE + " NUMERIC NOT NULL" +
                ");"
        );

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                DBConstant.TRANSACTION_TABLE_NAME + "(" +
                DBConstant.TRANSACTION_ID + " INTEGER PRIMARY KEY, " +
                DBConstant.TRANSACTION_ACCOUNT_NUM + " VARCHAR NOT NULL, " +
                DBConstant.TRANSACTION_TYPE + " VARCHAR NOT NULL, " +
                DBConstant.TRANSACTION_DATE + " TIMESTAMP NOT NULL, " +
                DBConstant.TRANSACTION_AMOUNT + " NUMERIC NOT NULL, " +
                " FOREIGN KEY (" + DBConstant.TRANSACTION_ACCOUNT_NUM + ") REFERENCES " +
                DBConstant.ACCOUNT_TABLE_NAME + "(" + DBConstant.ACCOUNT_ACCOUNT_NUM + ")," +
                " FOREIGN KEY (" + DBConstant.TRANSACTION_TYPE + ") REFERENCES " +
                DBConstant.EXPENSE_TYPE_TABLE_NAME + "(" + DBConstant.EXPENSE_TYPE_NAME + ")" +
                ");"
        );

        sqLiteDatabase.execSQL("INSERT INTO " + DBConstant.EXPENSE_TYPE_TABLE_NAME + "(" +
                DBConstant.EXPENSE_TYPE_NAME + ") VALUES (?);", new String[] {DBConstant.TYPE_OF_EXPENSE}
        );

        sqLiteDatabase.execSQL("INSERT INTO " + DBConstant.EXPENSE_TYPE_TABLE_NAME + "(" +
                DBConstant.EXPENSE_TYPE_NAME + ") VALUES (?);", new String[] {DBConstant.TYPE_OF_INCOME}
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int previousVersion, int newVersion) {
        String msg = String.format("Upgrading the Database from %s to %s", Integer.toString(previousVersion), Integer.toString(newVersion));
        Log.w(this.getClass().getName(), msg);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBConstant.EXPENSE_TYPE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBConstant.ACCOUNT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBConstant.TRANSACTION_TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
