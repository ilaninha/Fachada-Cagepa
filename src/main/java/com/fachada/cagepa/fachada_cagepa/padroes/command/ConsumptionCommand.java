package com.fachada.cagepa.fachada_cagepa.padroes.command;

/**
 * Command Interface para operações de cálculo de consumo.
 * Padrão: COMMAND (GoF) - Encapsula requisições como objetos.
 * 
 * Permite que operações de consumo sejam executadas, desfeitas e enfileiradas.
 */
public interface ConsumptionCommand {
    
    /**
     * Executa o comando de cálculo de consumo.
     * 
     * @return Resultado do cálculo
     */
    ConsumptionResult execute();
    
    /**
     * Desfaz o comando (para histórico de cálculos).
     */
    void undo();
    
    /**
     * Obtém descrição do comando.
     */
    String getDescription();
}
