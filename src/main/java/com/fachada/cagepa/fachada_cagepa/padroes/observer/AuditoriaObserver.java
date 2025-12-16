package com.fachada.cagepa.fachada_cagepa.padroes.observer;

import com.fachada.cagepa.fachada_cagepa.infra.entities.AuditoriaOperacao;

/**
 * Interface do padrão Observer para notificação de operações auditáveis.
 * 
 * Quando uma operação relevante (CREATE, UPDATE, DELETE, etc.) é realizada,
 * todos os Observers registrados são notificados.
 */
public interface AuditoriaObserver {
    
    /**
     * Chamado quando uma operação é registrada
     * @param operacao A operação auditada
     */
    void operacaoRegistrada(AuditoriaOperacao operacao);
    
    /**
     * Chamado quando uma operação crítica ocorre
     * @param operacao A operação crítica auditada
     */
    void operacaoCritica(AuditoriaOperacao operacao);
    
    /**
     * Chamado quando uma operação falha
     * @param operacao A operação que falhou
     */
    void operacaoFalhou(AuditoriaOperacao operacao);
}
