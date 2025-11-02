package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qrcode.dao.IngressanteDAO;
import com.example.qrcode.model.Ingressante;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastrarUsuario extends AppCompatActivity {

    private EditText edtNomeUser,edtTelefone,edtEmail;
    private DatabaseReference ingressanteRef;
    private String ultimoIngressanteKey,NomeIngressante; // ← guarda a chave gerada no Firebase e o nome do Ingressante
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastrar_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtNomeUser = findViewById(R.id.edtNomeUser);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelefone = findViewById(R.id.edtTelefone);
        ingressanteRef = FirebaseDatabase.getInstance().getReference("ingressantes");
    }
    public void limpar(View view){
        edtNomeUser.setText(null);
        edtEmail.setText(null);
        edtTelefone.setText(null);
    }
    public void salvar(View view) {
       String nome = edtNomeUser.getText().toString();
       NomeIngressante = edtNomeUser.getText().toString();
        String email = edtEmail.getText().toString();
        String telefone = edtTelefone.getText().toString();

        //verifica se foi colocado alguma coisa
        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria objeto
        Ingressante ingressante = new Ingressante();
        ingressante.setNomeIngressante(nome);
        ingressante.setEmailIngressante(email);
        ingressante.setTelefoneIngressante(telefone);

        IngressanteDAO dao = new IngressanteDAO();
        ultimoIngressanteKey = dao.insert(ingressante);

        Intent intent = new Intent(getApplicationContext(), EventoMain.class);
        intent.putExtra("Ingressante_key", ultimoIngressanteKey);// envia a key do Firebase
        intent.putExtra("NomeIngressante", NomeIngressante);
        startActivity(intent);
        finish();
    }
    public void entrar(View view) {
        String nome = edtNomeUser.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String telefone = edtTelefone.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        IngressanteDAO dao = new IngressanteDAO();
        dao.login(nome, email, telefone, new IngressanteDAO.LoginCallback() {
            @Override
            public void onLoginSuccess(String keyIngressante, String nomeIngressante) {
                Toast.makeText(CadastrarUsuario.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), EventoMain.class);
                intent.putExtra("Ingressante_key", keyIngressante);
                intent.putExtra("NomeIngressante", nomeIngressante);
                startActivity(intent);
                finish();

            }

            @Override
            public void onLoginFailed() {
                Toast.makeText(CadastrarUsuario.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginError(String error) {
                Toast.makeText(CadastrarUsuario.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Sair(View view) {
        finish();
    }
}