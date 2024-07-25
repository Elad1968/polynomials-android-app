package com.eladoreltamir.polynomials;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.TreeMap;

public class BrowseActivity extends AppCompatActivity {
    private GuiDatabase database;
    private TextView empty;
    ListView list;
    private Button deleteAll;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            database = new GuiDatabase(this);
        } catch (Exception exception) {
            GUI.showError(this, exception.getMessage(), true);
        }
        empty = findViewById(R.id.empty);
        list = findViewById(R.id.list);
        deleteAll = findViewById(R.id.deleteAll);
        back = findViewById(R.id.back);
        refresh();
        connect();
    }
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
    private void refresh() {
        try {
            TreeMap<Integer, Polynomial> polynomials = database.read();
            if (polynomials.isEmpty()) {
                list.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                return;
            } else {
                list.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
            }
            ArrayList<Integer> keys = new ArrayList<>(polynomials.keySet());
            ArrayList<String> dataList = new ArrayList<>();
            for (Integer key: keys) {
                dataList.add(String.valueOf(key));
            }
            ArrayList<Polynomial> values = new ArrayList<>(polynomials.values());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_layout, R.id.major_text_view, dataList) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView majorTextView = view.findViewById(R.id.major_text_view);
                    majorTextView.setText(dataList.get(position));
                    majorTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    TextView subTextView = view.findViewById(R.id.sub_text_view);
                    subTextView.setText(values.get(position).toString());
                    subTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    return view;
                }
            };
            list.setAdapter(adapter);
            list.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(BrowseActivity.this, ViewActivity.class);
                intent.putExtra("id", keys.get(position));
                startActivity(intent);
            });
        } catch (Exception exception) {
            GUI.showError(this, exception.getMessage(), true);
        }
    }
    private void connect() {
        GUI.connectView(this, deleteAll, (view) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Are you sure you want to delete all the polynomials?");
            alert.setPositiveButton("Delete", (dialog, idx) -> {
                try {
                    database.clear();
                } catch (Exception exception) {
                    GUI.showError(this, exception.getMessage(), false);
                }
                refresh();
            });
            alert.setNegativeButton("Cancel", (dialog, idx) -> {

            });
            alert.show();
        });
        GUI.connectView(this, back, (view) -> finish());
    }
}