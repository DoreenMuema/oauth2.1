package com.muema.oauth21.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeUtils {

    /**
     * Generates a QR code from the provided text and returns it as a byte array.
     *
     * @param text The text to encode in the QR code
     * @return A byte array containing the QR code image in PNG format
     * @throws WriterException if there is an error during QR code generation
     * @throws IOException if there is an error writing the QR code to the output stream
     */
    public static byte[] generateQRCode(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter(); // Create QR code writer instance
        // Encode the text into a BitMatrix representing the QR code
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);

        // Use ByteArrayOutputStream to capture the PNG image data
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            // Write the BitMatrix to the output stream in PNG format
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray(); // Return the generated PNG image as a byte array
        }
    }
}