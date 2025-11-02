package com.example.qrcode.AdminEvento;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrcode.DrawerBaseActivity;
import com.example.qrcode.R;
import com.example.qrcode.dao.AdminEventoDAO;
import com.example.qrcode.databinding.ActivityManutencaoEventoBinding;
import com.example.qrcode.model.AdminEvento;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManutencaoEvento extends DrawerBaseActivity {

    private Button btnAlterarEvento, btnLimparManutencao, btnExcluirManutencao;
    private EditText edtNomeEventoManutencao, edtLocalManutencao, edtDataManutencao,
            edtOrganizadorManutencao, edtHorarioInicioManutencao, edtHorarioFimManutencao,
            edtDescricaoManutencao, edtIdEvento;

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
        btnLimparManutencao = findViewById(R.id.btnLimparManutencao);

        edtNomeEventoManutencao = findViewById(R.id.edtNomeEventoManutencao);
        edtLocalManutencao = findViewById(R.id.edtLocalManutencao);
        edtDataManutencao = findViewById(R.id.edtDataManutencao);
        edtOrganizadorManutencao = findViewById(R.id.edtOrganizadorManutencao);
        edtHorarioInicioManutencao = findViewById(R.id.edtHorarioInicioManutencao);
        edtHorarioFimManutencao = findViewById(R.id.edtHorarioFimManutencao);
        edtDescricaoManutencao = findViewById(R.id.edtDescricaoManutencao);
        //edtIdEvento = findViewById(R.id.edtIdEvento);

        // Pega os dados vindos da Intent
        eventoId = getIntent().getStringExtra("adminEvento_key"); // agora usamos o ID do Firebase

        //edtIdEvento.setText(eventoId); // mostra no campo (opcional)
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

        // Atualiza o objeto com os novos valores
        AdminEvento adminEvento = new AdminEvento();
        //adminEvento.setID(0); // Não usamos o ID autoincremento no Firebase
        adminEvento.setNomeEvento(edtNomeEventoManutencao.getText().toString());
        adminEvento.setLocal(edtLocalManutencao.getText().toString());
        adminEvento.setData(edtDataManutencao.getText().toString());
        adminEvento.setOrganizador(edtOrganizadorManutencao.getText().toString());
        adminEvento.setHorarioInicio(edtHorarioInicioManutencao.getText().toString());
        adminEvento.setHorarioFim(edtHorarioFimManutencao.getText().toString());
        adminEvento.setDescricao(edtDescricaoManutencao.getText().toString());

        // Atualiza no Firebase
        eventosRef.child(eventoId).setValue(adminEvento)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Evento alterado com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao alterar: " + e.getMessage(), Toast.LENGTH_LONG).show());
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

    public void limpar(View view) {

        edtNomeEventoManutencao.setText(null);
        edtLocalManutencao.setText(null);
        edtDataManutencao.setText(null);
        edtOrganizadorManutencao.setText(null);
        edtHorarioInicioManutencao.setText(null);
        edtHorarioFimManutencao.setText(null);
        edtDescricaoManutencao.setText(null);
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
}
