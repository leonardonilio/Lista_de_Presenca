package com.example.qrcode.dao;

import androidx.annotation.NonNull;

import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminEventoDAO {

    private DatabaseReference eventosRef;

    public AdminEventoDAO() {
        // Referência para o nó "Eventos" no Realtime Database
        eventosRef = FirebaseDatabase.getInstance().getReference("eventos");
    }

    // ------------------------------
    // CREATE - Inserir novo evento
    // ------------------------------
    public void insert(AdminEvento evento) {
        // Cria um ID único automático
        String key = eventosRef.push().getKey();
        if (key != null) {
            //evento.setID(key.hashCode()); // opcional: gerar ID numérico
            eventosRef.child(key).setValue(evento);
        }
    }

    // ------------------------------
    // READ - Obter todos os eventos
    // ------------------------------
    public void obterTodos(ValueEventListener listener) {
        // Retorna todos os eventos
        eventosRef.addListenerForSingleValueEvent(listener);
    }

    // ------------------------------
    // READ - Obter evento específico
    // ------------------------------
    public void read(String key, ValueEventListener listener) {
        eventosRef.child(key).addListenerForSingleValueEvent(listener);
    }

    // ------------------------------
    // UPDATE - Atualizar evento
    // ------------------------------
    public void update(String key, AdminEvento eventoAtualizado) {
        eventosRef.child(key).setValue(eventoAtualizado);
    }

    // ------------------------------
    // DELETE - Remover evento
    // ------------------------------
    public void delete(String key) {
        eventosRef.child(key).removeValue();
    }

    // ------------------------------
    // LISTA LOCAL (opcional)
    // Método de conveniência para
    // converter DataSnapshot em lista
    // ------------------------------
    public static List<AdminEvento> snapshotToList(@NonNull DataSnapshot snapshot) {
        List<AdminEvento> lista = new ArrayList<>();
        for (DataSnapshot ds : snapshot.getChildren()) {
            AdminEvento e = ds.getValue(AdminEvento.class);
            if (e != null) {
                lista.add(e);
            }
        }
        return lista;
    }
}
