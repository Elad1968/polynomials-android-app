package com.eladoreltamir.polynomials;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class GUI {
    private static final char[] superscript = {'⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹'};
    public static Toast showError(Activity activity, String message, boolean quit) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
        toast.show();
        if (quit) {
            activity.finish();
        }
        return toast;
    }
    public static AlertDialog askToDeletePolynomial(Activity activity, Database database, int id, boolean finish) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage("Are you sure you want to delete polynomial #" + id + "?");
        alert.setPositiveButton("Yes", (dialog, idx) -> {
            try {
                database.remove(id);
                if (finish) {
                    activity.finish();
                }
            } catch (Exception exception) {
                GUI.showError(activity, exception.getMessage(), false);
            }
        });
        alert.setNegativeButton("No", (dialog, idx) -> {

        });
        return alert.show();
    }
    public static AlertDialog askToInsertPolynomial(Activity activity, Database database, int id, Polynomial polynomial, boolean go) {
        if (!database.exists(id)) {
            database.insert(polynomial, id);
            if (go) {
                askToGoTo(activity, id);
            }
            return null;
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setMessage("There is already a polynomial in slot " + id + ". Are you sure you want to replace it?");
            alert.setPositiveButton("Yes", (dialog, idx) -> {
                try {
                    database.insert(polynomial, id);
                } catch (Exception exception) {
                    GUI.showError(activity, exception.getMessage(), false);
                }
                if (go) {
                    askToGoTo(activity, id);
                }
            });
            alert.setNegativeButton("No", (dialog, idx) -> {

            });
            return alert.show();
        }
    }
    public static AlertDialog askToGoTo(Activity activity, int id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage("Would you like to go to the new Polynomial?");
        alert.setPositiveButton("Yes", (dialog, idx) -> {
            Intent intent = new Intent(activity, ViewActivity.class);
            intent.putExtra("id", id);
            activity.startActivity(intent);
        });
        alert.setNegativeButton("No", (dialog, idx) -> {

        });
        return alert.show();
    }
    public static int getId(EditText edit, Integer defaultID) {
        String text = edit.getText().toString();
        if (defaultID != null && text.isEmpty()) return defaultID;
        try {
            int id = Integer.parseInt(text);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException exception) {
            throw new NumberFormatException("ID must be a positive integer.");
        }
    }
    public static Polynomial getPolynomial(EditText edit, Polynomial defaultPolynomial) {
        String text = edit.getText().toString();
        if (defaultPolynomial != null && text.isEmpty()) return defaultPolynomial;
        try {
            return new Polynomial(text);
        } catch (Exception exception) {
            throw new NumberFormatException("Failed to create polynomial. Please check your input.");
        }
    }
    public static double getDouble(EditText edit, Double defaultDouble) {
        String text = edit.getText().toString();
        if (defaultDouble != null && text.isEmpty()) return defaultDouble;
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException exception) {
            throw new NumberFormatException(text + " is not a number.");
        }
    }
    public static void connectView(Activity activity, View view, View.OnClickListener listener) {
        view.setOnClickListener((event) -> {
            try {
                listener.onClick(view);
            } catch (Exception exception) {
                GUI.showError(activity, exception.getMessage(), false);
            }
        });
    }
    public static String doubleToString(double number) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DecimalFormat format = new DecimalFormat("0.######");
            return format.format(number);
        } else {
            return String.valueOf(number);
        }
    }
    public static String intToSuperscript(int power) {
        String powerString = String.valueOf(power);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < powerString.length(); ++i) {
            result.append(superscript[powerString.charAt(i) - '0']);
        }
        return result.toString();
    }
}
