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

    private final DatabaseReference eventosRef;

    public interface FirebaseCallback {
        void onSuccess(String key);
        void onFailure(Exception e);
    }

    public AdminEventoDAO() {
        eventosRef = FirebaseDatabase.getInstance().getReference("eventos");
    }

    // ------------------------------
    // CREATE - Inserir novo evento
    // ------------------------------
    public void insert(AdminEvento evento, FirebaseCallback callback) {
        String key = eventosRef.push().getKey();
        if (key == null) {
            if (callback != null)
                callback.onFailure(new Exception("Erro ao gerar chave do evento."));
            return;
        }

        evento.setKey(key);
        eventosRef.child(key).setValue(evento)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(key);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    // ------------------------------
    // READ - Obter todos os eventos
    // ------------------------------
    public void obterTodos(ValueEventListener listener) {
        eventosRef.addValueEventListener(listener);
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
    // Conversão snapshot → lista
    // ------------------------------
    public static List<AdminEvento> snapshotToList(@NonNull DataSnapshot snapshot) {
        List<AdminEvento> lista = new ArrayList<>();
        for (DataSnapshot ds : snapshot.getChildren()) {
            AdminEvento e = ds.getValue(AdminEvento.class);
            if (e != null) {
                e.setKey(ds.getKey());
                lista.add(e);
            }
        }
        return lista;
    }
}
