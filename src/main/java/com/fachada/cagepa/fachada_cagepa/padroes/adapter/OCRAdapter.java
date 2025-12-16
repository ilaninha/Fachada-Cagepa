package com.fachada.cagepa.fachada_cagepa.padroes.adapter;

import com.fachada.cagepa.fachada_cagepa.padroes.strategy.ColaboradorStrategy;
import com.fachada.cagepa.fachada_cagepa.padroes.strategy.ProprietarioStrategy;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class OCRAdapter {

    private final ProprietarioStrategy proprietarioStrategy;
    private final ColaboradorStrategy colaboradorStrategy;

    public OCRAdapter(ProprietarioStrategy proprietarioStrategy, ColaboradorStrategy colaboradorStrategy) {
        this.proprietarioStrategy = proprietarioStrategy;
        this.colaboradorStrategy = colaboradorStrategy;
    }

    public String extractMeterId(String filename) {
        return proprietarioStrategy.extractMeterId(filename);
    }

    public Long tryExtractMeterValue(BufferedImage image) {
        Long proprietarioValue = proprietarioStrategy.extractMeterValue(image);
        if (proprietarioValue != null) {
            return proprietarioValue;
        }

        Long colaboradorValue = colaboradorStrategy.extractMeterValue(image);
        if (colaboradorValue != null) {
            return colaboradorValue;
        }

        return null;
    }

    public String determineMeterType(BufferedImage image) {
        Long proprietarioValue = proprietarioStrategy.extractMeterValue(image);
        if (proprietarioValue != null) {
            return proprietarioStrategy.getMeterType();
        }

        Long colaboradorValue = colaboradorStrategy.extractMeterValue(image);
        if (colaboradorValue != null) {
            return colaboradorStrategy.getMeterType();
        }

        return "DESCONHECIDO";
    }
}
