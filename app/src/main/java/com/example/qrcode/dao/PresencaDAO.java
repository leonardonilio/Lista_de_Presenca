package com.example.qrcode.dao;

import androidx.annotation.NonNull;

import com.example.qrcode.model.Presenca;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PresencaDAO {

    private final DatabaseReference eventosRef;

    public PresencaDAO() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        eventosRef = db.getReference("eventos");
    }

    /**
     * Registra a presença dentro do nó do evento, sem duplicar.
     */
    public void registrarPresenca(String fkEvento, Presenca presenca, Runnable onJaRegistrado, Runnable onRegistradoComSucesso, Runnable onErro) {
        DatabaseReference presencaRef = eventosRef
                .child(fkEvento)
                .child("Presenca")
                .child(presenca.getFk_KeyIngressante());

        //  Verifica se o usuário já está registrado no evento
        presencaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotExistente) {
                if (snapshotExistente.exists()) {
                    // Já existe presença registrada para esse ingressante
                    if (onJaRegistrado != null) onJaRegistrado.run();
                } else {
                    //  Busca o horário de fim do evento e salva
                    eventosRef.child(fkEvento).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotEvento) {
                            String horarioFim = null;
                            if (snapshotEvento.exists() && snapshotEvento.child("horarioFim").getValue() != null) {
                                horarioFim = snapshotEvento.child("horarioFim").getValue(String.class);
                            }
                            presenca.setHorarioSaida(horarioFim);

                            presencaRef.setValue(presenca)
                                    .addOnSuccessListener(unused -> {
                                        if (onRegistradoComSucesso != null) onRegistradoComSucesso.run();
                                    })
                                    .addOnFailureListener(e -> {
                                        System.err.println(" Erro ao registrar presença: " + e.getMessage());
                                        if (onErro != null) onErro.run();
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (onErro != null) onErro.run();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (onErro != null) onErro.run();
            }
        });
    }

    /**
     * Atualiza automaticamente o horário de saída de todos os ingressantes de um evento.
     */
    public void atualizarHorarioSaidaAutomatico(String fkEvento) {
        eventosRef.child(fkEvento).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotEvento) {
                if (snapshotEvento.exists() && snapshotEvento.child("horarioFim").getValue() != null) {
                    String horarioFim = snapshotEvento.child("horarioFim").getValue(String.class);

                    DatabaseReference presencasRef = eventosRef.child(fkEvento).child("Presenca");

                    presencasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotPresencas) {
                            for (DataSnapshot presencaSnapshot : snapshotPresencas.getChildren()) {
                                presencaSnapshot.getRef().child("horarioSaida").setValue(horarioFim);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    public void deletePresenca(String fkEvento, String fkKeyIngressante,
                               Runnable onSuccess, Runnable onFailure) {

        DatabaseReference presencaRef = eventosRef
                .child(fkEvento)
                .child("Presenca")
                .child(fkKeyIngressante);

        presencaRef.removeValue()
                .addOnSuccessListener(unused -> {
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    System.err.println("Erro ao deletar presença: " + e.getMessage());
                    if (onFailure != null) onFailure.run();
                });
    }
}
