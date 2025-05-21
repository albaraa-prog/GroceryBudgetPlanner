package com.app.grocerybudgetplanner.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.app.grocerybudgetplanner.R;
import com.app.grocerybudgetplanner.adapter.CartAdapter;
import com.app.grocerybudgetplanner.adapter.GroceryItemAdapter;
import com.app.grocerybudgetplanner.model.Budget;
import com.app.grocerybudgetplanner.model.Cart;
import com.app.grocerybudgetplanner.model.GroceryItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class home_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CartAdapter.OnCartItemListener {
    private Button menuButton;
    private TextView welcomeText, budgetStatusText, budgetAmountText, budgetRemainingText;
    private TextView headerEmail;
    private ProgressBar budgetProgressBar;
    private ListView recentItemsListView;
    private ListView cartListView;
    private TextView cartTotalText;
    private Button checkoutButton;
    private GroceryItemAdapter recentItemsAdapter;
    private CartAdapter cartAdapter;
    private List<GroceryItem> recentGroceryItems = new ArrayList<>();
    private Cart cart;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String userId;
    private String currentMonth;
    private int currentYear;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(home_page.this, login_page.class));
            finish();
            return;
        }
        userId = currentUser.getUid();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date now = new Date();
        currentMonth = monthFormat.format(now);
        currentYear = Integer.parseInt(yearFormat.format(now));
        menuButton = findViewById(R.id.menuButton);
        welcomeText = findViewById(R.id.welcomeText);
        budgetStatusText = findViewById(R.id.budgetStatusText);
        budgetAmountText = findViewById(R.id.budgetAmountText);
        budgetRemainingText = findViewById(R.id.budgetRemainingText);
        budgetProgressBar = findViewById(R.id.budgetProgressBar);
        recentItemsListView = findViewById(R.id.recentItemsListView);
        cartListView = findViewById(R.id.cartListView);
        cartTotalText = findViewById(R.id.cartTotalText);
        checkoutButton = findViewById(R.id.checkoutButton);
        cart = new Cart();
        recentItemsAdapter = new GroceryItemAdapter(this, recentGroceryItems);
        cartAdapter = new CartAdapter(this, cart.getItems(), this);
        recentItemsListView.setAdapter(recentItemsAdapter);
        cartListView.setAdapter(cartAdapter);
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(this);
        View headerView = navView.getHeaderView(0);
        headerEmail = headerView.findViewById(R.id.headerEmail);
        headerEmail.setText(currentUser.getEmail());
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        String email = currentUser.getEmail();
        welcomeText.setText("Welcome, " + email);
        loadCurrentBudget();
        loadRecentGroceryItems();
        updateCartTotal();
        setupItemClickListeners();
    }
    private void loadCurrentBudget() {
        if (auth == null) {
            Log.e("HomePage", "Auth is null, initializing it now");
            auth = FirebaseAuth.getInstance();
            if (auth == null || auth.getCurrentUser() == null) {
                Toast.makeText(this, "Failed to initialize authentication", Toast.LENGTH_SHORT).show();
                return;
            }
            userId = auth.getCurrentUser().getUid();
        }
        if (userId == null || userId.isEmpty()) {
            Log.e("HomePage", "User ID is null or empty, cannot load budget");
            Toast.makeText(this, "User not logged in properly", Toast.LENGTH_SHORT).show();
            return;
        }
        budgetProgressBar.setVisibility(View.VISIBLE);
        budgetProgressBar.setIndeterminate(true);
        String budgetId = userId + "_" + currentMonth + "_" + currentYear;
        firestore.collection("budgets").document(budgetId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    budgetProgressBar.setIndeterminate(false);
                    if (documentSnapshot.exists()) {
                        try {
                            Budget budget = documentSnapshot.toObject(Budget.class);
                            if (budget != null) {
                                Log.d("HomePage", "Found budget: " + budget.getAmount() + ", Spent: " + budget.getSpent());
                                updateBudgetUI(budget);
                            } else {
                                Log.w("HomePage", "Budget document exists but could not be converted to Budget object");
                                showNoBudgetUI();
                            }
                        } catch (Exception e) {
                            Log.e("HomePage", "Error processing budget data: " + e.getMessage(), e);
                            Toast.makeText(home_page.this, "Error loading budget data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("HomePage", "No budget found for " + currentMonth + "/" + currentYear);
                        showNoBudgetUI();
                    }
                })
                .addOnFailureListener(e -> {
                    budgetProgressBar.setIndeterminate(false);
                    Log.e("HomePage", "Error loading budget: " + e.getMessage());
                    Toast.makeText(home_page.this, "Error loading budget: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
    private void showNoBudgetUI() {
        budgetStatusText.setText("No budget set for " + currentMonth + " " + currentYear);
        budgetAmountText.setText("Budget: OMR 0.000");
        budgetRemainingText.setText("Remaining: OMR 0.000");
        budgetProgressBar.setProgress(0);
        budgetProgressBar.setVisibility(View.VISIBLE);
        budgetProgressBar.setIndeterminate(false);
        Toast.makeText(home_page.this, "No budget set for " + currentMonth + ". Please set a budget.", Toast.LENGTH_SHORT).show();
    }
    private void updateBudgetUI(Budget budget) {
        if (budget == null) return;
        double amount = budget.getAmount();
        double spent = budget.getSpent();
        double remaining = amount - spent;
        int percentUsed = (int)((spent / amount) * 100);
        if (percentUsed > 100) percentUsed = 100;
        budgetStatusText.setText("Budget for " + currentMonth + " " + currentYear);
        budgetAmountText.setText(String.format(Locale.getDefault(), "Budget: OMR %.3f", amount));
        budgetRemainingText.setText(String.format(Locale.getDefault(), "Remaining: OMR %.3f", remaining));
        budgetProgressBar.setVisibility(View.VISIBLE);
        budgetProgressBar.setMax(100);
        budgetProgressBar.setProgress(percentUsed);
        if (percentUsed > 90) {
            budgetRemainingText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (percentUsed > 75) {
            budgetRemainingText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            budgetRemainingText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }
    private void loadRecentGroceryItems() {
        if (firestore == null) {
            Toast.makeText(this, "Database connection error", Toast.LENGTH_SHORT).show();
            return;
        }
        firestore.collection("grocery_items")
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recentGroceryItems.clear();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(home_page.this, "No items found", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GroceryItem item = document.toObject(GroceryItem.class);
                                if (item != null) {
                                    item.setId(document.getId());
                                    recentGroceryItems.add(item);
                                }
                            }
                        }
                        recentItemsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(home_page.this, "Error loading items: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateCartTotal() {
        double total = cart.getTotalAmount();
        cartTotalText.setText(String.format(Locale.getDefault(), "Total: OMR %.3f", total));
    }
    @Override
    public void onQuantityChanged(GroceryItem item, int newQuantity) {
        cart.updateItemQuantity(item.getId(), newQuantity);
        cartAdapter.notifyDataSetChanged();
        updateCartTotal();
    }
    @Override
    public void onRemoveItem(GroceryItem item) {
        cart.removeItem(item.getId());
        cartAdapter.notifyDataSetChanged();
        updateCartTotal();
    }
    private void processCheckout() {
        if (cart.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        double total = cart.getTotalAmount();
        String budgetId = userId + "_" + currentMonth + "_" + currentYear;
        firestore.collection("budgets").document(budgetId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        if (!documentSnapshot.exists()) {
                            Toast.makeText(home_page.this,
                                    "No budget set for " + currentMonth + " " + currentYear + ". Please set a budget first.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        Budget budget = documentSnapshot.toObject(Budget.class);
                        if (budget == null) {
                            Toast.makeText(home_page.this, "Error: Budget data not found", Toast.LENGTH_LONG).show();
                            return;
                        }
                        double remaining = budget.getAmount() - budget.getSpent();
                        if (total > remaining) {
                            double shortfall = total - remaining;
                            Toast.makeText(home_page.this,
                                    String.format(Locale.getDefault(),
                                            "Insufficient budget. You need OMR %.3f more",
                                            shortfall),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            double newSpent = budget.getSpent() + total;
                            Log.d("Checkout", "Updating budget spent to: " + newSpent);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("spent", newSpent);
                            firestore.collection("budgets").document(budgetId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        savePurchaseHistory();
                                        ArrayList<GroceryItem> itemsList = new ArrayList<>(cart.getItems());
                                        Intent intent = new Intent(home_page.this, checkout_page.class);
                                        intent.putExtra("total", total);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("items", itemsList);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        Toast.makeText(home_page.this,
                                                String.format(Locale.getDefault(),
                                                        "Checkout successful. Remaining budget: OMR %.3f",
                                                        (budget.getAmount() - newSpent)),
                                                Toast.LENGTH_LONG).show();
                                        cart.clear();
                                        cartAdapter.notifyDataSetChanged();
                                        updateCartTotal();
                                        loadCurrentBudget();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(home_page.this,
                                                "Checkout failed: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    });
                        } catch (IllegalArgumentException e) {
                            Toast.makeText(home_page.this,
                                    "Error updating budget: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("Checkout", "Error processing checkout: " + e.getMessage(), e);
                        Toast.makeText(home_page.this,
                                "Error processing checkout: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Checkout", "Error fetching budget: " + e.getMessage());
                    Toast.makeText(home_page.this,
                            "Error processing checkout: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
    private void savePurchaseHistory() {
        if (firestore == null) return;
        for (GroceryItem item : cart.getItems()) {
            Map<String, Object> purchase = new HashMap<>();
            purchase.put("userId", userId);
            purchase.put("itemId", item.getId());
            purchase.put("itemName", item.getName());
            purchase.put("quantity", item.getQuantity());
            purchase.put("price", item.getPrice());
            purchase.put("totalCost", item.getTotalCost());
            purchase.put("purchaseDate", new Date());
            purchase.put("month", currentMonth);
            purchase.put("year", currentYear);
            firestore.collection("purchase_history")
                    .add(purchase)
                    .addOnFailureListener(e -> Log.e("PurchaseHistory", "Error saving purchase: " + e.getMessage()));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentBudget();
        loadRecentGroceryItems(); // Refresh recent items as well
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.nav_budget) {
            intent = new Intent(this, budget_page.class);
            startActivityForResult(intent, 1);
        } else if (item.getItemId() == R.id.nav_logout) {
            showLogoutConfirmation();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadCurrentBudget();
        }
    }
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    auth.signOut();
                    startActivity(new Intent(home_page.this, login_page.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void setupItemClickListeners() {
        recentItemsListView.setOnItemClickListener((parent, view, position, id) -> {
            GroceryItem selectedItem = recentItemsAdapter.getItem(position);
            if (selectedItem != null) {
                showAddToCartDialog(selectedItem);
            }
        });
        checkoutButton.setOnClickListener(v -> {
            if (cart.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            processCheckout();
        });
    }
    private void showAddToCartDialog(GroceryItem item) {
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_to_cart, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add " + item.getName() + " to Cart");
            builder.setView(dialogView);
            TextView quantityText = dialogView.findViewById(R.id.quantityText);
            Button decreaseButton = dialogView.findViewById(R.id.decreaseButton);
            Button increaseButton = dialogView.findViewById(R.id.increaseButton);
            final int[] quantity = {1};
            quantityText.setText(String.valueOf(quantity[0]));
            decreaseButton.setOnClickListener(v -> {
                if (quantity[0] > 1) {
                    quantity[0]--;
                    quantityText.setText(String.valueOf(quantity[0]));
                }
            });
            increaseButton.setOnClickListener(v -> {
                quantity[0]++;
                quantityText.setText(String.valueOf(quantity[0]));
            });
            builder.setPositiveButton("Add to Cart", (dialog, which) -> {
                GroceryItem cartItem = new GroceryItem(
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        quantity[0]
                );
                addItemToCart(cartItem);
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Error showing dialog", Toast.LENGTH_SHORT).show();
        }
    }
    private void addItemToCart(GroceryItem item) {
        try {
            cart.addItem(item);
            cartAdapter.notifyDataSetChanged();
            updateCartTotal();
            Toast.makeText(this, item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}