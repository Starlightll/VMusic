package com.example.vmusic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button signupButton = findViewById(R.id.signup_btn);
        signupButton.setOnClickListener(view -> {
            // Handle signup button click
            // For example, navigate to the signup screen or show a signup dialog
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}