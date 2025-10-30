package com.example.qrcode.dao;

import androidx.annotation.NonNull;

import com.example.qrcode.model.AdminEvento;
import com.example.qrcode.model.Ingressante;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IngressanteDAO {
    private DatabaseReference ingressanteRef;

    public IngressanteDAO() {
        // Referência para o nó "Eventos" no Realtime Database
        ingressanteRef = FirebaseDatabase.getInstance().getReference("ingressantes");
    }
    // ------------------------------
    // CREATE - Inserir novo evento
    // ------------------------------
    public void insert(Ingressante ingressante) {
        // Cria um ID único automático
        String keyIngressante = ingressanteRef.push().getKey();
        if (keyIngressante != null) {
            //evento.setID(key.hashCode()); // opcional: gerar ID numérico
            ingressanteRef.child(keyIngressante).setValue(ingressante);
        }
    }

    // ------------------------------
    // READ - Obter todos os eventos
    // ------------------------------
    public void obterTodos(ValueEventListener listener) {
        // Retorna todos os eventos
        ingressanteRef.addListenerForSingleValueEvent(listener);
    }

    // ------------------------------
    // READ - Obter evento específico
    // ------------------------------
    public void read(String keyIngressante, ValueEventListener listener) {
        ingressanteRef.child(keyIngressante).addListenerForSingleValueEvent(listener);
    }

    // ------------------------------
    // UPDATE - Atualizar evento
    // ------------------------------
    public void update(String keyIngressante, Ingressante ingressanteAtualizado) {
        ingressanteRef.child(keyIngressante).setValue(ingressanteAtualizado);
    }

    // ------------------------------
    // DELETE - Remover evento
    // ------------------------------
    public void delete(String keyIngressante) {
        ingressanteRef.child(keyIngressante).removeValue();
    }

    // ------------------------------
    // LISTA LOCAL (opcional)
    // Método de conveniência para
    // converter DataSnapshot em lista
    // ------------------------------
    public static List<Ingressante> snapshotToList(@NonNull DataSnapshot snapshot) {
        List<Ingressante> lista = new ArrayList<>();
        for (DataSnapshot ds : snapshot.getChildren()) {
            Ingressante e = ds.getValue(Ingressante.class);
            if (e != null) {
                lista.add(e);
            }
        }
        return lista;
    }
}

