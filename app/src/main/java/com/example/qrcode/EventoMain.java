package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qrcode.AdminEvento.ManutencaoEvento;
import com.example.qrcode.databinding.ActivityEventoMainBinding;
import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class EventoMain extends DrawerBaseActivity {

    ActivityEventoMainBinding binding;
    SearchView mysearchview1;
    String Id_User;
    String Nome_User;

    private ListView listViewEventos;
    ArrayAdapter<String> adapter;
    List<AdminEvento> listaEventos = new ArrayList<>();
    List<String> nomesEventos = new ArrayList<>();

    DatabaseReference refEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventoMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        allocateActivityTitle("Meus Eventos");

        listViewEventos = findViewById(R.id.lv_dadosEventos);
        mysearchview1 = findViewById(R.id.searchviewEvento);

        refEventos = FirebaseDatabase.getInstance().getReference("eventos");

        // Recebe dados do usuário
        Intent intent = getIntent();
        if (intent != null) {
            Id_User = intent.getStringExtra("Ingressante_key");
            Nome_User = intent.getStringExtra("NomeIngressante");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomesEventos);
        listViewEventos.setAdapter(adapter);

        carregarEventosDoUsuario();
        listViewEventos.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < listaEventos.size()) {
                AdminEvento eventoSelecionado = listaEventos.get(position);
                abrirManutencao(eventoSelecionado);
            }
        });
        mysearchview1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    //  Carrega apenas os eventos em que o usuário marcou presença
    private void carregarEventosDoUsuario() {
        refEventos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEventos.clear();
                nomesEventos.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdminEvento evento = ds.getValue(AdminEvento.class);
                    if (evento != null && ds.child("Presenca").hasChild(Id_User)) {
                        evento.setKey(ds.getKey());
                        listaEventos.add(evento);
                        nomesEventos.add("\nNome: " + evento.getNomeEvento()
                                + "\nLocal: " + evento.getLocal()
                                + "\nData: " + evento.getData()
                                + "\nInício: " + evento.getHorarioInicio()
                                + "\nTérmino: " + evento.getHorarioFim()
                                + "\nDescrição: " + evento.getDescricao());
                    }
                }

                if (nomesEventos.isEmpty()) {
                    Toast.makeText(EventoMain.this, "Você ainda não está cadastrado em nenhum evento.", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EventoMain.this, "Erro ao carregar eventos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void abrirManutencao(AdminEvento adminEvento) {
        Intent it = new Intent(getApplicationContext(), DetalhesDoEvento.class);
        it.putExtra("adminEvento_key", adminEvento.getKey());
        it.putExtra("adminEvento_Nome", adminEvento.getNomeEvento());
        it.putExtra("adminEvento_Local", adminEvento.getLocal());
        it.putExtra("adminEvento_Data", adminEvento.getData());
        it.putExtra("adminEvento_Organizador", adminEvento.getOrganizador());
        it.putExtra("adminEvento_HorarioInicio", adminEvento.getHorarioInicio());
        it.putExtra("adminEvento_HorarioFim", adminEvento.getHorarioFim());
        it.putExtra("adminEvento_Descricao", adminEvento.getDescricao());
        startActivity(it);
    }

    // ====== Leitura do QR Code ======
    public void LerQrcode(View view) {
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
                    Intent intent = new Intent(getApplicationContext(), PresencaDoEvento.class);
                    intent.putExtra("Ingressante_key", Id_User);
                    intent.putExtra("NomeIngressante", Nome_User);
                    intent.putExtra("Evento_key", eventoKey);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "QR não reconhecido (evento não encontrado).", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro ao acessar o Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
