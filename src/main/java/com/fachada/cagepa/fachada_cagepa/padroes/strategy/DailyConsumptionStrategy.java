package com.fachada.cagepa.fachada_cagepa.padroes.strategy;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

/**
 * Estratégia para cálculo de consumo DIÁRIO.
 * Calcula o consumo desde o início do dia até agora.
 */
public class DailyConsumptionStrategy implements ConsumptionCalculationStrategy {
    
    @Override
    public Long calculateConsumption(List<Long> leituras) {
        if (leituras == null || leituras.size() < 2) {
            return 0L;
        }
        
        // Consumo = última leitura - primeira leitura do dia
        Long ultimaLeitura = leituras.get(leituras.size() - 1);
        Long primeiraLeitura = leituras.get(0);
        
        return ultimaLeitura - primeiraLeitura;
    }
    
    @Override
    public String getPeriodDescription() {
        return "Diária";
    }
    
    @Override
    public LocalDateTime[] getDateRange() {
        LocalDate hoje = LocalDate.now();
        return new LocalDateTime[] {
            hoje.atStartOfDay(),
            hoje.atTime(23, 59, 59)
        };
    }
}
