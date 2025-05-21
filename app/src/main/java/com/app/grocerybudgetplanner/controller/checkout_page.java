package com.app.grocerybudgetplanner.controller;

import java.util.List;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.app.grocerybudgetplanner.R;
import com.app.grocerybudgetplanner.adapter.GroceryItemAdapter;
import com.app.grocerybudgetplanner.model.GroceryItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class checkout_page extends AppCompatActivity {
    private ListView itemsListView;
    private TextView totalTextView;
    private Button backButton;
    private GroceryItemAdapter adapter;
    private List<GroceryItem> items;
    private double total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout_page);
        itemsListView = findViewById(R.id.checkoutItemsListView);
        totalTextView = findViewById(R.id.checkoutTotalText);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });
        try {
            total = getIntent().getDoubleExtra("total", 0.0);
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("items")) {
                Serializable serializableItems = bundle.getSerializable("items");
                if (serializableItems instanceof ArrayList) {
                    items = (ArrayList<GroceryItem>) serializableItems;
                } else {
                    items = new ArrayList<>();
                    Toast.makeText(this, "Error loading items", Toast.LENGTH_SHORT).show();
                }
            } else {
                items = new ArrayList<>();
            }
        } catch (Exception e) {
            total = 0.0;
            items = new ArrayList<>();
            Toast.makeText(this, "Error loading checkout data", Toast.LENGTH_SHORT).show();
        }
        try {
            adapter = new GroceryItemAdapter(this, items);
            itemsListView.setAdapter(adapter);
            double calculatedTotal = 0.0;
            for (GroceryItem item : items) {
                calculatedTotal += item.getTotalCost();
            }
            if (Math.abs(calculatedTotal - total) > 0.001) {
                total = calculatedTotal;
            }
            totalTextView.setText(String.format(Locale.getDefault(), "Total: OMR %.3f", total));;
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying checkout items" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying checkout items" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}