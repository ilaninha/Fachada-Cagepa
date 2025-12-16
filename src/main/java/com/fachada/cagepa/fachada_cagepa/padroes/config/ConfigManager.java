package com.fachada.cagepa.fachada_cagepa.padroes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static ConfigManager instance;
    private static final String CONFIG_FILE_NAME = ".config";
    private static final String CONFIG_DIR = System.getProperty("user.dir");
    private static final Path CONFIG_PATH = Paths.get(CONFIG_DIR, CONFIG_FILE_NAME);

    private final ObjectMapper objectMapper;
    private Map<String, String> configData;

    private ConfigManager() {
        this.objectMapper = new ObjectMapper();
        loadConfiguration();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfiguration() {
        File configFile = CONFIG_PATH.toFile();

        if (configFile.exists()) {
            try {
                this.configData = objectMapper.readValue(configFile, HashMap.class);
            } catch (IOException e) {
                this.configData = new HashMap<>();
            }
        } else {
            this.configData = new HashMap<>();
        }
    }

    public void saveConfiguration() throws IOException {
        File configFile = CONFIG_PATH.toFile();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, configData);
    }

    public void setConfiguration(String key, String value) throws IOException {
        configData.put(key, value);
        saveConfiguration();
    }

    public String getConfiguration(String key) {
        return configData.get(key);
    }

    public String getConfiguration(String key, String defaultValue) {
        return configData.getOrDefault(key, defaultValue);
    }

    public Map<String, String> getAllConfigurations() {
        return new HashMap<>(configData);
    }

    public boolean hasConfiguration(String key) {
        return configData.containsKey(key);
    }
}



