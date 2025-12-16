package com.fachada.cagepa.fachada_cagepa.padroes.config;

import com.fachada.cagepa.fachada_cagepa.padroes.fachada.PainelCagepaFacade;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationRunner implements CommandLineRunner {
    private final PainelCagepaFacade painelCagepaFacade;

    public ConfigurationRunner(PainelCagepaFacade painelCagepaFacade) {
        this.painelCagepaFacade = painelCagepaFacade;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!painelCagepaFacade.isConfigured()) {
            System.out.println("Configuracao inicial necessaria!");
            ConfigurationCommand command = new ConfigurationCommand();
            command.executeConfiguration();
        } else {
            System.out.println("Diretorio de imagens configurado: " + painelCagepaFacade.getImageDirectory());
        }
    }
}

