package com.app.grocerybudgetplanner.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.app.grocerybudgetplanner.R;
import com.app.grocerybudgetplanner.model.GroceryItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class admin_page extends AppCompatActivity {

    private EditText nameInput, priceInput, quantityInput;
    private TextView resultView;
    private Button addBtn, updateBtn, deleteBtn, resetBtn, closeBtn, viewBtn, logoutBtn;
    private String selectedItemId = null;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private List<GroceryItem> groceryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        nameInput = findViewById(R.id.nameInput);
        priceInput = findViewById(R.id.priceInput);
        quantityInput = findViewById(R.id.quantityInput);
        resultView = findViewById(R.id.resultView);
        addBtn = findViewById(R.id.addBtn);
        updateBtn = findViewById(R.id.updateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        resetBtn = findViewById(R.id.resetBtn);
        closeBtn = findViewById(R.id.closeBtn);
        viewBtn = findViewById(R.id.viewBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        addBtn.setOnClickListener(v -> addRecord());
        updateBtn.setOnClickListener(v -> updateRecord());
        deleteBtn.setOnClickListener(v -> deleteRecord());
        resetBtn.setOnClickListener(v -> resetFields());
        closeBtn.setOnClickListener(v -> finish());
        viewBtn.setOnClickListener(v -> viewAllRecords());
        logoutBtn.setOnClickListener(v -> showLogoutConfirmation());
    }
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> {
                auth.signOut();
                startActivity(new Intent(admin_page.this, login_page.class));
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }
    private void addRecord() {
        String name = nameInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        double price = Double.parseDouble(priceText);
        int quantity = Integer.parseInt(quantityText);
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", price);
        item.put("quantity", quantity);
        if (auth.getCurrentUser() != null) {
            item.put("userId", auth.getCurrentUser().getUid());
        }
        db.collection("grocery_items").add(item)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                    resetFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void updateRecord() {
        if (selectedItemId == null) {
            Toast.makeText(this, "Select item to update (View All → Tap item)", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = nameInput.getText().toString().trim();
        double price = Double.parseDouble(priceInput.getText().toString().trim());
        int quantity = Integer.parseInt(quantityInput.getText().toString().trim());
        GroceryItem updatedItem = new GroceryItem(selectedItemId, name, price, quantity);

        db.collection("grocery_items").document(selectedItemId).set(updatedItem).addOnSuccessListener(v -> {
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            resetFields();
        });
    }
    private void deleteRecord() {
        if (selectedItemId == null) {
            Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("grocery_items").document(selectedItemId).delete().addOnSuccessListener(v -> {
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            resetFields();
        });
    }
    @SuppressLint("SetTextI18n")
    private void resetFields() {
        nameInput.setText("");
        priceInput.setText("");
        quantityInput.setText("");
        resultView.setText("Total Cost:");
        selectedItemId = null;
    }
    private void viewAllRecords() {
        db.collection("grocery_items").get().addOnSuccessListener(querySnapshot -> {
            groceryItems.clear();
            List<String> displayItems = new ArrayList<>();
            Map<String, GroceryItem> itemMap = new HashMap<>();

            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                GroceryItem item = doc.toObject(GroceryItem.class);
                if (item != null) {
                    item.setId(doc.getId());
                    groceryItems.add(item);
                    String itemDetails = "Name: " + item.getName() + "\nPrice: " + item.getPrice() + "\nQty: " + item.getQuantity();
                    displayItems.add(itemDetails);
                    itemMap.put(itemDetails, item); // Store item details for easy retrieval
                }
            }
            if (displayItems.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("All Grocery Items")
                        .setMessage("No records found.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("All Grocery Items")
                    .setItems(displayItems.toArray(new String[0]), (dialog, which) -> {
                        String selectedDetails = displayItems.get(which);
                        GroceryItem selectedItem = itemMap.get(selectedDetails);
                        if (selectedItem != null) {
                            selectedItemId = selectedItem.getId();
                            nameInput.setText(selectedItem.getName());
                            priceInput.setText(String.valueOf(selectedItem.getPrice()));
                            quantityInput.setText(String.valueOf(selectedItem.getQuantity()));
                        }
                    })
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
}