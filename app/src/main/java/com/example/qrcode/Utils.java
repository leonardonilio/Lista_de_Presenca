package com.example.qrcode;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class Utils {
    public static byte[] bitmapToEscPos(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
