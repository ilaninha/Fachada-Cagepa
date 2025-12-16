package com.fachada.cagepa.fachada_cagepa.padroes.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigurationCommand {
    private final ConfigManager configManager;
    private final BufferedReader reader;

    public ConfigurationCommand() {
        this.configManager = ConfigManager.getInstance();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void executeConfiguration() throws IOException {
        System.out.println("=== Configuracao de Diretorio de Imagens ===");

        String currentDir = configManager.getConfiguration("imageDirectory");
        if (currentDir != null) {
            System.out.println("Diretorio atual: " + currentDir);
        }

        System.out.print("Insira o caminho do diretorio de imagens dos hidrometros: ");
        String inputPath = reader.readLine().trim();

        if (inputPath.isEmpty()) {
            System.out.println("Operacao cancelada.");
            return;
        }

        try {
            ConfigBuilder builder = new ConfigBuilder();
            builder.withImageDirectory(inputPath).build();

            configManager.setConfiguration("imageDirectory", inputPath);
            System.out.println("Configuracao salva com sucesso!");
            System.out.println("Diretorio: " + inputPath);

        } catch (InvalidConfigurationException e) {
            System.err.println("Erro na configuracao: " + e.getMessage());
        }
    }
}

