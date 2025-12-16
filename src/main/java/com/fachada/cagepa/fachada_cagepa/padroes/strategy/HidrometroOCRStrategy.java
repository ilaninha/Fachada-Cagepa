package com.fachada.cagepa.fachada_cagepa.padroes.strategy;

import java.awt.image.BufferedImage;

public interface HidrometroOCRStrategy {
    String extractMeterId(String filename);
    Long extractMeterValue(BufferedImage image);
    String getMeterType();
}
