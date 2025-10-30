package com.example.qrcode;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qrcode.dao.PresencaDAO;
import com.example.qrcode.model.Presenca;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PresencaDoEvento extends AppCompatActivity {


    private String fkEvento, fkIngressante, nomeIngressante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_presenca_do_evento);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Recebe os dados
        fkEvento = getIntent().getStringExtra("Evento_key");
        fkIngressante = getIntent().getStringExtra("Ingressante_key");
        nomeIngressante = getIntent().getStringExtra("NomeIngressante");

        if (fkEvento == null || fkIngressante == null || nomeIngressante == null) {
            Toast.makeText(this, "Erro: dados incompletos.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void Salvar(View view) {
        String horarioEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());

        Presenca presenca = new Presenca();
        presenca.setFk_KeyIngressante(fkIngressante);
        presenca.setFk_Nome(nomeIngressante);
        presenca.setHorarioEntrada(horarioEntrada);
        presenca.setHorarioSaida(null);

        PresencaDAO dao = new PresencaDAO();
        dao.registrarPresenca(fkEvento, presenca);

        Toast.makeText(this, "Presença registrada!", Toast.LENGTH_LONG).show();
        finish();
    }

    public void sair(View view) {
        finish();
    }
}
