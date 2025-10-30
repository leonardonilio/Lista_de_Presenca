package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrcode.databinding.ActivityCadastrarEventoBinding;
import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastrarEvento extends DrawerBaseActivity {

    private EditText edtNomeEvento, edtLocal, edtData, edtOrganizador, edtHorarioInicio, edtHorarioFim, edtDescricao;
    private Button btnCadastrarEvento, btnVoltar;
    private String ultimoEventoKey; // ← guarda a chave gerada no Firebase
    private DatabaseReference referenciaEventos;

    ActivityCadastrarEventoBinding activityCadastrarEventoBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCadastrarEventoBinding = ActivityCadastrarEventoBinding.inflate(getLayoutInflater());
        setContentView(activityCadastrarEventoBinding.getRoot());
        allocateActivityTitle("Cadastrar");

        // Inicializa campos
        edtNomeEvento = findViewById(R.id.edtNomeUser);
        edtLocal = findViewById(R.id.edtEmail);
        edtData = findViewById(R.id.edtTelefone);
        edtOrganizador = findViewById(R.id.edtOrganizador);
        edtHorarioInicio = findViewById(R.id.edtHorarioInicio);
        edtHorarioFim = findViewById(R.id.edtHorarioFim);
        edtDescricao = findViewById(R.id.edtDescricao);

        btnCadastrarEvento = findViewById(R.id.btnCadastrarUsuario);
        btnVoltar = findViewById(R.id.btnVoltarUsuario);

        // Inicializa o Firebase
        referenciaEventos = FirebaseDatabase.getInstance().getReference("eventos");
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

    public void salvar(View view) {
        String nome = edtNomeEvento.getText().toString();
        String local = edtLocal.getText().toString();
        String data = edtData.getText().toString();
        String organizador = edtOrganizador.getText().toString();
        String horarioInicio = edtHorarioInicio.getText().toString();
        String horarioFim = edtHorarioFim.getText().toString();
        String descricao = edtDescricao.getText().toString();

        if (nome.isEmpty() || local.isEmpty() || data.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria objeto
        AdminEvento evento = new AdminEvento();
        evento.setNomeEvento(nome);
        evento.setLocal(local);
        evento.setData(data);
        evento.setOrganizador(organizador);
        evento.setHorarioInicio(horarioInicio);
        evento.setHorarioFim(horarioFim);
        evento.setDescricao(descricao);

        // Gera uma nova chave única no Firebase
        String key = referenciaEventos.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Erro ao gerar chave do evento", Toast.LENGTH_SHORT).show();
            return;
        }

        evento.setKey(key); // guarda a key dentro do objeto
        ultimoEventoKey = key;


        // Salva no Firebase
        referenciaEventos.child(key).setValue(evento)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Evento salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erro ao salvar evento: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void gerarQRCode(View view) {

        if (ultimoEventoKey == null || ultimoEventoKey.isEmpty()) {
            Toast.makeText(this, "Salve o evento antes de gerar o QR Code!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GerarQrCode.class);
        intent.putExtra("Evento_key", ultimoEventoKey); // envia a key do Firebase
        startActivity(intent);
    }

    public void Sair(View view) {
        finish();
    }
}
