package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.qrcode.databinding.ActivityEventosFechadosBinding;
import com.example.qrcode.databinding.ActivityListaDePresencaBinding;
import com.example.qrcode.databinding.ActivityManutencaoEventoBinding;
import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventosFechados extends DrawerBaseActivity {

    private ListView listViewEventos;
    private List<AdminEvento> eventosList = new ArrayList<>();
    private List<String> keysList = new ArrayList<>();
    private DatabaseReference eventosEncerradosRef;
    private ActivityEventosFechadosBinding Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = ActivityEventosFechadosBinding.inflate(getLayoutInflater());
        setContentView(Binding.getRoot());
        allocateActivityTitle("Eventos Encerrados");

        listViewEventos = findViewById(R.id.listViewEventos);
        eventosEncerradosRef = FirebaseDatabase.getInstance().getReference("eventosEncerrados");

        // Buscar eventos encerrados no Firebase
        eventosEncerradosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventosList.clear();
                keysList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdminEvento evento = ds.getValue(AdminEvento.class);
                    if (evento != null) {
                        eventosList.add(evento);
                        keysList.add(ds.getKey());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EventosFechados.this,
                        android.R.layout.simple_list_item_1,
                        eventosList.stream().map(AdminEvento::getNomeEvento).toArray(String[]::new));
                listViewEventos.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Clique em um evento para ver presenças
        listViewEventos.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(EventosFechados.this, ListaDePresenca.class);
            intent.putExtra("Evento_key", keysList.get(position));
            startActivity(intent);
        });
    }
}
