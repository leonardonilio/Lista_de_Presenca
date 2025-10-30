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

import com.example.qrcode.model.AdminEvento;
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


        // Gera uma nova chave única no Firebase
        String key = ingressanteRef.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Erro ao gerar chave do evento", Toast.LENGTH_SHORT).show();
            return;
        }

        ingressante.setKeyIngressante(key); // guarda a key dentro do objeto
        ultimoIngressanteKey = key;


        // Salva no Firebase
        ingressanteRef.child(key).setValue(ingressante)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Usuario salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erro ao salvar ingressante: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
        Intent intent = new Intent(getApplicationContext(), EventoMain.class);
        intent.putExtra("Ingressante_key", ultimoIngressanteKey);// envia a key do Firebase
        intent.putExtra("NomeIngressante", NomeIngressante);
        startActivity(intent);
    }
    public void Sair(View view) {
        finish();
    }
}