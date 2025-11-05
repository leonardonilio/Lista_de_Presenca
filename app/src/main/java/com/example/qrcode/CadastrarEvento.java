package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrcode.dao.AdminEventoDAO;
import com.example.qrcode.databinding.ActivityCadastrarEventoBinding;
import com.example.qrcode.model.AdminEvento;

import java.util.HashMap;

public class CadastrarEvento extends DrawerBaseActivity {

    private EditText edtNomeEvento, edtLocal, edtData, edtOrganizador, edtHorarioInicio, edtHorarioFim, edtDescricao;
    private Button btnCadastrarEvento, btnVoltar;
    private String ultimoEventoKey;
    private AdminEventoDAO eventoDAO;
    ActivityCadastrarEventoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastrarEventoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Cadastrar");

        edtNomeEvento = findViewById(R.id.edtNomeEvento);
        edtLocal = findViewById(R.id.edtLocal);
        edtData = findViewById(R.id.edtData);
        edtOrganizador = findViewById(R.id.edtOrganizador);
        edtHorarioInicio = findViewById(R.id.edtHorarioInicio);
        edtHorarioFim = findViewById(R.id.edtHorarioFim);
        edtDescricao = findViewById(R.id.edtDescricao);
        btnCadastrarEvento = findViewById(R.id.btnCadastrarUsuario);
        btnVoltar = findViewById(R.id.btnVoltarUsuario);

        eventoDAO = new AdminEventoDAO();
    }

    public void limpar(View view) {
        edtNomeEvento.setText(null);
        edtLocal.setText(null);
        edtData.setText(null);
        edtOrganizador.setText(null);
        edtHorarioInicio.setText(null);
        edtHorarioFim.setText(null);
        edtDescricao.setText(null);
    }

    public void Salvar(View view) {
        String nome = edtNomeEvento.getText().toString().trim();
        String local = edtLocal.getText().toString().trim();
        String data = edtData.getText().toString().trim();
        String organizador = edtOrganizador.getText().toString().trim();
        String horarioInicio = edtHorarioInicio.getText().toString().trim();
        String horarioFim = edtHorarioFim.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();

        if (nome.isEmpty() || local.isEmpty() || data.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminEvento evento = new AdminEvento();
        evento.setNomeEvento(nome);
        evento.setLocal(local);
        evento.setData(data);
        evento.setOrganizador(organizador);
        evento.setHorarioInicio(horarioInicio);
        evento.setHorarioFim(horarioFim);
        evento.setDescricao(descricao);
        evento.setPresenca(new HashMap<>());

        eventoDAO.insert(evento, new AdminEventoDAO.FirebaseCallback() {
            @Override
            public void onSuccess(String key) {
                ultimoEventoKey = key;
                Toast.makeText(CadastrarEvento.this, "Evento salvo com sucesso!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CadastrarEvento.this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void gerarQRCode(View view) {
        if (ultimoEventoKey == null || ultimoEventoKey.isEmpty()) {
            Toast.makeText(this, "Salve o evento antes de gerar o QR Code!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GerarQrCode.class);
        intent.putExtra("Evento_key", ultimoEventoKey);
        startActivity(intent);
    }

    public void Sair(View view) {
        finish();
    }
}
