package com.moviebooking.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.moviebooking.entity.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class QRCodeService {

    private static final Logger log = LoggerFactory.getLogger(QRCodeService.class);
    private static final String HMAC_SECRET = "moviebooking-qr-secret-2026";

    public String generateQRCode(Booking booking) {
        try {
            String qrContent = buildQRContent(booking);
            String hash = generateHMAC(qrContent);
            String fullContent = qrContent + "|HASH:" + hash;

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(fullContent, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            String dataUri = "data:image/png;base64," + base64Image;

            log.info("QR code generated for booking {}", booking.getBookingReference());
            return dataUri;

        } catch (WriterException | java.io.IOException e) {
            log.error("Failed to generate QR code for booking {}: {}", booking.getBookingReference(), e.getMessage());
            return null;
        }
    }

    public boolean verifyQRCode(String qrContent) {
        try {
            int hashIndex = qrContent.lastIndexOf("|HASH:");
            if (hashIndex == -1) return false;

            String content = qrContent.substring(0, hashIndex);
            String providedHash = qrContent.substring(hashIndex + 6);
            String expectedHash = generateHMAC(content);

            return providedHash.equals(expectedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private String buildQRContent(Booking booking) {
        return String.format("BK:%s|USR:%s|MOV:%s|THR:%s|SCR:%s|TIME:%s|SEATS:%d",
                booking.getBookingReference(),
                booking.getUser().getFullName(),
                booking.getMovie().getTitle(),
                booking.getTheater().getName(),
                booking.getScreen().getScreenName(),
                booking.getShowtime().getShowDatetime(),
                booking.getNumSeats());
    }

    private String generateHMAC(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(HMAC_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes).substring(0, 16);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC", e);
        }
    }
}
