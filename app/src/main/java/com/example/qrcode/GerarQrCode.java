package com.example.qrcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qrcode.databinding.ActivityGerarQrCodeBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GerarQrCode extends AppCompatActivity {

    private ImageView qrImage;
    private TextView txtInfo;
    private Button btnImprimirBluetooth, btnImprimirUSB;
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
        btnImprimirBluetooth = findViewById(R.id.btnImprimir);
        btnImprimirUSB = findViewById(R.id.btnImprimirUSB);

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

        /*btnImprimirBluetooth.setOnClickListener(v -> imprimirBluetooth());
        btnImprimirUSB.setOnClickListener(v -> detectarImpressoraUSB());*/
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

    // -------------------------------------------------
    // BLUETOOTH
    // -------------------------------------------------
    public void imprimirBluetooth(View view) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não suportado.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Ative o Bluetooth e tente novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.isEmpty()) {
            Toast.makeText(this, "Nenhuma impressora Bluetooth pareada.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().toLowerCase().contains("printer") ||
                    device.getName().toLowerCase().contains("pegasus") ||
                    device.getName().toLowerCase().contains("MPT-II")) {

                try {
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID);
                    socket.connect();
                    OutputStream outputStream = socket.getOutputStream();

                    byte[] command = Utils.bitmapToEscPos(qrBitmap);
                    outputStream.write(command);

                    String msg = "\nEvento: " + txtInfo.getText().toString() + "\n\n";
                    outputStream.write(msg.getBytes());

                    outputStream.flush();
                    outputStream.close();
                    socket.close();

                    Snackbar.make(qrImage, "Impressão Bluetooth concluída!", Snackbar.LENGTH_LONG).show();
                    return;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao imprimir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        Toast.makeText(this, "Nenhuma impressora compatível encontrada.", Toast.LENGTH_SHORT).show();
    }

    // -------------------------------------------------
    // USB (OTG)
    // -------------------------------------------------
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void detectarImpressoraUSB(View view) {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        if (deviceList.isEmpty()) {
            Toast.makeText(this, "Nenhuma impressora USB detectada.", Toast.LENGTH_SHORT).show();
            return;
        }

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (device.getInterfaceCount() > 0) {
                usbDevice = device;
                PendingIntent permissionIntent = PendingIntent.getBroadcast(
                        this, 0, new Intent(ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_IMMUTABLE);

                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                registerReceiver(usbReceiver, filter);

                usbManager.requestPermission(device, permissionIntent);
                break;
            }
        }
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            imprimirUSB(device);
                        }
                    } else {
                        Toast.makeText(context, "Permissão negada para o dispositivo USB", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private void imprimirUSB(UsbDevice device) {
        connection = usbManager.openDevice(device);

        if (connection == null) {
            Toast.makeText(this, "Erro ao conectar à impressora USB.", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] data = Utils.bitmapToEscPos(qrBitmap);
        int sent = connection.bulkTransfer(device.getInterface(0).getEndpoint(1), data, data.length, 1000);

        if (sent >= 0) {
            Toast.makeText(this, "Impressão via USB concluída!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erro ao enviar dados para impressora USB.", Toast.LENGTH_SHORT).show();
        }

        connection.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(usbReceiver);
        } catch (Exception ignored) {}
    }
}
