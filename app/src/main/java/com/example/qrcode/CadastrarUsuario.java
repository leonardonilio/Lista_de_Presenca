package com.example.qrcode;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastrarUsuario extends AppCompatActivity {

    private EditText edtNomeUser,edtTelefone,edtEmail;
    private DatabaseReference ingressanteRef;
    private String ultimoIngressanteKey; // ← guarda a chave gerada no Firebase
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
        ingressanteRef = FirebaseDatabase.getInstance().getReference("eventos");
    }
    public void limpar(View view){
        edtNomeUser.setText(null);
        edtEmail.setText(null);
        edtTelefone.setText(null);
    }
    public void salvar(View view) {
        String nome = edtNomeUser.getText().toString();
        String email = edtEmail.getText().toString();
        String telefone= edtTelefone.getText().toString();


        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria objeto
        AdminEvento evento = new AdminEvento();
        evento.setNomeEvento(nome);
        evento.setLocal(email);
        evento.setData(telefone);


        // Gera uma nova chave única no Firebase
        String key = ingressanteRef.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Erro ao gerar chave do evento", Toast.LENGTH_SHORT).show();
            return;
        }

        evento.setKey(key); // guarda a key dentro do objeto
        ultimoIngressanteKey = key;


        // Salva no Firebase
        ingressanteRef.child(key).setValue(evento)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Evento salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erro ao salvar evento: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}