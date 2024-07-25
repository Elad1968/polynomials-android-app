package com.eladoreltamir.polynomials;

import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class ViewActivity extends AppCompatActivity {
    private int id;
    private GuiDatabase database;
    LineChart graph;
    private Button update;
    private Button copy;
    private Button derivative;
    private Button integral;
    private Button calculate;
    private Button add;
    private Button subtract;
    private Button multiply;
    private Button delete;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            database = new GuiDatabase(this);
        } catch (Exception exception) {
            GUI.showError(this, exception.getMessage(), true);
        }
        graph = findViewById(R.id.chart);
        update = findViewById(R.id.update);
        copy = findViewById(R.id.copy);
        derivative = findViewById(R.id.derivative);
        integral = findViewById(R.id.integral);
        calculate = findViewById(R.id.calculate);
        add = findViewById(R.id.add);
        subtract = findViewById(R.id.subtract);
        multiply = findViewById(R.id.multiply);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back);
        refresh(getIntent().getIntExtra("id", 0));
        connect();
    }
    private void updateTableLayout(Polynomial polynomial) {
        TableLayout table = findViewById(R.id.table);
        TableRow header = findViewById(R.id.tableHeader);
        table.removeAllViews();
        table.addView(header);
        if (polynomial.entrySet().isEmpty()) {
            addRowToTable(table, header, 0, 0.0);
        }
        for (TreeMap.Entry<Integer, Double> pair: polynomial.entrySet()) {
            addRowToTable(table, header, pair.getKey(), pair.getValue());
        }
    }
    private void addRowToTable(TableLayout table, TableRow tableHeader, Integer key, Double value) {
        TableRow row = new TableRow(this);
        TextView powerCell = new TextView(this);
        powerCell.setText(String.format(Locale.getDefault(), "%d", key));
        powerCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        TextView coefficientCell = new TextView(this);
        coefficientCell.setText(String.format(Locale.getDefault(), "%f", value));
        coefficientCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        row.addView(powerCell, tableHeader.getChildAt(0).getLayoutParams());
        row.addView(coefficientCell, tableHeader.getChildAt(1).getLayoutParams());
        table.addView(row, tableHeader.getLayoutParams());
    }
    private void refresh(int newID) {
        getIntent().putExtra("id", newID);
        id = newID;
        try {
            Polynomial polynomial = database.get(id);
            TextView header = findViewById(R.id.view_header);
            header.setText(getString(R.string.ViewActivity_header, id));
            TextView function = findViewById(R.id.view_function);
            function.setText(polynomial.toString());
            updateTableLayout(polynomial);
        } catch (Exception exception) {
            GUI.showError(this, exception.getMessage(), true);
        }
        displayGraph();
    }
    private void connect() {
        GUI.connectView(this, update, (view) -> {
            EditText getNewPolynomial = new EditText(this);
            getNewPolynomial.setHint("Polynomial");
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Update the polynomial:");
            alert.setView(getNewPolynomial);
            alert.setPositiveButton("Update", (dialog, idx) -> {
                try {
                    Polynomial polynomial = GUI.getPolynomial(getNewPolynomial, null);
                    AlertDialog dialog1 = GUI.askToInsertPolynomial(this, database, id, polynomial, false);
                    if (dialog1 != null) {
                        dialog1.setOnDismissListener((event2) -> refresh(id));
                    }
                } catch (Exception exception) {
                    GUI.showError(this, exception.getMessage(), false);
                }
            });
            alert.setNegativeButton("Cancel", (dialog, idx) -> {

            });
            alert.show();
        });
        GUI.connectView(this, copy, (view) -> {
            Polynomial polynomial = database.get(id);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            EditText editID = new EditText(this);
            editID.setInputType(InputType.TYPE_CLASS_NUMBER);
            editID.setHint("ID (Leave blank for auto id.)");
            alert.setView(editID);
            alert.setMessage("Copy the polynomial to:");
            alert.setPositiveButton("Copy", (dialog, idx) -> {
                int newID = GUI.getId(editID, database.getMinID());
                AlertDialog dialog1 = GUI.askToInsertPolynomial(this, database, newID, polynomial, newID != id);
                if (dialog1 != null) {
                    dialog1.setOnDismissListener((event2) -> {
                        if (id == newID) {
                            refresh(newID);
                        }
                    });
                }
            });
            alert.setNegativeButton("Cancel", (dialog, idx) -> {

            });
            alert.show();
        });
        GUI.connectView(this, derivative, (view) -> {
            Polynomial polynomial = database.get(id);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            EditText editID = new EditText(this);
            editID.setInputType(InputType.TYPE_CLASS_NUMBER);
            editID.setHint("ID (Leave blank for current id.)");
            alert.setView(editID);
            alert.setMessage("Get the derivative:");
            alert.setPositiveButton("Add", (dialog, idx) -> {
                int newID = GUI.getId(editID, id);
                Polynomial newPolynomial = polynomial.derivative();
                AlertDialog dialog1 = GUI.askToInsertPolynomial(this, database, newID, newPolynomial, newID != id);
                if (dialog1 != null) {
                    dialog1.setOnDismissListener((event2) -> {
                        if (id == newID) {
                            refresh(newID);
                        }
                    });
                }
            });
            alert.setNegativeButton("Cancel", (dialog, idx) -> {

            });
            alert.show();
        });
        GUI.connectView(this, integral, (view) -> {
            Polynomial polynomial = database.get(id);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            EditText editID = new EditText(this);
            editID.setInputType(InputType.TYPE_CLASS_NUMBER);
            editID.setHint("ID (Leave empty for the current id.)");
            EditText editConstant = new EditText(this);
            editConstant.setInputType(InputType.TYPE_CLASS_TEXT);
            editConstant.setHint("Constant (Leave empty for zero.)");
            layout.addView(editID);
            layout.addView(editConstant);
            alert.setView(layout);
            alert.setMessage("Get the integral:");
            alert.setPositiveButton("Add", (dialog, idx) -> {
                try {
                    int newID = GUI.getId(editID, id);
                    double constant = GUI.getDouble(editConstant, 0.0);
                    Polynomial newPolynomial = polynomial.integral(constant);
                    AlertDialog dialog1 = GUI.askToInsertPolynomial(this, database, newID, newPolynomial, newID != id);
                    if (dialog1 != null) {
                        dialog1.setOnDismissListener((event2) -> {
                            if (id == newID) {
                                refresh(newID);
                            }
                        });
                    }
                } catch (Exception exception) {
                    GUI.showError(this, exception.getMessage(), false);
                }
            });
            alert.setNegativeButton("Cancel", (dialog, idx) -> {

            });
            alert.show();
        });
        GUI.connectView(this, calculate, (view) -> {
            Polynomial polynomial = database.get(id);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            EditText getX = new EditText(this);
            getX.setHint("X to calculate");
            alert.setView(getX);
            alert.setMessage("Get f(x):");
            alert.setPositiveButton("Calculate", (dialog, idx) -> {
                AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
                try {
                    alert2.setMessage(String.valueOf(polynomial.calculate(GUI.getDouble(getX, null))));
                } catch (NumberFormatException exception) {
                    GUI.showError(this, exception.getMessage(), false);
                    return;
                }
                alert2.setNeutralButton("OK", (event3, idx3) -> {

                });
                alert2.show();
            });
            alert.setNegativeButton("Cancel", ((dialog, idx) -> {

            }));
            alert.show();
        });
        GUI.connectView(this, add, (view) -> {
            Polynomial polynomial = database.get(id);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            EditText editID = new EditText(this);
            editID.setInputType(InputType.TYPE_CLASS_NUMBER);
            editID.setHint("ID (Leave empty for the current id.)");
            EditText editPolynomial = new EditText(this);
            editPolynomial.setInputType(InputType.TYPE_CLASS_TEXT);
            editPolynomial.setHint("Polynomial");
            layout.addView(editID);
            layout.addView(editPolynomial);
            alert.setView(layout);
            alert.setMessage("Add to the polynomial:");
            alert.setPositiveButton("Add", (dialog, idx) -> {
                try {
                    int newID = GUI.getId(editID, id);
                    Polynomial secondPolynomial = GUI.getPolynomial(editPolynomial, null);
                    Polynomial newPolynomial = polynomial.add(secondPolynomial);
                    AlertDialog alert2 = GUI.askToInsertPolynomial(this, database, newID, newPolynomial, newID != id);
                    if (alert2 != null) {
                        alert2.setOnDismissListener((event2) -> {
                            if (id == newID) {
                                refresh(newID);
                            }
                        });
                    }
                } catch (Exception exception) {
                    GUI.showError(this, exception.getMessage(), false);
                }
            });
            alert.setNegativeButton("Cancel", (dialog, idx) -> {

            });
            alert.show();
        });
        GUI.connectView(this, subtract, (view) -> {
            Polynomial polynomial = database.get(id);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            EditText editID = new EditText(this);
            editID.setInputType(InputType.TYPE_CLASS_NUMBER);
            editID.setHint("ID (Leave empty for the current id.)");
            EditText editPolynomial = new EditText(this);
            editPolynomial.setInputType(InputType.TYPE_CLASS_TEXT);
            editPolynomial.setHint("Polynomial");
            layout.addView(editID);
            layout.addView(editPolynomial);
            alert.setView(layout);
            alert.setMessage("Subtract from the polynomial:");
            alert.setPositiveButton("Add", (dialog, idx) -> {
                try {
                    int newID = GUI.getId(editID, id);
                    Polynomial secondPolynomial = GUI.getPolynomial(editPolynomial, null);
                    Polynomial newPolynomial = polynomial.subtract(secondPolynomial);
                    AlertDialog alert2 = GUI.askToInsertPolynomial(this, database, newID, newPolynomial, newID != id);
                    if (alert2 != null) {
                        alert2.setOnDismissListener((event2) -> {
                            if (id == newID) {
                                refresh(newID);
                            }
                        });
                    }
                } catch (Exception exception) {
                    GUI.showError(this, exception.getMessage(), false);
                }
            });
            alert.setNegativeButton("Cancel", (dialog, idx) -> {

            });
            alert.show();
        });
        GUI.connectView(this, multiply, (view) -> {
            Polynomial polynomial = database.get(id);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            EditText editID = new EditText(this);
            editID.setInputType(InputType.TYPE_CLASS_NUMBER);
            editID.setHint("ID (Leave empty for the current id.)");
            EditText editPolynomial = new EditText(this);
            editPolynomial.setInputType(InputType.TYPE_CLASS_TEXT);
            editPolynomial.setHint("Polynomial");
            layout.addView(editID);
            layout.addView(editPolynomial);
            alert.setView(layout);
            alert.setMessage("Multiply the polynomial by:");
            alert.setPositiveButton("Add", (dialog, idx) -> {
                try {
                    int newID = GUI.getId(editID, id);
                    Polynomial secondPolynomial = GUI.getPolynomial(editPolynomial, null);
                    Polynomial newPolynomial = polynomial.multiply(secondPolynomial);
                    AlertDialog alert2 = GUI.askToInsertPolynomial(this, database, newID, newPolynomial, newID != id);
                    if (alert2 != null) {
                        alert2.setOnDismissListener((event2) -> {
                            if (id == newID) {
                                refresh(newID);
                            }
                        });
                    }
                } catch (Exception exception) {
                    GUI.showError(this, exception.getMessage(), false);
                }
            });
            alert.setNegativeButton("Cancel", (dialog, idx) -> {

            });
            alert.show();
        });
        GUI.connectView(this, delete, (view) -> GUI.askToDeletePolynomial(this, database, id, true));
        GUI.connectView(this, back, (view) -> finish());
    }
    private void displayGraph() {
        Polynomial polynomial = database.get(id);
        double min = -10;
        double max = 10;
        int amount = 1000;
        double distance = (Math.abs(min) + Math.abs(max)) / amount;
        List<Entry> entries = new ArrayList<>(amount);
        for (double x = min; x <= max; x += distance) {
            entries.add(new Entry((float) x, (float)polynomial.calculate(x)));
        }
        LineDataSet dataSet = new LineDataSet(entries, polynomial.toString());
        dataSet.setColor(Color.GREEN);
        dataSet.setLineWidth(4);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        graph.setData(lineData);
        XAxis x = graph.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1);
        x.setLabelCount(2);
        YAxis y = graph.getAxisLeft();
        y.setGranularity(1f);
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
        x.setTextColor(array.getColor(0, Color.BLACK));
        y.setTextColor(array.getColor(0, Color.BLACK));
        graph.getDescription().setTextColor(array.getColor(0, Color.BLACK));
        array.close();
        graph.getAxisRight().setEnabled(false);
        graph.getDescription().setEnabled(true);
        graph.getDescription().setText(polynomial.toString());
        graph.getDescription().setTextSize(12f);
        graph.getLegend().setEnabled(false);
        graph.getLayoutParams().height = getResources().getDisplayMetrics().widthPixels;
        graph.invalidate();
    }
}