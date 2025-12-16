package com.fachada.cagepa.fachada_cagepa.padroes.service;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Hidrometro;
import com.fachada.cagepa.fachada_cagepa.infra.entities.LeituraDoHidrometro;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.HidrometroRepository;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.LeituraDoHidrometroRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.command.ConsumptionResult;
import com.fachada.cagepa.fachada_cagepa.padroes.strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço para cálculo de consumo de água em diferentes períodos.
 * 
 * Responsabilidades:
 * - Calcular consumo individual de um hidrômetro
 * - Calcular consumo total de um cliente (todos os hidrômetros)
 * - Suportar cálculos em múltiplos períodos (diário, semanal, mensal, anual)
 */
@Service
@Transactional(readOnly = true)
public class ConsumptionCalculationService {
    
    private final LeituraDoHidrometroRepository leituraRepository;
    private final HidrometroRepository hidrometroRepository;
    
    // Strategies para cada tipo de período (lazy initialization)
    private Map<String, ConsumptionCalculationStrategy> strategies;
    
    @Autowired
    public ConsumptionCalculationService(LeituraDoHidrometroRepository leituraRepository,
                                        HidrometroRepository hidrometroRepository) {
        this.leituraRepository = leituraRepository;
        this.hidrometroRepository = hidrometroRepository;
        this.strategies = null; // Inicializa lazy
    }
    
    /**
     * Inicializa as estratégias de cálculo (lazy initialization).
     */
    private void initializeStrategies() {
        if (strategies == null) {
            strategies = new HashMap<>();
            try {
                strategies.put("daily", new DailyConsumptionStrategy());
                strategies.put("weekly", new WeeklyConsumptionStrategy());
                strategies.put("monthly", new MonthlyConsumptionStrategy());
                strategies.put("annual", new AnnualConsumptionStrategy());
            } catch (Exception e) {
                System.err.println("Erro ao inicializar estrategias de consumo: " + e.getMessage());
                strategies = new HashMap<>(); // Garante que não seja null
            }
        }
    }
    
    /**
     * Calcula o consumo de um hidrômetro específico em um período.
     * 
     * @param sha Identificador único do hidrômetro
     * @param periodType Tipo de período (daily, weekly, monthly, annual)
     * @return Resultado do cálculo com detalhes
     */
    public ConsumptionResult calculateIndividualConsumption(String sha, String periodType) {
        initializeStrategies();
        ConsumptionCalculationStrategy strategy = getStrategy(periodType);
        LocalDateTime[] dateRange = strategy.getDateRange();
        
        // Busca leituras no período
        List<LeituraDoHidrometro> leituras = leituraRepository.findByPeriodAndSha(
            sha, dateRange[0], dateRange[1]
        );
        
        if (leituras.isEmpty()) {
            return new ConsumptionResult(
                sha, strategy.getPeriodDescription(), 0L,
                dateRange[0], dateRange[1], 0L, 0L
            );
        }
        
        // Extrai valores de leitura
        List<Long> valores = leituras.stream()
            .map(LeituraDoHidrometro::getValorLeitura)
            .collect(Collectors.toList());
        
        Long consumo = strategy.calculateConsumption(valores);
        
        return new ConsumptionResult(
            sha, strategy.getPeriodDescription(), consumo,
            dateRange[0], dateRange[1],
            valores.get(0),
            valores.get(valores.size() - 1)
        );
    }
    
    /**
     * Calcula o consumo de um hidrômetro em todos os períodos suportados.
     * 
     * @param sha Identificador único do hidrômetro
     * @return Mapa com resultado para cada período
     */
    public Map<String, ConsumptionResult> calculateAllPeriods(String sha) {
        initializeStrategies();
        Map<String, ConsumptionResult> results = new LinkedHashMap<>();
        
        for (String periodType : strategies.keySet()) {
            results.put(periodType, calculateIndividualConsumption(sha, periodType));
        }
        
        return results;
    }
    
    /**
     * Calcula o consumo total de um cliente somando todos os seus hidrômetros.
     * 
     * @param pessoaFisicaId ID da Pessoa Física
     * @param periodType Tipo de período
     * @return Resultado agregado do consumo total
     */
    public ConsumptionResult calculateClientConsumption(Long pessoaFisicaId, String periodType) {
        initializeStrategies();
        List<Hidrometro> hidrometros = hidrometroRepository.findByPessoaFisicaId(pessoaFisicaId);
        
        ConsumptionCalculationStrategy strategy = getStrategy(periodType);
        LocalDateTime[] dateRange = strategy.getDateRange();
        
        Long totalConsumption = 0L;
        
        for (Hidrometro hidrometro : hidrometros) {
            List<LeituraDoHidrometro> leituras = leituraRepository.findByPeriodAndSha(
                hidrometro.getSha(), dateRange[0], dateRange[1]
            );
            
            if (!leituras.isEmpty()) {
                List<Long> valores = leituras.stream()
                    .map(LeituraDoHidrometro::getValorLeitura)
                    .collect(Collectors.toList());
                
                totalConsumption += strategy.calculateConsumption(valores);
            }
        }
        
        return new ConsumptionResult(
            "CLIENTE_" + pessoaFisicaId,
            "Total de todos os hidrômetros - " + strategy.getPeriodDescription(),
            totalConsumption,
            dateRange[0], dateRange[1],
            0L, 0L
        );
    }
    
