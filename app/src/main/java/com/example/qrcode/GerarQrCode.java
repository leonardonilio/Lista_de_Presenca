package com.example.qrcode;

import android.graphics.Bitmap;
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


// import androidmads.library.qrgenearator.BarcodeEncoder;
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
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityGerarQrCodeBinding = ActivityGerarQrCodeBinding.inflate(getLayoutInflater());
        setContentView(activityGerarQrCodeBinding.getRoot());
            qrImage = findViewById(R.id.qrImage);
            txtInfo = findViewById(R.id.edtTextoID);
            btnImprimir = findViewById(R.id.btnImprimir);
            edtImpressoraID = findViewById(R.id.edtImpressoraID);

            String eventoId = getIntent().getStringExtra("Evento_key");

            if (!eventoId.isEmpty()) {
                gerarQRCode(String.valueOf(eventoId));
                txtInfo.setText("QR Code do evento ID: " + eventoId);
            } else {
                Toast.makeText(this, "Erro: ID do evento não encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }

            btnImprimir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrintBluetooth.printer_id = edtImpressoraID.getText().toString();
                    Bitmap qrBit = imprimirQRCode(eventoId);
                    try{
                        printBT.findBT();
                        printBT.openBT();
                        printBT.printQrCode(qrBit);
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




    private Bitmap imprimirQRCode(String textToQR) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToQR, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch(WriterException e){
            e.printStackTrace();
            return null;
        }
      }
    }

