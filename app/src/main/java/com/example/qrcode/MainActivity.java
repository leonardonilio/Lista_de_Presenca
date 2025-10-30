package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnAdmEvento, btnEnter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    btnAdmEvento = findViewById(R.id.btnAdmEvento);
    btnEnter = findViewById(R.id.btnEnter);

    }

    public void entrarAdmEvento(View view) {
        Intent it = new Intent(getApplicationContext(), AdminEventoMain.class);
        startActivity(it);

    }

    public void entrarEvento(View view) {
        Intent it = new Intent(getApplicationContext(), EventoMain.class);
        startActivity(it);
    }
}