package com.app.grocerybudgetplanner.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.grocerybudgetplanner.R;
import com.app.grocerybudgetplanner.model.Budget;
import com.app.grocerybudgetplanner.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class budget_page extends AppCompatActivity {

    private Spinner monthSpinner;
    private EditText yearEditText, budgetEditText;
    private Button saveButton, cancelButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userDocRef;
    private String userId;
    @SuppressLint("RestrictedApi")
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_page);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        userDocRef = db.collection("users").document(userId);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearEditText = findViewById(R.id.yearEditText);
        budgetEditText = findViewById(R.id.budgetEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearEditText.setText(String.valueOf(currentYear));
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        monthSpinner.setSelection(currentMonth);
        saveButton.setOnClickListener(v -> saveBudget());
        cancelButton.setOnClickListener(v -> finish());
        loadUserData();
    }
    private void loadUserData() {
        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);

                        if (currentUser == null) {
                            Log.e("BudgetPage", "User data not found");
                            Toast.makeText(budget_page.this, "Error: User data not found", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        loadExistingBudget();
                    } else {
                        Log.e("BudgetPage", "User document does not exist");
                        Toast.makeText(budget_page.this, "Error: User data not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("BudgetPage", "Error loading user data: " + e.getMessage());
                    Toast.makeText(budget_page.this, "Error loading user data", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
    private void loadExistingBudget() {
        String selectedMonth = monthSpinner.getSelectedItem().toString();
        int selectedYear = Integer.parseInt(yearEditText.getText().toString());
        String budgetId = userId + "_" + selectedMonth + "_" + selectedYear;
        db.collection("budgets").document(budgetId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Budget budget = documentSnapshot.toObject(Budget.class);
                        if (budget != null) {
                            budgetEditText.setText(String.valueOf(budget.getAmount()));
                            Log.d("BudgetPage", "Loaded existing budget for " + selectedMonth + "/" + selectedYear);
                        }
                    } else {
                        Log.d("BudgetPage", "No existing budget found for " + selectedMonth + "/" + selectedYear);
                        budgetEditText.setText("");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("BudgetPage", "Error loading budget: " + e.getMessage());
                    Toast.makeText(budget_page.this, "Error loading budget data", Toast.LENGTH_SHORT).show();
                });
    }
    private void saveBudget() {
        String selectedMonth = monthSpinner.getSelectedItem().toString();
        String yearString = yearEditText.getText().toString().trim();
        String budgetString = budgetEditText.getText().toString().trim();
        if (yearString.isEmpty() || budgetString.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            final int selectedYear = Integer.parseInt(yearString);
            final double budgetAmount = Double.parseDouble(budgetString);
            if (budgetAmount <= 0) {
                Toast.makeText(this, "Budget amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
            String budgetId = userId + "_" + selectedMonth + "_" + selectedYear;
            DocumentReference budgetDocRef = db.collection("budgets").document(budgetId);
            budgetDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> budgetData = new HashMap<>();
                    budgetData.put("userId", userId);
                    budgetData.put("month", selectedMonth);
                    budgetData.put("year", selectedYear);
                    budgetData.put("amount", budgetAmount);
                    if (document.exists()) {
                        Double oldSpent = document.getDouble("spent");
                        if (oldSpent != null) {
                            budgetData.put("spent", oldSpent);
                        } else {
                            budgetData.put("spent", 0.0);
                        }
                    } else {
                        // Create new budget
                        budgetData.put("spent", 0.0);
                        Log.d("BudgetPage", "Creating new budget - Month: " + selectedMonth +
                                ", Year: " + selectedYear + ", Amount: " + budgetAmount);
                    }
                    budgetDocRef.set(budgetData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("BudgetPage", "Budget saved successfully");
                                Toast.makeText(budget_page.this, "Budget saved", Toast.LENGTH_SHORT).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("BudgetPage", "Failed to save budget: " + e.getMessage(), e);
                                Toast.makeText(budget_page.this, "Failed to save budget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e("BudgetPage", "Error checking for existing budget", task.getException());
                    Toast.makeText(budget_page.this, "Error checking for existing budget", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Log.e("BudgetPage", "Invalid number format: " + e.getMessage());
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("BudgetPage", "Error saving budget: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}