package com.payline.wechatpay.service.impl;

import com.payline.payment.wechatpay.wechatpay.service.QRCodeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

class QRCodeServiceTest {
    private QRCodeService qrCodeService = QRCodeService.getInstance();

    @Test
    void generateMatrixOk() {
        BufferedImage image = qrCodeService.generateMatrix("test", 300);
        Assertions.assertNotNull(image);
    }}