    /**
     * Calcula o consumo total de um cliente em todos os períodos.
     * 
     * @param pessoaFisicaId ID da Pessoa Física
     * @return Mapa com resultado para cada período
     */
    public Map<String, ConsumptionResult> calculateClientAllPeriods(Long pessoaFisicaId) {
        initializeStrategies();
        Map<String, ConsumptionResult> results = new LinkedHashMap<>();
        
        for (String periodType : strategies.keySet()) {
            results.put(periodType, calculateClientConsumption(pessoaFisicaId, periodType));
        }
        
        return results;
    }
    
    /**
     * Calcula o consumo total de uma empresa (Pessoa Jurídica) somando todos os seus hidrômetros.
     * 
     * @param pessoaJuridicaId ID da Pessoa Jurídica
     * @param periodType Tipo de período
     * @return Resultado agregado do consumo total
     */
    public ConsumptionResult calculateCompanyConsumption(Long pessoaJuridicaId, String periodType) {
        initializeStrategies();
        List<Hidrometro> hidrometros = hidrometroRepository.findByPessoaJuridicaId(pessoaJuridicaId);
        
        ConsumptionCalculationStrategy strategy = getStrategy(periodType);
        LocalDateTime[] dateRange = strategy.getDateRange();
        
        Long totalConsumption = 0L;
        
        for (Hidrometro hidrometro : hidrometros) {
            List<LeituraDoHidrometro> leituras = leituraRepository.findByPeriodAndSha(
                hidrometro.getSha(), dateRange[0], dateRange[1]
            );
            
            if (!leituras.isEmpty()) {
                List<Long> valores = leituras.stream()
                    .map(LeituraDoHidrometro::getValorLeitura)
                    .collect(Collectors.toList());
                
                totalConsumption += strategy.calculateConsumption(valores);
            }
        }
        
        return new ConsumptionResult(
            "EMPRESA_" + pessoaJuridicaId,
            "Total de todos os hidrômetros - " + strategy.getPeriodDescription(),
            totalConsumption,
            dateRange[0], dateRange[1],
            0L, 0L
        );
    }
    
    /**
     * Calcula o consumo total de uma empresa em todos os períodos.
     * 
     * @param pessoaJuridicaId ID da Pessoa Jurídica
     * @return Mapa com resultado para cada período
     */
    public Map<String, ConsumptionResult> calculateCompanyAllPeriods(Long pessoaJuridicaId) {
        initializeStrategies();
        Map<String, ConsumptionResult> results = new LinkedHashMap<>();
        
        for (String periodType : strategies.keySet()) {
            results.put(periodType, calculateCompanyConsumption(pessoaJuridicaId, periodType));
        }
        
        return results;
    }
    
    /**
     * Obtém a estratégia de cálculo para um período específico.
     * 
     * @param periodType Tipo de período
     * @return Strategy ou lança exceção se não encontrada
    /**
     * Obtém a estratégia de cálculo para um período específico.
     * 
     * @param periodType Tipo de período
     * @return Strategy ou lança exceção se não encontrada
     */
    private ConsumptionCalculationStrategy getStrategy(String periodType) {
        initializeStrategies();
        ConsumptionCalculationStrategy strategy = strategies.get(periodType.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException(
                "Tipo de período inválido: " + periodType + 
                ". Periodos suportados: " + strategies.keySet()
            );
        }
        return strategy;
    }
    
    /**
     * Lista todos os períodos suportados.
     */
    public List<String> getSupportedPeriods() {
        initializeStrategies();
        return new ArrayList<>(strategies.keySet());
    }
    
    /**
     * Obtém comparativo de consumo entre dois períodos para um hidrômetro.
     * Útil para análise de tendências.
     * 
     * @param sha Identificador do hidrômetro
     * @param periodType1 Primeiro período
     * @param periodType2 Segundo período
     * @return Array com [consumoPeriodo1, consumoPeriodo2, percentualVariacao]
     */
    public Long[] compareConsumption(String sha, String periodType1, String periodType2) {
        ConsumptionResult result1 = calculateIndividualConsumption(sha, periodType1);
        ConsumptionResult result2 = calculateIndividualConsumption(sha, periodType2);
        
        Long consumo1 = result1.getConsumptionValue();
        Long consumo2 = result2.getConsumptionValue();
        
        Long variacao = consumo2 - consumo1;
        
        return new Long[] { consumo1, consumo2, variacao };
    }
}
