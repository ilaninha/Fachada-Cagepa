package com.fachada.cagepa.fachada_cagepa.padroes.strategy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Strategy para cálculo de consumo de água em diferentes períodos.
 * Implementa diferentes estratégias de cálculo: diária, semanal, mensal e anual.
 */
public interface ConsumptionCalculationStrategy {
    
    /**
     * Calcula o consumo baseado na estratégia específica.
     * 
     * @param leituras Lista de leituras do hidrômetro ordenadas cronologicamente
     * @return Consumo total no período
     */
    Long calculateConsumption(List<Long> leituras);
    
    /**
     * Obtém a descrição do período de cálculo.
     * 
     * @return Descrição legível do período (ex: "Diária", "Semanal")
     */
    String getPeriodDescription();
    
    /**
     * Obtém o intervalo de datas para filtro.
     * 
     * @return Array com [dataInicio, dataFim]
     */
    LocalDateTime[] getDateRange();
}
