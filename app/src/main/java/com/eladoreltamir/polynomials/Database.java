package com.eladoreltamir.polynomials;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.io.File;
import java.util.TreeMap;

public class Database {
    private static final String databaseFileName = "polynomials.db";
    private static final String polynomialsTable = "polynomials";
    private static final String idColumn = "id";
    private static final String coefficientColumn = "coefficient";
    private static final String powerColumn = "power";
    private static final String minimumTable = "minimum";
    private static final String minColumn = "min";
    private final String databasePath;

    public Database(File directory) {
        databasePath = directory + "/" + databaseFileName;
        create();
    }
    public void create() {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        database.execSQL("CREATE TABLE IF NOT EXISTS " + polynomialsTable + " (" + idColumn + " INTEGER, " + coefficientColumn + " DOUBLE, " + powerColumn + " INTEGER)");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + minimumTable + " (" + minColumn + " INTEGER)");
        try (Cursor cursor = database.rawQuery("SELECT * FROM " + minimumTable, null)) {
            if (cursor.getCount() == 0) {
                setMinID(1);
            }
        }
        database.close();
    }
    public TreeMap<Integer, Polynomial> read() {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
        try (Cursor cursor = database.rawQuery("SELECT * FROM " + polynomialsTable, null)) {
            TreeMap<Integer, Polynomial> polynomials = new TreeMap<>();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(idColumn);
                int coefficientIndex = cursor.getColumnIndex(coefficientColumn);
                int powerIndex = cursor.getColumnIndex(powerColumn);
                int id = cursor.getInt(idIndex);
                double coefficient = cursor.getDouble(coefficientIndex);
                int power = cursor.getInt(powerIndex);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    polynomials.putIfAbsent(id, new Polynomial());
                } else {
                    if (!polynomials.containsKey(id)) {
                        polynomials.put(id, new Polynomial());
                    }
                }
                Polynomial polynomial = polynomials.get(id);
                if (polynomial != null) {
                    polynomial.put(power, coefficient);
                }
            }
            database.close();
            return polynomials;
        }
    }
    public void clear() {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
        database.execSQL("DELETE FROM " + polynomialsTable);
        setMinID(1);
        database.close();
    }
    public void insert(Polynomial polynomial, int id) {
        checkID(id);
        if (id == 0) throw new NumberFormatException("ID cannot be zero.");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
        database.execSQL("DELETE FROM " + polynomialsTable + " WHERE id = " + id);
        if (polynomial.entrySet().isEmpty()) {
            String command = "INSERT INTO " + polynomialsTable + " (" + idColumn + "," + coefficientColumn + "," + powerColumn + ") VALUES (" + id + "," + 0 + "," + 0 + ")";
            database.execSQL(command);
        }
        for (TreeMap.Entry<Integer, Double> pair: polynomial.entrySet()) {
            String command = "INSERT INTO " + polynomialsTable + " (" + idColumn + "," + coefficientColumn + "," + powerColumn + ") VALUES (" + id + "," + pair.getValue() + "," + pair.getKey() + ")";
            database.execSQL(command);
        }
        if (id == getMinID()) {
            int i = id;
            while (get(++i) != null) {

            }
            setMinID(i);
        }
        database.close();
    }
    public void remove(int id) {
        checkID(id);
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
        database.execSQL("DELETE FROM " + polynomialsTable + " WHERE " + idColumn + " = " + id);
        if (id < getMinID()) {
            setMinID(id);
        }
        database.close();
    }
    public boolean exists(int id) {
        checkID(id);
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
        try (Cursor cursor = database.rawQuery("SELECT * FROM " + polynomialsTable + " WHERE " + idColumn + " = " + id, null)) {
            int count = cursor.getCount();
            database.close();
            return count != 0;
        }
    }
    public Polynomial get(int id) {
        checkID(id);
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
        try (Cursor cursor = database.rawQuery("SELECT * FROM " + polynomialsTable + " WHERE " + idColumn + " = " + id, null)) {
            if (cursor.getCount() == 0) return null;
            Polynomial polynomial = new Polynomial();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                double coefficient = cursor.getDouble(1);
                int power = cursor.getInt(2);
                polynomial.put(power, coefficient);
            }
            database.close();
            return polynomial;
        }
    }
    public int getMinID() {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
        try (Cursor cursor = database.rawQuery("SELECT * FROM " + minimumTable, null)) {
            cursor.moveToPosition(-1);
            cursor.moveToNext();
            int column = cursor.getColumnIndex(minColumn);
            int min = cursor.getInt(column);
            database.close();
            return min;
        }
    }
    private void checkID(int id) {
        if (id <= 0) throw new RuntimeException("ID must be a positive integer.");
    }
    private void setMinID(int id) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
        database.execSQL("DELETE FROM " + minimumTable);
        database.execSQL("INSERT INTO " + minimumTable + " (" + minColumn + ") VALUES (" + id + ")");
        database.close();
    }
}
