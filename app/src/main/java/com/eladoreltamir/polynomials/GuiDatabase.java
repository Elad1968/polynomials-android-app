package com.eladoreltamir.polynomials;

import android.app.Activity;

import java.util.TreeMap;

public class GuiDatabase extends Database {
    private final Activity activity;

    public GuiDatabase(Activity activity) {
        super((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) ? activity.getDataDir() : activity.getFilesDir());
        this.activity = activity;
    }
    @Override
    public void create() {
        try {
            super.create();
        } catch (Exception exception) {
            GUI.showError(activity, "Failed to create the database.", true);
        }
    }
    @Override
    public TreeMap<Integer, Polynomial> read() {
        try {
            return super.read();
        } catch (Exception exception) {
            GUI.showError(activity, "Failed to read the database.", false);
            return new TreeMap<>();
        }
    }
    @Override
    public void clear() {
        try {
            super.clear();
        } catch (Exception exception) {
            GUI.showError(activity, "Failed to clear the database.", false);
        }
    }
    @Override
    public void insert(Polynomial polynomial, int id) {
        try {
            super.insert(polynomial, id);
        } catch (Exception exception) {
            GUI.showError(activity, "Failed to insert the polynomial to the database.", false);
        }
    }
    @Override
    public void remove(int id) {
        try {
            super.remove(id);
        } catch (Exception exception) {
            GUI.showError(activity, "Failed to remove the polynomial from the database.", false);
        }
    }
    @Override
    public boolean exists(int id) {
        try {
            return super.exists(id);
        } catch (Exception exception) {
            GUI.showError(activity, "Failed to read the database.", false);
            return false;
        }
    }
    @Override
    public Polynomial get(int id) {
        try {
            return super.get(id);
        } catch (Exception exception) {
            GUI.showError(activity, "Failed to get the polynomial from the database.", false);
            return null;
        }
    }
    @Override
    public int getMinID() {
        try {
            return super.getMinID();
        } catch (Exception exception) {
            GUI.showError(activity, "Failed to read the database.", false);
            return 0;
        }
    }
}
