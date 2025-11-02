package com.example.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.qrcode.databinding.ActivityDetalhesDoEventoBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DetalhesDoEvento extends DrawerBaseActivity {

    private EditText edtNomeEventoVisto, edtLocalVisto, edtDataVisto,
            edtOrganizadorVisto, edtHorarioIniciovisto, edtHorarioFimVisto,
            edtDescricaoVisto;

    private DatabaseReference eventosRef;



    private String eventoId; // Firebase key
    private ActivityDetalhesDoEventoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesDoEventoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Conexão Firebase
        eventosRef = FirebaseDatabase.getInstance().getReference("Eventos");

        // Vincular EditTexts
        edtNomeEventoVisto = binding.edtNomeVisto;
        edtLocalVisto = binding.edtLocaVisto;
        edtDataVisto = binding.edtDataVisto;
        edtOrganizadorVisto = binding.edtOrganizadorVisto;
        edtHorarioIniciovisto = binding.edtHorarioiniVisto;
        edtHorarioFimVisto = binding.edtHorarioVisto;
        edtDescricaoVisto = binding.edtDescricaoVisto;

        // Pega dados da Intent
        eventoId = getIntent().getStringExtra("adminEvento_key");
        edtNomeEventoVisto.setText(getIntent().getStringExtra("adminEvento_Nome"));
        edtLocalVisto.setText(getIntent().getStringExtra("adminEvento_Local"));
        edtDataVisto.setText(getIntent().getStringExtra("adminEvento_Data"));
        edtOrganizadorVisto.setText(getIntent().getStringExtra("adminEvento_Organizador"));
        edtHorarioIniciovisto.setText(getIntent().getStringExtra("adminEvento_HorarioInicio"));
        edtHorarioFimVisto.setText(getIntent().getStringExtra("adminEvento_HorarioFim"));
        edtDescricaoVisto.setText(getIntent().getStringExtra("adminEvento_Descricao"));

        // Botão Ver QR Code
        binding.VerQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoParaQR = eventoId; // ou outro dado
                gerarQRCode(textoParaQR);
                binding.qrImage3.setVisibility(View.VISIBLE);

                // Rolar até o QR Code
                binding.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.scrollView.scrollTo(0, binding.qrImage3.getBottom());
                    }
                });
            }
        });
    }

    public void Sair(View view) {
        finish();
    }

    // Método para gerar QR Code
    private void gerarQRCode(String texto) {
        try {
            QRGEncoder qrgEncoder = new QRGEncoder(texto, null, QRGContents.Type.TEXT, 500);
            qrgEncoder.setColorBlack(Color.WHITE);
            qrgEncoder.setColorWhite(Color.BLACK);

            Bitmap qrBitmap = qrgEncoder.getBitmap();
            binding.qrImage3.setImageBitmap(qrBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }
}
