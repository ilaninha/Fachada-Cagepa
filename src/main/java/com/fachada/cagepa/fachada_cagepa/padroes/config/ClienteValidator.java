package com.fachada.cagepa.fachada_cagepa.padroes.config;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ClienteValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public void validarCPF(String cpf) throws InvalidConfigurationException {
        if (cpf == null || cpf.isEmpty()) {
            throw new InvalidConfigurationException("CPF nao pode estar vazio.");
        }

        String cpfLimpo = cpf.replaceAll("\\D", "");

        if (cpfLimpo.length() != 11) {
            throw new InvalidConfigurationException("CPF deve conter exatamente 11 digitos.");
        }

        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            throw new InvalidConfigurationException("CPF invalido: todos os digitos sao iguais.");
        }

        if (!validarDigitosCPF(cpfLimpo)) {
            throw new InvalidConfigurationException("CPF invalido: digitos verificadores incorretos.");
        }
    }

    public void validarCNPJ(String cnpj) throws InvalidConfigurationException {
        if (cnpj == null || cnpj.isEmpty()) {
            throw new InvalidConfigurationException("CNPJ nao pode estar vazio.");
        }

        String cnpjLimpo = cnpj.replaceAll("\\D", "");

        if (cnpjLimpo.length() != 14) {
            throw new InvalidConfigurationException("CNPJ deve conter exatamente 14 digitos.");
        }

        if (cnpjLimpo.matches("(\\d)\\1{13}")) {
            throw new InvalidConfigurationException("CNPJ invalido: todos os digitos sao iguais.");
        }

        if (!validarDigitosCNPJ(cnpjLimpo)) {
            throw new InvalidConfigurationException("CNPJ invalido: digitos verificadores incorretos.");
        }
    }

    public void validarEmail(String email) throws InvalidConfigurationException {
        if (email == null || email.isEmpty()) {
            throw new InvalidConfigurationException("Email nao pode estar vazio.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidConfigurationException("Email invalido.");
        }

        if (email.length() > 255) {
            throw new InvalidConfigurationException("Email muito longo (maximo 255 caracteres).");
        }
    }

    public void validarTelefone(String telefone) throws InvalidConfigurationException {
        if (telefone == null || telefone.isEmpty()) {
            throw new InvalidConfigurationException("Telefone nao pode estar vazio.");
        }

        String telefoneLimpo = telefone.replaceAll("\\D", "");

        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            throw new InvalidConfigurationException("Telefone deve conter 10 ou 11 digitos.");
        }
    }

    public String limparCPF(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    public String limparCNPJ(String cnpj) {
        return cnpj.replaceAll("\\D", "");
    }

    public String limparTelefone(String telefone) {
        return telefone.replaceAll("\\D", "");
    }

    private boolean validarDigitosCPF(String cpf) {
        int primeiroDigito = calcularDigitoCPF(cpf.substring(0, 9), 10);
        int segundoDigito = calcularDigitoCPF(cpf.substring(0, 9) + primeiroDigito, 11);

        return cpf.charAt(9) == Character.forDigit(primeiroDigito, 10) &&
               cpf.charAt(10) == Character.forDigit(segundoDigito, 10);
    }

    private int calcularDigitoCPF(String base, int multiplicador) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * multiplicador--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private boolean validarDigitosCNPJ(String cnpj) {
        int primeiroDigito = calcularDigitoCNPJ(cnpj.substring(0, 12), 5);
        int segundoDigito = calcularDigitoCNPJ(cnpj.substring(0, 12) + primeiroDigito, 6);

        return cnpj.charAt(12) == Character.forDigit(primeiroDigito, 10) &&
               cnpj.charAt(13) == Character.forDigit(segundoDigito, 10);
    }

    private int calcularDigitoCNPJ(String base, int multiplicadorInicial) {
        int soma = 0;
        int multiplicador = multiplicadorInicial;

        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * multiplicador;
            multiplicador--;
            if (multiplicador < 2) {
                multiplicador = 9;
            }
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
