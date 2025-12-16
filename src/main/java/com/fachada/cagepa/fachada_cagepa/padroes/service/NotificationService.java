package com.fachada.cagepa.fachada_cagepa.padroes.service;

import com.fachada.cagepa.fachada_cagepa.infra.entities.*;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.ConfiguracaoLimiteConsumoRepository;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.HistoricoNotificacaoRepository;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.HidrometroRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.command.ConsumptionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciar notificações de limite de consumo.
 * 
 * Responsabilidades:
 * - Configurar limites de consumo por hidrômetro
 * - Detectar quando o consumo atinge um percentual do limite
 * - Enviar notificações por email (mock)
 * - Registrar histórico de notificações
 * - Consultar histórico de notificações
 * - Evitar duplicatas no mesmo dia
 */
@Service
@Transactional
public class NotificationService {
    
    private final ConfiguracaoLimiteConsumoRepository configuracaoRepository;
    private final HistoricoNotificacaoRepository historicoRepository;
    private final HidrometroRepository hidrometroRepository;
    private final ConsumptionCalculationService consumptionCalculationService;
    
    @Autowired
    public NotificationService(ConfiguracaoLimiteConsumoRepository configuracaoRepository,
                              HistoricoNotificacaoRepository historicoRepository,
                              HidrometroRepository hidrometroRepository,
                              ConsumptionCalculationService consumptionCalculationService) {
        this.configuracaoRepository = configuracaoRepository;
        this.historicoRepository = historicoRepository;
        this.hidrometroRepository = hidrometroRepository;
        this.consumptionCalculationService = consumptionCalculationService;
    }
    
    /**
     * Configura limite de consumo para um hidrômetro
     */
    public ConfiguracaoLimiteConsumo configurarLimite(Long hidrometroId, Long limiteConsumMensal, Integer percentualLimite) {
        Hidrometro hidrometro = hidrometroRepository.findById(hidrometroId)
            .orElseThrow(() -> new IllegalArgumentException("Hidrômetro não encontrado: " + hidrometroId));
        
        ConfiguracaoLimiteConsumo configuracao = configuracaoRepository.findByHidrometro(hidrometro)
            .orElse(new ConfiguracaoLimiteConsumo());
        
        configuracao.setHidrometro(hidrometro);
        configuracao.setLimiteConsumMensal(limiteConsumMensal);
        configuracao.setPercentualLimite(percentualLimite != null ? percentualLimite : 70);
        configuracao.setAtivo(true);
        
        return configuracaoRepository.save(configuracao);
    }
    
