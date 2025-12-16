package com.fachada.cagepa.fachada_cagepa.padroes.config;

import com.fachada.cagepa.fachada_cagepa.padroes.observer.LogAuditoriaObserver;
import com.fachada.cagepa.fachada_cagepa.padroes.observer.AlertaAuditoriaObserver;
import com.fachada.cagepa.fachada_cagepa.padroes.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import jakarta.annotation.PostConstruct;

/**
 * Configuração para inicializar o sistema de auditoria com seus observers.
 */
@Configuration
public class AuditoriaConfig {
    
    @Autowired
    private AuditoriaService auditoriaService;
    
    @Autowired
    @Lazy
    private LogAuditoriaObserver logObserver;
    
    @Autowired
    @Lazy
    private AlertaAuditoriaObserver alertaObserver;
    
    /**
     * Inicializa e registra todos os observers ao sistema
     */
    @PostConstruct
    public void inicializarObservers() {
        // Registra observer de log
        auditoriaService.registrarObserver(logObserver);
        
        // Registra observer de alertas
        auditoriaService.registrarObserver(alertaObserver);
        
        System.out.println("✓ Sistema de auditoria inicializado com " + 2 + " observers");
    }
}
