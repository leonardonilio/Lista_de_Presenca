package com.example.qrcode.dao;

import androidx.annotation.NonNull;

import com.example.qrcode.model.Presenca;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PresencaDAO {

    private final DatabaseReference presencasRef;
    private final DatabaseReference eventosRef;

    public PresencaDAO() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        presencasRef = db.getReference("presencas");
        eventosRef = db.getReference("eventos");
    }


     // Registra uma nova presença de um ingressante em um evento.

    public void registrarPresenca(String fkEvento, Presenca presenca) {
        // 1️⃣ Busca o horário de fim do evento no nó "eventos"
        eventosRef.child(fkEvento).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String horarioFim = null;
                if (snapshot.exists() && snapshot.child("horarioFim").getValue() != null) {
                    horarioFim = snapshot.child("horarioFim").getValue(String.class);
                }
                presenca.setHorarioSaida(horarioFim);

                // 2️⃣ Cria ou obtém o nó da presença com fk_evento
                DatabaseReference novaPresencaRef = presencasRef.push();
                String presencaKey = novaPresencaRef.getKey();

                if (presencaKey != null) {
                    novaPresencaRef.child("fk_evento").setValue(fkEvento);
                    novaPresencaRef.child("Presentes")
                            .child(presenca.getFk_KeyIngressante())
                            .setValue(presenca);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Erro ao buscar evento: " + error.getMessage());
            }
        });
    }

    /**
     * Lê todas as presenças cadastradas.
     */
    public void listarPresencas(ValueEventListener listener) {
        presencasRef.addListenerForSingleValueEvent(listener);
    }

    /**
     * Lê presenças de um evento específico.
     */
    public void listarPorEvento(String fkEvento, ValueEventListener listener) {
        presencasRef.orderByChild("fk_evento").equalTo(fkEvento)
                .addListenerForSingleValueEvent(listener);
    }

    /**
     * Atualiza o horário de saída de todos os ingressantes de um evento,
     * caso o evento mude o horário final.
     */
    public void atualizarHorarioSaidaAutomatico(String fkEvento) {
        eventosRef.child(fkEvento).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotEvento) {
                if (snapshotEvento.exists() && snapshotEvento.child("horarioFim").getValue() != null) {
                    String horarioFim = snapshotEvento.child("horarioFim").getValue(String.class);

                    presencasRef.orderByChild("fk_evento").equalTo(fkEvento)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotPresencas) {
                                    for (DataSnapshot presencaSnapshot : snapshotPresencas.getChildren()) {
                                        DataSnapshot presentesSnapshot = presencaSnapshot.child("Presentes");
                                        for (DataSnapshot ingressoSnapshot : presentesSnapshot.getChildren()) {
                                            ingressoSnapshot.getRef().child("horarioSaida").setValue(horarioFim);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
