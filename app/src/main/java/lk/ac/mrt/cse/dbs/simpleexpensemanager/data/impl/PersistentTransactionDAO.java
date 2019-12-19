package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.database.DBConstant;

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteOpenHelper helper;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    public PersistentTransactionDAO(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    @Override
    public void logTransaction(Date date, String accNum, ExpenseType expType, double amount) {
        if (accNum == null) return;

        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstant.TRANSACTION_DATE, dateFormat.format(date));
        values.put(DBConstant.TRANSACTION_ACCOUNT_NUM, accNum);
        values.put(DBConstant.TRANSACTION_TYPE, expType.toString());
        values.put(DBConstant.TRANSACTION_AMOUNT, amount);
        sqLiteDatabase.insert(DBConstant.TRANSACTION_TABLE_NAME, null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBConstant.TRANSACTION_TABLE_NAME +
                " ORDER BY " + DBConstant.TRANSACTION_ID + " DESC ", null);
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                String expTypeString = cursor.getString(2);
                ExpenseType expType = ExpenseType.EXPENSE;
                if (expTypeString.equals(DBConstant.TYPE_OF_INCOME)) {
                    expType = ExpenseType.INCOME;
                }
                Transaction tr = new Transaction(dateFormat.parse(cursor.getString(3)),
                        cursor.getString(1), expType, cursor.getDouble(4));
                transactions.add(tr);
            } catch (ParseException ignored) {
            }
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + DBConstant.TRANSACTION_TABLE_NAME +
                " order by " + DBConstant.TRANSACTION_ID + " desc " +
                " limit ?;", new String[]{Integer.toString(limit)});
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                String expTypeString = cursor.getString(2);
                ExpenseType expType = ExpenseType.EXPENSE;
                if (expTypeString.equals(DBConstant.TYPE_OF_INCOME)) {
                    expType = ExpenseType.INCOME;
                }
                Transaction transaction = new Transaction(dateFormat.parse(cursor.getString(3)),
                        cursor.getString(1), expType, cursor.getDouble(4));
                transactions.add(transaction);
            } catch (ParseException ignored) {
            }
        }
        cursor.close();
        return transactions;
    }
}
