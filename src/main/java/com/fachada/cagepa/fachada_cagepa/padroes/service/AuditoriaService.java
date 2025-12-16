package com.fachada.cagepa.fachada_cagepa.padroes.service;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Administrador;
import com.fachada.cagepa.fachada_cagepa.infra.entities.AuditoriaOperacao;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.AuditoriaRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.observer.AuditoriaObserver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia registro de operações e notifica observers sobre mudanças.
 */
@Service
@Transactional
public class AuditoriaService {
    
    @Autowired
    private AuditoriaRepository auditoriaRepository;
    
    private final List<AuditoriaObserver> observers = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Registra um observer para ser notificado sobre operações
     */
    public synchronized void registrarObserver(AuditoriaObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Remove um observer
     */
    public synchronized void removerObserver(AuditoriaObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifica todos os observers de uma operação registrada
     */
    private void notificarObservers(AuditoriaOperacao operacao) {
        for (AuditoriaObserver observer : observers) {
            if (operacao.getCritica()) {
                observer.operacaoCritica(operacao);
            } else {
                observer.operacaoRegistrada(operacao);
            }
        }
    }
    
    /**
     * Notifica observers de uma operação que falhou
     */
    private void notificarFalha(AuditoriaOperacao operacao) {
        for (AuditoriaObserver observer : observers) {
            observer.operacaoFalhou(operacao);
        }
    }
    
    /**
     * Registra uma operação CREATE
     */
    public AuditoriaOperacao registrarCriacaoEntidade(Administrador administrador, String tipoEntidade, 
                                                       Long entidadeId, String descricao, Object dadosNovos) {
        return registrarOperacao(administrador, "CREATE", tipoEntidade, entidadeId, descricao, null, dadosNovos, true);
    }
    
    /**
     * Registra uma operação READ
     */
    public AuditoriaOperacao registrarLeituraEntidade(Administrador administrador, String tipoEntidade, 
                                                       Long entidadeId, String descricao) {
        return registrarOperacao(administrador, "READ", tipoEntidade, entidadeId, descricao, null, null, false);
    }
    
    /**
     * Registra uma operação UPDATE
     */
    public AuditoriaOperacao registrarAtualizacaoEntidade(Administrador administrador, String tipoEntidade, 
                                                          Long entidadeId, String descricao, Object dadosAntigos, 
                                                          Object dadosNovos) {
        return registrarOperacao(administrador, "UPDATE", tipoEntidade, entidadeId, descricao, dadosAntigos, dadosNovos, true);
    }
    
    /**
     * Registra uma operação DELETE
     */
    public AuditoriaOperacao registrarDelecaoEntidade(Administrador administrador, String tipoEntidade, 
                                                       Long entidadeId, String descricao, Object dadosAntigos) {
        return registrarOperacao(administrador, "DELETE", tipoEntidade, entidadeId, descricao, dadosAntigos, null, true);
    }
    
    /**
     * Registra um LOGIN
     */
    public AuditoriaOperacao registrarLogin(Administrador administrador, String descricao) {
        AuditoriaOperacao operacao = AuditoriaOperacao.builder()
            .administrador(administrador)
            .dataOperacao(LocalDateTime.now())
            .tipoOperacao("LOGIN")
            .tipoEntidade("Administrador")
            .entidadeId(administrador.getId())
            .descricao(descricao)
            .resultadoOperacao("SUCESSO")
            .critica(true)
            .build();
        
        AuditoriaOperacao salva = auditoriaRepository.save(operacao);
        notificarObservers(salva);
        return salva;
    }
    
    /**
     * Registra uma mudança de configuração
     */
    public AuditoriaOperacao registrarMudancaConfiguracao(Administrador administrador, String tipoEntidade,
                                                          Long entidadeId, String descricao, Object dadosAntigos,
                                                          Object dadosNovos) {
        return registrarOperacao(administrador, "CONFIG_CHANGE", tipoEntidade, entidadeId, descricao, dadosAntigos, dadosNovos, true);
    }
    
    /**
     * Registra uma operação genérica com controle total
     */
    public AuditoriaOperacao registrarOperacao(Administrador administrador, String tipoOperacao, 
                                               String tipoEntidade, Long entidadeId, String descricao,
                                               Object dadosAntigos, Object dadosNovos, boolean critica) {
        try {
            AuditoriaOperacao operacao = AuditoriaOperacao.builder()
                .administrador(administrador)
                .dataOperacao(LocalDateTime.now())
                .tipoOperacao(tipoOperacao)
                .tipoEntidade(tipoEntidade)
                .entidadeId(entidadeId)
                .descricao(descricao)
                .dadosAnteriores(serializarDados(dadosAntigos))
                .dadosNovos(serializarDados(dadosNovos))
                .resultadoOperacao("SUCESSO")
                .critica(critica)
                .build();
            
            AuditoriaOperacao salva = auditoriaRepository.save(operacao);
            notificarObservers(salva);
            return salva;
        } catch (Exception e) {
            return registrarFalha(administrador, tipoOperacao, tipoEntidade, entidadeId, 
                    descricao, e.getMessage(), critica);
        }
    }
    
    /**
     * Registra uma operação que falhou
     */
    public AuditoriaOperacao registrarFalha(Administrador administrador, String tipoOperacao,
                                           String tipoEntidade, Long entidadeId, String descricao,
                                           String mensagemErro, boolean critica) {
        AuditoriaOperacao operacao = AuditoriaOperacao.builder()
            .administrador(administrador)
            .dataOperacao(LocalDateTime.now())
            .tipoOperacao(tipoOperacao)
            .tipoEntidade(tipoEntidade)
            .entidadeId(entidadeId)
            .descricao(descricao)
            .resultadoOperacao("FALHA")
            .mensagemErro(mensagemErro)
            .critica(critica)
            .build();
        
        AuditoriaOperacao salva = auditoriaRepository.save(operacao);
        notificarFalha(salva);
        return salva;
    }
    
    /**
     * Serializa objeto para JSON
     */
    private String serializarDados(Object dados) {
        if (dados == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(dados);
        } catch (Exception e) {
            return dados.toString();
        }
    }
    
    // ===== MÉTODOS DE CONSULTA =====
    
    /**
     * Obtém operações de um administrador
     */
    public Page<AuditoriaOperacao> obterOperacoesAdministrador(Long administradorId, Pageable pageable) {
        return auditoriaRepository.findByAdministradorId(administradorId, pageable);
    }
    
    /**
     * Obtém operações de um tipo específico
     */
    public Page<AuditoriaOperacao> obterOperacoesPorTipo(String tipoOperacao, Pageable pageable) {
        return auditoriaRepository.findByTipoOperacao(tipoOperacao, pageable);
    }
    
    /**
     * Obtém operações de uma entidade específica
     */
    public Page<AuditoriaOperacao> obterOperacoesEntidade(String tipoEntidade, Long entidadeId, Pageable pageable) {
        return auditoriaRepository.findByTipoEntidadeAndEntidadeId(tipoEntidade, entidadeId, pageable);
    }
    
    /**
     * Obtém operações entre duas datas
     */
    public Page<AuditoriaOperacao> obterOperacoesPeríodo(LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        return auditoriaRepository.findByDataOperacaoBetween(dataInicio, dataFim, pageable);
    }
    
    /**
     * Obtém operações críticas de um administrador
     */
    public Page<AuditoriaOperacao> obterOperacoesCriticas(Long administradorId, Pageable pageable) {
        return auditoriaRepository.findByAdministradorIdAndCriticaTrue(administradorId, pageable);
    }
    
    /**
     * Obtém operações que falharam
     */
    public Page<AuditoriaOperacao> obterOperacoesFalhadas(Pageable pageable) {
        return auditoriaRepository.findFalhas(pageable);
    }
    
    /**
     * Obtém últimas operações de um administrador
     */
    public List<AuditoriaOperacao> obterUltimasOperacoes(Long administradorId) {
        return auditoriaRepository.findUltimasOperacoes(administradorId);
    }
    
    /**
     * Conta operações de um administrador em um período
     */
    public long contarOperacoesPeríodo(Long administradorId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return auditoriaRepository.countByAdministradorIdAndDataOperacaoBetween(administradorId, dataInicio, dataFim);
    }
}
