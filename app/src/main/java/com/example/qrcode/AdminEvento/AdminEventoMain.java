package com.example.qrcode.AdminEvento;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qrcode.DrawerBaseActivity;
import com.example.qrcode.R;
import com.example.qrcode.dao.AdminEventoDAO;
import com.example.qrcode.databinding.ActivityAdminEventoMainBinding;
import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminEventoMain extends DrawerBaseActivity {

    private ListView lv_dados;
    private SearchView mysearchview;
    private ArrayAdapter<String> adapter;
    private List<String> listaEventos;
    private List<AdminEvento> eventos;
    private AdminEventoDAO eventoDAO;
    private ActivityAdminEventoMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminEventoMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        allocateActivityTitle("Lista de Cadastro");

        eventoDAO = new AdminEventoDAO();

        mysearchview = findViewById(R.id.searchview);
        lv_dados = findViewById(R.id.lv_dados);

        listaEventos = new ArrayList<>();
        eventos = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaEventos);
        lv_dados.setAdapter(adapter);

        carregarDadosEvento();

        lv_dados.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < eventos.size()) {
                AdminEvento eventoSelecionado = eventos.get(position);
                abrirManutencao(eventoSelecionado);
            }
        });

        mysearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void carregarDadosEvento() {
        eventoDAO.obterTodos(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEventos.clear();
                eventos = AdminEventoDAO.snapshotToList(snapshot);

                for (AdminEvento evento : eventos) {
                    String info = "\nNome: " + evento.getNomeEvento()
                            + "\nLocal: " + evento.getLocal()
                            + "\nData: " + evento.getData()
                            + "\nInício: " + evento.getHorarioInicio()
                            + "\nTérmino: " + evento.getHorarioFim()
                            + "\nDescrição: " + evento.getDescricao();
                    listaEventos.add(info);
                }

                adapter.notifyDataSetChanged();
                Toast.makeText(AdminEventoMain.this, "Eventos carregados: " + eventos.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminEventoMain.this, "Erro ao carregar eventos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirManutencao(AdminEvento adminEvento) {
        Intent it = new Intent(getApplicationContext(), ManutencaoEvento.class);
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
}
