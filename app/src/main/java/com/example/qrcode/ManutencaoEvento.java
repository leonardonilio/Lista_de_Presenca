package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qrcode.dao.AdminEventoDAO;
import com.example.qrcode.dao.PresencaDAO;
import com.example.qrcode.databinding.ActivityManutencaoEventoBinding;
import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ManutencaoEvento extends DrawerBaseActivity {

    private Button btnAlterarEvento, btnExcluirManutencao;
    private EditText edtNomeEventoManutencao, edtLocalManutencao, edtDataManutencao,
            edtOrganizadorManutencao, edtHorarioInicioManutencao, edtHorarioFimManutencao,
            edtDescricaoManutencao;

    private ActivityManutencaoEventoBinding activityManutencaoEventoBinding;
    private DatabaseReference eventosRef;
    private String eventoId; // Firebase key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManutencaoEventoBinding = ActivityManutencaoEventoBinding.inflate(getLayoutInflater());
        setContentView(activityManutencaoEventoBinding.getRoot());
        allocateActivityTitle("Manutenção");

        // Inicializa referência do Firebase
        eventosRef = FirebaseDatabase.getInstance().getReference("Eventos");

        // Liga componentes
        btnAlterarEvento = findViewById(R.id.btnAlterarEvento);
        btnExcluirManutencao = findViewById(R.id.btnExcluirManutencao);


        edtNomeEventoManutencao = findViewById(R.id.edtNomeEventoManutencao);
        edtLocalManutencao = findViewById(R.id.edtLocalManutencao);
        edtDataManutencao = findViewById(R.id.edtDataManutencao);
        edtOrganizadorManutencao = findViewById(R.id.edtOrganizadorManutencao);
        edtHorarioInicioManutencao = findViewById(R.id.edtHorarioInicioManutencao);
        edtHorarioFimManutencao = findViewById(R.id.edtHorarioFimManutencao);
        edtDescricaoManutencao = findViewById(R.id.edtDescricaoManutencao);


        // Pega os dados vindos da Intent
        eventoId = getIntent().getStringExtra("adminEvento_key");

        edtNomeEventoManutencao.setText(getIntent().getStringExtra("adminEvento_Nome"));
        edtLocalManutencao.setText(getIntent().getStringExtra("adminEvento_Local"));
        edtDataManutencao.setText(getIntent().getStringExtra("adminEvento_Data"));
        edtOrganizadorManutencao.setText(getIntent().getStringExtra("adminEvento_Organizador"));
        edtHorarioInicioManutencao.setText(getIntent().getStringExtra("adminEvento_HorarioInicio"));
        edtHorarioFimManutencao.setText(getIntent().getStringExtra("adminEvento_HorarioFim"));
        edtDescricaoManutencao.setText(getIntent().getStringExtra("adminEvento_Descricao"));
    }

    public void alterar(View view) {
        if (eventoId == null || eventoId.isEmpty()) {
            Toast.makeText(this, "ID do evento inválido!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference eventoRef = FirebaseDatabase.getInstance().getReference("eventos").child(eventoId);

        // Primeiro, buscar o evento atual para pegar o horário antigo
        eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(ManutencaoEvento.this, "Evento não encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String horarioAntigo = snapshot.child("horarioFim").getValue(String.class);
                String horarioNovo = edtHorarioFimManutencao.getText().toString();

                // Atualiza o objeto com os novos valores
                AdminEvento adminEvento = new AdminEvento();
                adminEvento.setNomeEvento(edtNomeEventoManutencao.getText().toString());
                adminEvento.setLocal(edtLocalManutencao.getText().toString());
                adminEvento.setData(edtDataManutencao.getText().toString());
                adminEvento.setOrganizador(edtOrganizadorManutencao.getText().toString());
                adminEvento.setHorarioInicio(edtHorarioInicioManutencao.getText().toString());
                adminEvento.setHorarioFim(horarioNovo);
                adminEvento.setDescricao(edtDescricaoManutencao.getText().toString());

                // Atualiza o evento no Firebase
                Map<String, Object> updates = new HashMap<>();
                updates.put("nomeEvento", adminEvento.getNomeEvento());
                updates.put("local", adminEvento.getLocal());
                updates.put("data", adminEvento.getData());
                updates.put("organizador", adminEvento.getOrganizador());
                updates.put("horarioInicio", adminEvento.getHorarioInicio());
                updates.put("horarioFim", adminEvento.getHorarioFim());
                updates.put("descricao", adminEvento.getDescricao());

                eventoRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ManutencaoEvento.this, "Evento alterado com sucesso!", Toast.LENGTH_SHORT).show();

                            // Chama o ajuste automático dos horários de saída, se necessário
                            if (horarioAntigo != null && !horarioAntigo.equals(horarioNovo)) {
                                PresencaDAO presencaDAO = new PresencaDAO();
                                presencaDAO.atualizarHorarioSaidaAutomatico(eventoId, horarioAntigo, horarioNovo);
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(ManutencaoEvento.this, "Erro ao alterar: " + e.getMessage(), Toast.LENGTH_LONG).show());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManutencaoEvento.this, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void excluir(View view) {
        if (eventoId == null || eventoId.isEmpty()) {
            Toast.makeText(this, "Nenhum evento selecionado para excluir.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            AdminEventoDAO dao = new AdminEventoDAO();
            dao.delete(eventoId);
            Toast.makeText(getApplicationContext(), "Evento excluído com sucesso!", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

            }

    public void gerarQRCode(View view) {
        if (eventoId.isEmpty()) {
            Toast.makeText(this, "Selecione um evento antes de gerar o QR Code.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GerarQrCode.class);
        intent.putExtra("Evento_key", eventoId); // usamos o ID do Firebase
        startActivity(intent);
    }
    public void VerPresenca(View view){
        Intent intent = new Intent(this,ListaDePresenca.class);
        intent.putExtra("Evento_key",eventoId);
        startActivity(intent);

    }
    public void fecharEvento(View view) {
        if (eventoId == null || eventoId.isEmpty()) return;

        DatabaseReference eventoRef = FirebaseDatabase.getInstance().getReference("eventos").child(eventoId);
        DatabaseReference encerradosRef = FirebaseDatabase.getInstance().getReference("eventosEncerrados");

        // Pega os dados completos do evento
        eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                // Cria um mapa com todos os dados, incluindo o nó "Presenca"
                Map<String, Object> eventoMap = (Map<String, Object>) snapshot.getValue();

                // Copia para "eventosEncerrados"
                encerradosRef.child(eventoId).setValue(eventoMap)
                        .addOnSuccessListener(aVoid -> {
                            // Remove do nó original
                            eventoRef.removeValue()
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(ManutencaoEvento.this, "Evento fechado com sucesso!", Toast.LENGTH_SHORT).show();

                                        // Abre a tela Participaram
                                        Intent intent = new Intent(ManutencaoEvento.this, EventosFechados.class);
                                        startActivity(intent);
                                        finish();
                                    });
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    public void Sair(View View){
        finish();
    }
}
