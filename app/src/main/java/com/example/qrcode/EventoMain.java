package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.qrcode.databinding.ActivityEventoMainBinding;
import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class EventoMain extends DrawerBaseActivity {


    ActivityEventoMainBinding binding;
    SearchView mysearchview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_evento_main);
        binding = ActivityEventoMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       allocateActivityTitle("Lista de Cadastro");
        mysearchview = (SearchView) findViewById(R.id.searchview);


    }
public void LerQrcode (View view){
    IntentIntegrator integrator = new IntentIntegrator(this);
    integrator.setPrompt("Aponte para o QR Code");
    integrator.setOrientationLocked(false);
    integrator.setBeepEnabled(true);
    integrator.initiateScan();
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                String valorLido = result.getContents().trim();
                buscarEventoPorKey(valorLido);
            } else {
                Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show();
            }
            // encerra essa tela após ler
        }
    }
    private void buscarEventoPorKey(String eventoKey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("eventos");

        ref.child(eventoKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                AdminEvento evento = task.getResult().getValue(AdminEvento.class);
                if (evento != null) {
                    evento.setKey(eventoKey);

                    Toast.makeText(this, "QR reconhecido! Evento encontrado.", Toast.LENGTH_SHORT).show();


                }
            } else {
                Toast.makeText(this, "QR não reconhecido (evento não encontrado no banco).", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro ao acessar o Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }


}