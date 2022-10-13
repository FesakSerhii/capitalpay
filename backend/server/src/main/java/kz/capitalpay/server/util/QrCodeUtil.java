package kz.capitalpay.server.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class QrCodeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeUtil.class);
    private final QrGenerator qrGenerator;

    public QrCodeUtil(QrGenerator qrGenerator) {
        this.qrGenerator = qrGenerator;
    }

    public String generateQrCode(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToStream(bitMatrix, "png", bos);
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }
        return Utils.getDataUriForImage(bos.toByteArray(), qrGenerator.getImageMimeType());
    }
}
