package com.app.grocerybudgetplanner.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.app.grocerybudgetplanner.R;
import com.app.grocerybudgetplanner.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class registration_page extends AppCompatActivity {

    EditText username, email, password, adminCode;
    Button registerBtn;
    CheckBox isAdmin;
    TextView loginLink;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    private static final String SECURE_ADMIN_CODE = "20f20449albaraa";
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        adminCode = findViewById(R.id.admin_code);
        registerBtn = findViewById(R.id.register_btn);
        isAdmin = findViewById(R.id.checkbox_admin);
        loginLink = findViewById(R.id.login_link);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        isAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adminCode.setAlpha(0f);
                adminCode.setVisibility(View.VISIBLE);
                adminCode.animate().alpha(1f).setDuration(300).start();
            } else {
                adminCode.animate().alpha(0f).setDuration(300).withEndAction(() -> adminCode.setVisibility(View.GONE)).start();
            }
        });
        registerBtn.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String usernameText = username.getText().toString().trim();
            String userType;
            if (usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() ||
                    (isAdmin.isChecked() && adminCode.getText().toString().trim().isEmpty())) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }
            if (passwordText.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isAdmin.isChecked()) {
                String codeEntered = adminCode.getText().toString().trim();
                if (!codeEntered.equals(SECURE_ADMIN_CODE)) {
                    Toast.makeText(this, "Invalid admin code", Toast.LENGTH_SHORT).show();
                    return;
                }
                userType = "admin";
            } else {
                userType = "user";
            }
            auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = auth.getCurrentUser().getUid();
                    User newUser = new User(usernameText, emailText, userType);
                    firestore.collection("users").document(uid).set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, login_page.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, login_page.class));
            finish();
        });
    }
}
