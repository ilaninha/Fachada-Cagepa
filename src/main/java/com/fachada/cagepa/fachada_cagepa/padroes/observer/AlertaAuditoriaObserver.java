package com.fachada.cagepa.fachada_cagepa.padroes.observer;

import com.fachada.cagepa.fachada_cagepa.infra.entities.AuditoriaOperacao;
import org.springframework.stereotype.Component;

/**
 * Observer que monitora operações críticas e pode gerar alertas
 */
@Component
public class AlertaAuditoriaObserver implements AuditoriaObserver {
    
    private static final String[] OPERACOES_CRITICAS = {"DELETE", "CONFIG_CHANGE", "LOGIN"};
    private static final String[] ENTIDADES_CRITICAS = {"Administrador", "ConfiguracaoLimiteConsumo"};
    
    @Override
    public void operacaoRegistrada(AuditoriaOperacao operacao) {
        // Operações normais não geram alertas
    }
    
    @Override
    public void operacaoCritica(AuditoriaOperacao operacao) {
        // Registra alerta de operação crítica
        verificarEGerarAlerta(operacao);
    }
    
    @Override
    public void operacaoFalhou(AuditoriaOperacao operacao) {
        // Operações que falham são sempre críticas
        System.out.println("ALERTA: Operação falhou!");
        System.out.println("   Tipo: " + operacao.getTipoOperacao());
        System.out.println("   Entidade: " + operacao.getTipoEntidade());
        System.out.println("   Admin: " + operacao.getAdministrador().getUsername());
        System.out.println("   Erro: " + operacao.getMensagemErro());
    }
    
    private void verificarEGerarAlerta(AuditoriaOperacao operacao) {
        boolean ehCritica = false;
        String motivo = "";
        
        // Verifica se é operação crítica
        for (String opCritica : OPERACOES_CRITICAS) {
            if (operacao.getTipoOperacao().equals(opCritica)) {
                ehCritica = true;
                motivo = "Operação crítica: " + opCritica;
                break;
            }
        }
        
        // Verifica se é entidade crítica
        for (String entCritica : ENTIDADES_CRITICAS) {
            if (operacao.getTipoEntidade().contains(entCritica)) {
                ehCritica = true;
                motivo = "Entidade crítica: " + entCritica;
                break;
            }
        }
        
        if (ehCritica) {
            gerarAlerta(operacao, motivo);
        }
    }
    
    private void gerarAlerta(AuditoriaOperacao operacao, String motivo) {
        System.out.println("ALERTA DE AUDITORIA");
        System.out.println("   Motivo: " + motivo);
        System.out.println("   Tipo Operação: " + operacao.getTipoOperacao());
        System.out.println("   Entidade: " + operacao.getTipoEntidade() + " (ID: " + operacao.getEntidadeId() + ")");
        System.out.println("   Administrador: " + operacao.getAdministrador().getUsername());
        System.out.println("   Descrição: " + operacao.getDescricao());
        System.out.println("   Timestamp: " + operacao.getDataOperacao());
        System.out.println("   Resultado: " + operacao.getResultadoOperacao());
    }
}
