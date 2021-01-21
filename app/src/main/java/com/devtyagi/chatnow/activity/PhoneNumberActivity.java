package com.devtyagi.chatnow.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.devtyagi.chatnow.R;
import com.devtyagi.chatnow.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            Intent i = new Intent(PhoneNumberActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        getSupportActionBar().hide();
        binding.etPhone.requestFocus();

        binding.btnContinue.setOnClickListener(v -> {
            Intent i = new Intent(PhoneNumberActivity.this, OTPActivity.class);
            i.putExtra("phoneNumber", binding.etPhone.getText().toString());
            startActivity(i);
        });

    }
}