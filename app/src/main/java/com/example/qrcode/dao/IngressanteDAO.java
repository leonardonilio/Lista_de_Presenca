package com.example.qrcode.dao;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qrcode.model.AdminEvento;
import com.example.qrcode.model.Ingressante;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    public String insert(Ingressante ingressante) {
        String keyIngressante = ingressanteRef.push().getKey();
        if (keyIngressante != null) {
            ingressante.setKeyIngressante(keyIngressante); // define a key ANTES
            ingressanteRef.child(keyIngressante).setValue(ingressante); // e agora salva já com ela
        }
        return keyIngressante;
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
    public void login(String nome, String email, String telefone, LoginCallback callback) {
        ingressanteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Ingressante i = ds.getValue(Ingressante.class);

                    if (i != null &&
                            i.getNomeIngressante().equalsIgnoreCase(nome.trim()) &&
                            i.getEmailIngressante().equalsIgnoreCase(email.trim()) &&
                            i.getTelefoneIngressante().equals(telefone.trim())) {

                        // Se o campo key não existir no Firebase, usamos a key real do nó:
                        String keyReal = i.getKeyIngressante() != null
                                ? i.getKeyIngressante()
                                : ds.getKey();

                        callback.onLoginSuccess(keyReal, i.getNomeIngressante());
                        return;
                    }
                }
                callback.onLoginFailed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onLoginError(error.getMessage());
            }
        });
    }


    // Interface para retornar o resultado do login
    public interface LoginCallback {
        void onLoginSuccess(String keyIngressante, String nomeIngressante);
        void onLoginFailed();
        void onLoginError(String error);
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

