package com.fachada.cagepa.fachada_cagepa.padroes.strategy;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

/**
 * Estratégia para cálculo de consumo ANUAL.
 * Calcula o consumo desde o primeiro dia do ano até hoje.
 */
public class AnnualConsumptionStrategy implements ConsumptionCalculationStrategy {
    
    @Override
    public Long calculateConsumption(List<Long> leituras) {
        if (leituras == null || leituras.size() < 2) {
            return 0L;
        }
        
        // Consumo = última leitura - primeira leitura do ano
        Long ultimaLeitura = leituras.get(leituras.size() - 1);
        Long primeiraLeitura = leituras.get(0);
        
        return ultimaLeitura - primeiraLeitura;
    }
    
    @Override
    public String getPeriodDescription() {
        return "Anual";
    }
    
    @Override
    public LocalDateTime[] getDateRange() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioAno = LocalDate.of(hoje.getYear(), 1, 1);
        
        return new LocalDateTime[] {
            inicioAno.atStartOfDay(),
            hoje.atTime(23, 59, 59)
        };
    }
}
