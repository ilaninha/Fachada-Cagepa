package com.fachada.cagepa.fachada_cagepa.padroes.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Component;

@Component
public class TesseractConfig {

    private static TesseractConfig instance;
    private final Tesseract tesseract;

    private TesseractConfig() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
        this.tesseract.setLanguage("por");
    }

    public static synchronized TesseractConfig getInstance() {
        if (instance == null) {
            instance = new TesseractConfig();
        }
        return instance;
    }

    public Tesseract getTesseract() {
        return tesseract;
    }
}
