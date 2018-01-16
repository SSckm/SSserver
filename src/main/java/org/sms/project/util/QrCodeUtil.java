package org.sms.project.util;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.sms.project.qrcode.MatrixToImageWriter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrCodeUtil {

	/**
	 * @param text
	 * @param width
	 * @param height
	 * @param format
	 * @param pathName
	 * @return
	 * @throws Exception
	 */
	public static String generateQRCode(String text, int width, int height, String format, String pathName) {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix;
		try {
			bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
			File outputFile = new File(pathName);
	        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
	        return pathName;
		} catch (WriterException | IOException e) {
			e.printStackTrace();
			return null;
		}
    }
}
