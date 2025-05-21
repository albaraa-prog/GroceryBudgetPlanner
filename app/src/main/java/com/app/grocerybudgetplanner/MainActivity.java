package com.app.grocerybudgetplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.app.grocerybudgetplanner.controller.admin_page;
import com.app.grocerybudgetplanner.controller.home_page;
import com.app.grocerybudgetplanner.controller.login_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private static final String USERS_COLLECTION = "users";
    private static final String ROLE_FIELD = "role";
    private static final String ADMIN_ROLE = "admin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        initializeFirebase();
        checkUserAuthentication();
    }
    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }
    private void checkUserAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        } else {
            checkUserRole(currentUser.getUid());
        }
    }
    private void checkUserRole(String uid) {
        firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener(this::handleUserData)
                .addOnFailureListener(this::handleError);
    }
    private void handleUserData(DocumentSnapshot documentSnapshot) {
        if (!documentSnapshot.exists()) {
            handleUserNotFound();
            return;
        }
        String role = documentSnapshot.getString(ROLE_FIELD);
        if (role == null) {
            handleInvalidRole();
            return;
        }
        navigateBasedOnRole(role);
    }
    private void handleUserNotFound() {
        showToast("User data not found");
        signOutAndNavigateToLogin();
    }
    private void handleInvalidRole() {
        showToast("User role not found");
        signOutAndNavigateToLogin();
    }
    private void handleError(Exception e) {
        showToast("Error fetching user data: " + e.getMessage());
        finish();
    }
    private void navigateBasedOnRole(String role) {
        Intent intent;
        if (role.equalsIgnoreCase(ADMIN_ROLE)) {
            intent = new Intent(this, admin_page.class);
        } else {
            intent = new Intent(this, home_page.class);
        }
        startActivity(intent);
        finish();
    }
    private void navigateToLogin() {
        startActivity(new Intent(this, login_page.class));
        finish();
    }
    private void signOutAndNavigateToLogin() {
        mAuth.signOut();
        navigateToLogin();
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}