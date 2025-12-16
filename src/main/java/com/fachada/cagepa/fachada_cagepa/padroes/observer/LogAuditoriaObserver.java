package com.fachada.cagepa.fachada_cagepa.padroes.observer;

import com.fachada.cagepa.fachada_cagepa.infra.entities.AuditoriaOperacao;
import org.springframework.stereotype.Component;

/**
 * Observer que registra operações em log (console/arquivo)
 */
@Component
public class LogAuditoriaObserver implements AuditoriaObserver {
    
    @Override
    public void operacaoRegistrada(AuditoriaOperacao operacao) {
        System.out.println("[AUDITORIA] Operação registrada:");
        System.out.println("  - Admin: " + operacao.getAdministrador().getUsername());
        System.out.println("  - Tipo: " + operacao.getTipoOperacao());
        System.out.println("  - Entidade: " + operacao.getTipoEntidade());
        System.out.println("  - Descrição: " + operacao.getDescricao());
        System.out.println("  - Resultado: " + operacao.getResultadoOperacao());
        System.out.println("  - Timestamp: " + operacao.getDataOperacao());
    }
    
    @Override
    public void operacaoCritica(AuditoriaOperacao operacao) {
        System.out.println("\n⚠️  [AUDITORIA CRÍTICA]");
        System.out.println("  - Admin: " + operacao.getAdministrador().getUsername());
        System.out.println("  - Tipo: " + operacao.getTipoOperacao());
        System.out.println("  - Entidade: " + operacao.getTipoEntidade() + " (ID: " + operacao.getEntidadeId() + ")");
        System.out.println("  - Descrição: " + operacao.getDescricao());
        System.out.println("  - Resultado: " + operacao.getResultadoOperacao());
        System.out.println("  - Timestamp: " + operacao.getDataOperacao() + "\n");
    }
    
    @Override
    public void operacaoFalhou(AuditoriaOperacao operacao) {
        System.out.println("\n❌ [AUDITORIA - FALHA]");
        System.out.println("  - Admin: " + operacao.getAdministrador().getUsername());
        System.out.println("  - Tipo: " + operacao.getTipoOperacao());
        System.out.println("  - Entidade: " + operacao.getTipoEntidade());
        System.out.println("  - Descrição: " + operacao.getDescricao());
        System.out.println("  - Erro: " + operacao.getMensagemErro());
        System.out.println("  - Timestamp: " + operacao.getDataOperacao() + "\n");
    }
}
