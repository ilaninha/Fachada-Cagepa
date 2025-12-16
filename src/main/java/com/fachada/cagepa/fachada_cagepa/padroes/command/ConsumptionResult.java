package com.fachada.cagepa.fachada_cagepa.padroes.command;

/**
 * DTO para representar o resultado do cálculo de consumo.
 * Encapsula informações sobre consumo e período.
 */
public class ConsumptionResult {
    private final String shaHidrometro;
    private final String periodDescription;
    private final Long consumptionValue;
    private final java.time.LocalDateTime dataInicio;
    private final java.time.LocalDateTime dataFim;
    private final Long leituraInicial;
    private final Long leituraFinal;
    
    public ConsumptionResult(String shaHidrometro, String periodDescription, Long consumptionValue,
                            java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim,
                            Long leituraInicial, Long leituraFinal) {
        this.shaHidrometro = shaHidrometro;
        this.periodDescription = periodDescription;
        this.consumptionValue = consumptionValue;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.leituraInicial = leituraInicial;
        this.leituraFinal = leituraFinal;
    }
    
    public String getShaHidrometro() {
        return shaHidrometro;
    }
    
    public String getPeriodDescription() {
        return periodDescription;
    }
    
    public Long getConsumptionValue() {
        return consumptionValue;
    }
    
    public java.time.LocalDateTime getDataInicio() {
        return dataInicio;
    }
    
    public java.time.LocalDateTime getDataFim() {
        return dataFim;
    }
    
    public Long getLeituraInicial() {
        return leituraInicial;
    }
    
    public Long getLeituraFinal() {
        return leituraFinal;
    }
    
    @Override
    public String toString() {
        return "ConsumptionResult{" +
                "sha='" + shaHidrometro + '\'' +
                ", period='" + periodDescription + '\'' +
                ", consumption=" + consumptionValue + " m³" +
                ", from=" + dataInicio +
                ", to=" + dataFim +
                ", initialReading=" + leituraInicial +
                ", finalReading=" + leituraFinal +
                '}';
    }
}
