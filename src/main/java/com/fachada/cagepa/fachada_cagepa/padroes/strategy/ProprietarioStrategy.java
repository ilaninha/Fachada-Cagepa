package com.fachada.cagepa.fachada_cagepa.padroes.strategy;

import com.fachada.cagepa.fachada_cagepa.padroes.config.TesseractConfig;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class ProprietarioStrategy implements HidrometroOCRStrategy {

    private final Tesseract tesseract;

    public ProprietarioStrategy() {
        this.tesseract = TesseractConfig.getInstance().getTesseract();
    }

    @Override
    public String extractMeterId(String filename) {
        // Remove extensões de arquivo (png, jpeg, jpg, etc)
        String nameWithoutExtension = filename.replaceAll("\\.(png|jpeg|jpg|PNG|JPEG|JPG)$", "");
        // Remove caracteres especiais mantendo apenas alfanuméricos
        String cleaned = nameWithoutExtension.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return cleaned;
    }

    @Override
    public Long extractMeterValue(BufferedImage image) {
        try {
            BufferedImage roi = image.getSubimage(340, 240, 240, 60);
            String result = tesseract.doOCR(roi);
            String cleaned = result.replaceAll("[^0-9]", "").trim();

            if (cleaned.isEmpty()) {
                return null;
            }

            return Long.parseLong(cleaned);
        } catch (TesseractException | RuntimeException e) {
            return null;
        }
    }

    @Override
    public String getMeterType() {
        return "PROPRIETARIO";
    }
}
