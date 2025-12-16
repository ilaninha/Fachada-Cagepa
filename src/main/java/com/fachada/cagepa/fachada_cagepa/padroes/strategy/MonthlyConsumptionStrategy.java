package com.fachada.cagepa.fachada_cagepa.padroes.strategy;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

/**
 * Estratégia para cálculo de consumo MENSAL.
 * Calcula o consumo desde o primeiro dia do mês até hoje.
 */
public class MonthlyConsumptionStrategy implements ConsumptionCalculationStrategy {
    
    @Override
    public Long calculateConsumption(List<Long> leituras) {
        if (leituras == null || leituras.size() < 2) {
            return 0L;
        }
        
        // Consumo = última leitura - primeira leitura do mês
        Long ultimaLeitura = leituras.get(leituras.size() - 1);
        Long primeiraLeitura = leituras.get(0);
        
        return ultimaLeitura - primeiraLeitura;
    }
    
    @Override
    public String getPeriodDescription() {
        return "Mensal";
    }
    
    @Override
    public LocalDateTime[] getDateRange() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        
        return new LocalDateTime[] {
            inicioMes.atStartOfDay(),
            hoje.atTime(23, 59, 59)
        };
    }
}
