package com.example.qrcode;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qrcode.dao.PresencaDAO;
import com.example.qrcode.databinding.ActivityEventosFechadosBinding;
import com.example.qrcode.databinding.ActivityParticiparamBinding;
import com.example.qrcode.model.Presenca;
import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Participaram extends DrawerBaseActivity {

    private ListView lvPresencas;
    private SearchView searchViewPresencas;
    private ArrayAdapter<String> adapter;
    private List<String> listaPresencas;
    private List<Presenca> presencas;
    private String eventoId;
    private DatabaseReference presencasRef;
    private PresencaDAO presencaDAO;
    private ActivityParticiparamBinding Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = ActivityParticiparamBinding.inflate(getLayoutInflater());
        setContentView(Binding.getRoot());
        allocateActivityTitle("Lista de Participantes");



        lvPresencas = findViewById(R.id.lvPresencas); // Crie este ListView no layout
        searchViewPresencas = findViewById(R.id.searchViewPresencas); // Crie este SearchView no layout
        listaPresencas = new ArrayList<>();
        presencas = new ArrayList<>();
        presencaDAO = new PresencaDAO();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPresencas);
        lvPresencas.setAdapter(adapter);

        eventoId = getIntent().getStringExtra("Evento_key");
        if (eventoId == null || eventoId.isEmpty()) {
            Toast.makeText(this, "Evento inválido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Aqui mudamos para "eventosEncerrados"
        presencasRef = FirebaseDatabase.getInstance()
                .getReference("eventosEncerrados")
                .child(eventoId)
                .child("Presenca");

        carregarPresencas();

        lvPresencas.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < presencas.size()) {
                Presenca p = presencas.get(position);
                mostrarOpcoes(p);
            }
        });

        searchViewPresencas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void carregarPresencas() {
        presencasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                presencas.clear();
                listaPresencas.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Presenca p = ds.getValue(Presenca.class);
                    if (p != null) {
                        presencas.add(p);

                        String entrada = p.getHorarioEntrada() != null ? formatarHorario(p.getHorarioEntrada()) : "—";
                        String saida = p.getHorarioSaida() != null ? formatarHorario(p.getHorarioSaida()) : "Ainda no evento";

                        String info = "Nome: " + p.getFk_Nome() +
                                "\nEntrada: " + entrada +
                                "\nSaída: " + saida + "\n";

                        listaPresencas.add(info);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Participaram.this, "Erro ao carregar presenças.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarOpcoes(Presenca presenca) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opções para " + presenca.getFk_Nome());
        builder.setItems(new CharSequence[]{"Atualizar horário de saída", "Deletar presença", "Cancelar"},
                (dialog, which) -> {
                    if (which == 0) {
                        atualizarHorarioSaida(presenca);
                    } else if (which == 1) {
                        deletarPresenca(presenca);
                    }
                });
        builder.show();
    }

    private void atualizarHorarioSaida(Presenca presenca) {
        String novoHorarioSaida = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        presencaDAO.atualizarHorarioSaidaIndividual(
                eventoId,
                presenca.getFk_KeyIngressante(),
                novoHorarioSaida,
                () -> runOnUiThread(() -> Toast.makeText(this, "Horário de saída atualizado!", Toast.LENGTH_SHORT).show()),
                () -> runOnUiThread(() -> Toast.makeText(this, "Erro ao atualizar horário de saída.", Toast.LENGTH_SHORT).show())
        );
    }

    private void deletarPresenca(Presenca presenca) {
        presencaDAO.deletePresenca(
                eventoId,
                presenca.getFk_KeyIngressante(),
                () -> runOnUiThread(() -> Toast.makeText(this, "Presença excluída!", Toast.LENGTH_SHORT).show()),
                () -> runOnUiThread(() -> Toast.makeText(this, "Erro ao excluir presença.", Toast.LENGTH_SHORT).show())
        );
    }

    private String formatarHorario(String horarioOriginal) {
        if (horarioOriginal == null || horarioOriginal.isEmpty()) return "—";
        try {
            String horario = horarioOriginal.replace("T", " ");
            SimpleDateFormat sdfEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdfEntrada.parse(horario);
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        } catch (Exception e) {
            return horarioOriginal;
        }
    }
}
