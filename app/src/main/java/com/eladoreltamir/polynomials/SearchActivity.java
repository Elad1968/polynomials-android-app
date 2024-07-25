package com.eladoreltamir.polynomials;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SearchActivity extends AppCompatActivity {
    private EditText askId;
    private Button search;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        askId = findViewById(R.id.idSearchPicker);
        search = findViewById(R.id.searcher);
        back = findViewById(R.id.back);
        connect();
    }
    private void connect() {
        GUI.connectView(this, search, (view) -> {
            int id = GUI.getId(askId, null);
            Intent intent = new Intent(SearchActivity.this, ViewActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });
        GUI.connectView(this, back, (view) -> finish());
    }
}