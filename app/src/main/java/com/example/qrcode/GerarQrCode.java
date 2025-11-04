package com.example.qrcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.qrcode.databinding.ActivityGerarQrCodeBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GerarQrCode extends DrawerBaseActivity {

    private ImageView qrImage;
    private TextView txtInfo;
    private Button btnImprimir;
    private EditText edtImpressoraID;
    ActivityGerarQrCodeBinding activityGerarQrCodeBinding;

    PrintBluetooth printBT = new PrintBluetooth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                // Android 12 ou superior
                androidx.core.app.ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                android.Manifest.permission.BLUETOOTH_CONNECT,
                                android.Manifest.permission.BLUETOOTH_SCAN
                        },
                        1
                );
            } else {
                // Android 11 ou inferior
                androidx.core.app.ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                android.Manifest.permission.BLUETOOTH,
                                android.Manifest.permission.BLUETOOTH_ADMIN,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        1
                );
            }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityGerarQrCodeBinding = ActivityGerarQrCodeBinding.inflate(getLayoutInflater());
        setContentView(activityGerarQrCodeBinding.getRoot());

        qrImage = findViewById(R.id.qrImage);
        txtInfo = findViewById(R.id.edtTextoID);
        btnImprimir = findViewById(R.id.btnImprimir);
        edtImpressoraID = findViewById(R.id.edtImpressoraID);

        String eventoId = getIntent().getStringExtra("Evento_key");

        if (eventoId != null && !eventoId.isEmpty()) {
            gerarQRCode(eventoId);
            txtInfo.setText("QR Code do evento ID: " + eventoId);
        } else {
            Toast.makeText(this, "Erro: ID do evento não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintBluetooth.printer_id = "MPT-II";
                Bitmap qrBit = imprimirQRCode(eventoId);
                // adiciona borda branca
                qrBit = addWhiteBorder(qrBit, 30);
                try {
                    printBT.findBT();
                    printBT.openBT();
                    printBT.printQrCode(qrBit);
                    // avanço de papel para garantir corte completo
                    printBT.mmOutputStream.write(new byte[]{0x0A, 0x0A, 0x0A});
                    Thread.sleep(100);
                    printBT.closeBT();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(GerarQrCode.this, "Erro ao imprimir", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void gerarQRCode(String idEvento) {
        try {
            String conteudo = idEvento;
            // Tamanho reduzido para impressora térmica
            QRGEncoder qrgEncoder = new QRGEncoder(conteudo, null, QRGContents.Type.TEXT, 300);
            qrgEncoder.setColorBlack(android.graphics.Color.BLACK);
            qrgEncoder.setColorWhite(android.graphics.Color.WHITE);

            Bitmap qrBitmap = qrgEncoder.getBitmap();
            qrImage.setImageBitmap(qrBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap imprimirQRCode(String textToQR) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToQR, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // método para adicionar uma borda branca ao QR
    private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        int newWidth = bmp.getWidth() + borderSize * 1;
        int newHeight = bmp.getHeight() + borderSize * 1;
        Bitmap output = Bitmap.createBitmap(newWidth, newHeight, bmp.getConfig());
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return output;
    }
}