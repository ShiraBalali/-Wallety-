package com.example.wallety.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.wallety.R;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Objects.requireNonNull(getSupportActionBar()).hide();

    }
}