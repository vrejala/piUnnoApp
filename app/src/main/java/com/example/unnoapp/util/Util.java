package com.example.unnoapp.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Util {

    // Converte Bitmap em MultipartBody.Part para enviar via Retrofit
    public static MultipartBody.Part createMultipartFromBitmap(Bitmap bitmap, String partName) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] bitmapData = bos.toByteArray();

        RequestBody reqFile = RequestBody.create(bitmapData, MediaType.parse("image/jpeg"));
        return MultipartBody.Part.createFormData(partName, "foto.jpg", reqFile);
    }
}
