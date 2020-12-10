package com.payline.payment.wechatpay.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

public class QRCodeService {

    // --- Singleton Holder pattern + initialization BEGIN
    private QRCodeService() {
    }

    private static class Holder {
        private static final QRCodeService instance = new QRCodeService();
    }

    public static QRCodeService getInstance() {
        return QRCodeService.Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END

    public BufferedImage generateMatrix(String data, int size) throws WriterException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
