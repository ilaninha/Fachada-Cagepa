package com.fachada.cagepa.fachada_cagepa.padroes.strategy;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;

/**
 * Estratégia para cálculo de consumo SEMANAL.
 * Calcula o consumo desde a segunda-feira até hoje.
 */
public class WeeklyConsumptionStrategy implements ConsumptionCalculationStrategy {
    
    @Override
    public Long calculateConsumption(List<Long> leituras) {
        if (leituras == null || leituras.size() < 2) {
            return 0L;
        }
        
        // Consumo = última leitura - primeira leitura da semana
        Long ultimaLeitura = leituras.get(leituras.size() - 1);
        Long primeiraLeitura = leituras.get(0);
        
        return ultimaLeitura - primeiraLeitura;
    }
    
    @Override
    public String getPeriodDescription() {
        return "Semanal";
    }
    
    @Override
    public LocalDateTime[] getDateRange() {
        LocalDate hoje = LocalDate.now();
        
        // Obtém o primeiro dia da semana (segunda-feira)
        LocalDate inicioSemana = hoje;
        while (inicioSemana.getDayOfWeek() != DayOfWeek.MONDAY) {
            inicioSemana = inicioSemana.minusDays(1);
        }
        
        return new LocalDateTime[] {
            inicioSemana.atStartOfDay(),
            hoje.atTime(23, 59, 59)
        };
    }
}
