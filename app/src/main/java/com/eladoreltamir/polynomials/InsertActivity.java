package com.eladoreltamir.polynomials;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InsertActivity extends AppCompatActivity {
    private GuiDatabase database;
    private EditText askId;
    private EditText askPolynomial;
    private Button insert;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.insert_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = new GuiDatabase(this);
        askId = findViewById(R.id.askID);
        askPolynomial = findViewById(R.id.askPolynomial);
        insert = findViewById(R.id.insert);
        back = findViewById(R.id.back);
        connect();
    }
    private void connect() {
        GUI.connectView(this, insert, (view) -> {
            int id = GUI.getId(askId, database.getMinID());
            Polynomial polynomial = GUI.getPolynomial(askPolynomial, null);
            GUI.askToInsertPolynomial(this, database, id, polynomial, true);
        });
        GUI.connectView(this, back, (view) -> finish());
    }
}