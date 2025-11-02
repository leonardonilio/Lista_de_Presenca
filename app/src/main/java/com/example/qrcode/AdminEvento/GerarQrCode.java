package com.example.qrcode.AdminEvento;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcode.R;

import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GerarQrCode extends AppCompatActivity {

    private ImageView qrImage;
    private TextView txtInfo;

    private Bitmap qrBitmap;
    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String ACTION_USB_PERMISSION = "com.example.qrcode.USB_PERMISSION";

    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbDeviceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gerar_qr_code);

        qrImage = findViewById(R.id.qrImage);
        txtInfo = findViewById(R.id.edtTextoID);


        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // Recebe o ID do evento
        String eventoId = getIntent().getStringExtra("Evento_key");

        if (eventoId != null && !eventoId.isEmpty()) {
            gerarQRCode(eventoId);
            txtInfo.setText("QR Code do evento ID: " + eventoId);
        } else {
            Toast.makeText(this, "Erro: ID do evento não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    // -------------------------------------------------
    // GERA O QR CODE
    // -------------------------------------------------
    private void gerarQRCode(String idEvento) {
        try {
            QRGEncoder qrgEncoder = new QRGEncoder(idEvento, null, QRGContents.Type.TEXT, 500);
            qrgEncoder.setColorBlack(Color.WHITE);
            qrgEncoder.setColorWhite(Color.BLACK);

            qrBitmap = qrgEncoder.getBitmap();
            qrImage.setImageBitmap(qrBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }



}
