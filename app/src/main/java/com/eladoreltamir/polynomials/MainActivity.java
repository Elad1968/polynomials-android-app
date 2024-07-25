package com.eladoreltamir.polynomials;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button insert;
    private Button browse;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        insert = findViewById(R.id.insert);
        browse = findViewById(R.id.list);
        search = findViewById(R.id.search);
        connect();
    }
    private void connect() {
        GUI.connectView(this, insert, (view) -> {
            Intent intent = new Intent(MainActivity.this, InsertActivity.class);
            startActivity(intent);
        });
        GUI.connectView(this, browse, (view) -> {
            Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
            startActivity(intent);
        });
        GUI.connectView(this, search, (view) -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }
}