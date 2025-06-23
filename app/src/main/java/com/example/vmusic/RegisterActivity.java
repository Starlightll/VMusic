package com.example.vmusic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Button loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(view -> {
            // Handle login button click
            // For example, navigate to the login screen or show a login dialog
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
//            finish(); // Close the register activity and return to the previous one
        });
    }
}