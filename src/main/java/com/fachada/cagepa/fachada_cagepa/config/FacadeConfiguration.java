package com.fachada.cagepa.fachada_cagepa.config;

import com.fachada.cagepa.fachada_cagepa.padroes.fachada.PainelCagepaFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacadeConfiguration {

    @Bean
    public PainelCagepaFacade painelCagepaFacade() {
        return new PainelCagepaFacade();
    }
}

