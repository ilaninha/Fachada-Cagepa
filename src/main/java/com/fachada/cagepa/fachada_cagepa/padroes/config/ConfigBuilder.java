package com.fachada.cagepa.fachada_cagepa.padroes.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigBuilder {
    private String imageDirectory;

    public ConfigBuilder withImageDirectory(String path) {
        this.imageDirectory = path;
        return this;
    }

    public void build() throws InvalidConfigurationException {
        validateImageDirectory();
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    private void validateImageDirectory() throws InvalidConfigurationException {
        if (imageDirectory == null || imageDirectory.trim().isEmpty()) {
            throw new InvalidConfigurationException("O diretório de imagens não pode estar vazio");
        }

        Path dirPath = Paths.get(imageDirectory);

        if (!Files.exists(dirPath)) {
            throw new InvalidConfigurationException("O diretório especificado não existe: " + imageDirectory);
        }

        if (!Files.isDirectory(dirPath)) {
            throw new InvalidConfigurationException("O caminho especificado não é um diretório: " + imageDirectory);
        }

        if (!Files.isReadable(dirPath)) {
            throw new InvalidConfigurationException("Não há permissão de leitura no diretório: " + imageDirectory);
        }
    }
}