    /**
     * Verifica consumo mensal e envia notificação se necessário
     */
    public void verificarENotificar(String sha) {
        try {
            Hidrometro hidrometro = hidrometroRepository.findBySha(sha)
                .orElseThrow(() -> new IllegalArgumentException("Hidrômetro não encontrado: " + sha));
            
            // Busca configuração de limite
            Optional<ConfiguracaoLimiteConsumo> configOpt = configuracaoRepository.findByHidrometro(hidrometro);
            if (configOpt.isEmpty() || !configOpt.get().getAtivo()) {
                return; // Sem configuração ou desativado
            }
            
            ConfiguracaoLimiteConsumo config = configOpt.get();
            
            // Calcula consumo mensal
            ConsumptionResult consumoMensal = consumptionCalculationService.calculateIndividualConsumption(sha, "monthly");
            Long consumoAtual = consumoMensal.getConsumptionValue();
            Long limiteConfigurado = config.getLimiteConsumMensal();
            Integer percentualLimite = config.getPercentualLimite();
            
            // Calcula percentual atingido
            Integer percentualAtingido = (int) ((consumoAtual * 100) / limiteConfigurado);
            
            // Verifica se atingiu o percentual configurado
            if (percentualAtingido >= percentualLimite) {
                // Verifica se já foi notificado hoje
                LocalDate hoje = LocalDate.now();
                Optional<HistoricoNotificacao> jaNotificado = historicoRepository.findByHidrometroAndData(hidrometro.getId(), hoje);
                
                if (jaNotificado.isEmpty()) {
                    // Envia notificação
                    String email = obterEmailCliente(hidrometro);
                    if (email != null && !email.isEmpty()) {
                        enviarNotificacao(hidrometro, email, consumoAtual, limiteConfigurado, percentualAtingido);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar consumo para notificação: " + e.getMessage());
        }
    }
    
    /**
     * Envia notificação por email (mock)
     */
    private void enviarNotificacao(Hidrometro hidrometro, String email, Long consumoAtual, Long limiteConfigurado, Integer percentualAtingido) {
        try {
            // Mock de envio de email
            String mensagem = String.format(
                "Email enviado com sucesso para '%s': Consumo mensal de %d m³ atingiu %d%% do limite de %d m³",
                email, consumoAtual, percentualAtingido, limiteConfigurado
            );
            
            System.out.println("✓ " + mensagem);
            
            // Registra no histórico
            PessoaFisica pessoaFisica = hidrometro.getPessoaFisica();
            PessoaJuridica pessoaJuridica = hidrometro.getPessoaJuridica();
            
            HistoricoNotificacao historico = HistoricoNotificacao.builder()
                .hidrometro(hidrometro)
                .pessoaFisica(pessoaFisica)
                .pessoaJuridica(pessoaJuridica)
                .emailDestino(email)
                .consumoMensal(consumoAtual)
                .limitConfigurado(limiteConfigurado)
                .percentualAtingido(percentualAtingido)
                .dataEnvio(LocalDateTime.now())
                .dataEvento(LocalDate.now())
                .statusEnvio("ENVIADO")
                .mensagem(mensagem)
                .build();
            
            historicoRepository.save(historico);
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
            
            // Registra como falha
            HistoricoNotificacao historico = HistoricoNotificacao.builder()
                .hidrometro(hidrometro)
                .pessoaFisica(hidrometro.getPessoaFisica())
                .pessoaJuridica(hidrometro.getPessoaJuridica())
                .emailDestino(email)
                .consumoMensal(consumoAtual)
                .limitConfigurado(limiteConfigurado)
                .percentualAtingido(percentualAtingido)
                .dataEnvio(LocalDateTime.now())
                .dataEvento(LocalDate.now())
                .statusEnvio("FALHA")
                .mensagem("Erro ao enviar: " + e.getMessage())
                .build();
            
            historicoRepository.save(historico);
        }
    }
    
    /**
     * Obtém email do cliente associado ao hidrômetro
     */
    private String obterEmailCliente(Hidrometro hidrometro) {
        if (hidrometro.getPessoaFisica() != null) {
            return hidrometro.getPessoaFisica().getEmail();
        } else if (hidrometro.getPessoaJuridica() != null) {
            return hidrometro.getPessoaJuridica().getEmail();
        }
        return null;
    }
    
    /**
     * Consulta histórico de notificações para um hidrômetro
     */
    @Transactional(readOnly = true)
    public List<HistoricoNotificacao> consultarHistoricoHidrometro(Long hidrometroId) {
        return historicoRepository.findByHidrometroId(hidrometroId);
    }
    
    /**
     * Consulta histórico de notificações para um cliente (Pessoa Física)
     */
    @Transactional(readOnly = true)
    public List<HistoricoNotificacao> consultarHistoricoPessoaFisica(Long pessoaFisicaId) {
        return historicoRepository.findByPessoaFisicaId(pessoaFisicaId);
    }
    
    /**
     * Consulta histórico de notificações para um cliente (Pessoa Jurídica)
     */
    @Transactional(readOnly = true)
    public List<HistoricoNotificacao> consultarHistoricoPessoaJuridica(Long pessoaJuridicaId) {
        return historicoRepository.findByPessoaJuridicaId(pessoaJuridicaId);
    }
    
    /**
     * Obtém configuração de limite para um hidrômetro
     */
    @Transactional(readOnly = true)
    public ConfiguracaoLimiteConsumo obterConfiguracao(Long hidrometroId) {
        Hidrometro hidrometro = hidrometroRepository.findById(hidrometroId)
            .orElseThrow(() -> new IllegalArgumentException("Hidrômetro não encontrado: " + hidrometroId));
        
        return configuracaoRepository.findByHidrometro(hidrometro)
            .orElseThrow(() -> new IllegalArgumentException("Nenhuma configuração de limite para este hidrômetro"));
    }
    
    /**
     * Desativa notificações para um hidrômetro
     */
    public void desativarNotificacoes(Long hidrometroId) {
        Hidrometro hidrometro = hidrometroRepository.findById(hidrometroId)
            .orElseThrow(() -> new IllegalArgumentException("Hidrômetro não encontrado: " + hidrometroId));
        
        Optional<ConfiguracaoLimiteConsumo> configOpt = configuracaoRepository.findByHidrometro(hidrometro);
        if (configOpt.isPresent()) {
            ConfiguracaoLimiteConsumo config = configOpt.get();
            config.setAtivo(false);
            configuracaoRepository.save(config);
        }
    }
    
    /**
     * Reativa notificações para um hidrômetro
     */
    public void reativarNotificacoes(Long hidrometroId) {
        Hidrometro hidrometro = hidrometroRepository.findById(hidrometroId)
            .orElseThrow(() -> new IllegalArgumentException("Hidrômetro não encontrado: " + hidrometroId));
        
        Optional<ConfiguracaoLimiteConsumo> configOpt = configuracaoRepository.findByHidrometro(hidrometro);
        if (configOpt.isPresent()) {
            ConfiguracaoLimiteConsumo config = configOpt.get();
            config.setAtivo(true);
            configuracaoRepository.save(config);
        }
    }
}
