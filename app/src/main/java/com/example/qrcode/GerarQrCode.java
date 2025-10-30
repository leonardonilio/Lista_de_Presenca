package com.example.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.qrcode.databinding.ActivityGerarQrCodeBinding;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GerarQrCode extends DrawerBaseActivity {

    private ImageView qrImage;
    private TextView txtInfo;
    ActivityGerarQrCodeBinding activityGerarQrCodeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityGerarQrCodeBinding = ActivityGerarQrCodeBinding.inflate(getLayoutInflater());
        setContentView(activityGerarQrCodeBinding.getRoot());
            qrImage = findViewById(R.id.qrImage);
            txtInfo = findViewById(R.id.edtTextoID);

            String eventoId = getIntent().getStringExtra("Evento_key");

            if (!eventoId.isEmpty()) {
                gerarQRCode(String.valueOf(eventoId));
                txtInfo.setText("QR Code do evento ID: " + eventoId);
            } else {
                Toast.makeText(this, "Erro: ID do evento não encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        private void gerarQRCode(String idEvento) {
            try {
                String conteudo = idEvento;

                QRGEncoder qrgEncoder = new QRGEncoder(conteudo, null, QRGContents.Type.TEXT, 500);
                qrgEncoder.setColorBlack(android.graphics.Color.WHITE);
                qrgEncoder.setColorWhite(android.graphics.Color.BLACK);

                Bitmap qrBitmap = qrgEncoder.getBitmap();
                qrImage.setImageBitmap(qrBitmap);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
            }
        }
    }

