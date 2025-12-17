package com.org.hosply360.util.Others;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class BarcodeGenerator {

    public static String generateCode128BarcodeBase64(String text) {
        try {
            int width = 400;
            int height = 120;

            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.CODE_128, width, height);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", baos);

            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Barcode generation failed", e);
        }
    }
}
