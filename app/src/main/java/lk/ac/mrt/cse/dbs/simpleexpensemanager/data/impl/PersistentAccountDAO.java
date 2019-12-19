package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.database.DBConstant;

public class PersistentAccountDAO implements AccountDAO {
    private SQLiteOpenHelper helper;

    public PersistentAccountDAO(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT " + DBConstant.ACCOUNT_ACCOUNT_NUM + " FROM "
                + DBConstant.ACCOUNT_TABLE_NAME, null);

        ArrayList<String> accountNums = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accountNums.add(cursor.getString(0));
        }
        cursor.close();
        return accountNums;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBConstant.ACCOUNT_TABLE_NAME, null);
        ArrayList<Account> accounts = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Account acc = new Account(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getDouble(3));
            accounts.add(acc);
        }
        cursor.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accNum) throws InvalidAccountException {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DBConstant.ACCOUNT_TABLE_NAME + " WHERE " + DBConstant.ACCOUNT_ACCOUNT_NUM + "=?;", new String[]{accNum});
        Account acc;
        if (cursor.moveToFirst()) {
            acc = new Account(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getDouble(3));
        } else {
            throw new InvalidAccountException("Account ID is Invalid");
        }
        cursor.close();
        return acc;
    }


    @Override
    public void addAccount(Account acc) {
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBConstant.ACCOUNT_ACCOUNT_NUM, acc.getAccountNo());
        values.put(DBConstant.ACCOUNT_BANK_NAME, acc.getBankName());
        values.put(DBConstant.ACCOUNT_HOLDER_NAME, acc.getAccountHolderName());
        values.put(DBConstant.ACCOUNT_BALANCE, acc.getBalance());

        sqLiteDatabase.insert(DBConstant.ACCOUNT_TABLE_NAME, null, values);
    }

    @Override
    public void removeAccount(String accNum) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + DBConstant.ACCOUNT_TABLE_NAME + " WHERE " + DBConstant.ACCOUNT_ACCOUNT_NUM + "=?;", new String[]{accNum});
        if (cursor.moveToFirst()) {
            sqLiteDatabase.delete(DBConstant.ACCOUNT_TABLE_NAME, DBConstant.ACCOUNT_ACCOUNT_NUM + " = ?", new String[]{accNum});
        } else {
            throw new InvalidAccountException("Account ID is Invalid");
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accNum, ExpenseType expType, double amount) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();

        if (accNum == null) throw new InvalidAccountException("Account Not Selected");

        sqLiteDatabase.beginTransaction();
        Account acc = getAccount(accNum);

        if (acc != null) {
            double newAmount;
            if (expType == ExpenseType.EXPENSE) {
                newAmount = acc.getBalance() - amount;
            } else if (expType == ExpenseType.INCOME) {
                newAmount = acc.getBalance() + amount;
            } else {
                throw new InvalidAccountException("Unknown Expense Type");
            }

            if (newAmount < 0){
                throw  new InvalidAccountException("Insufficient balance. (" + acc.getBalance() + " in the account)");
            }

            sqLiteDatabase.execSQL("UPDATE " + DBConstant.ACCOUNT_TABLE_NAME + " SET "
                            + DBConstant.ACCOUNT_BALANCE + " = ? WHERE " +
                            DBConstant.ACCOUNT_ACCOUNT_NUM + " = ?",
                    new String[]{Double.toString(newAmount), accNum});
            sqLiteDatabase.endTransaction();
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
    }
}
