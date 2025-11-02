package com.example.qrcode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qrcode.databinding.ActivityDetalhesDoEventoBinding;
import com.example.qrcode.databinding.ActivityEventoMainBinding;

public class DetalhesDoEvento extends DrawerBaseActivity {
    ActivityDetalhesDoEventoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesDoEventoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}