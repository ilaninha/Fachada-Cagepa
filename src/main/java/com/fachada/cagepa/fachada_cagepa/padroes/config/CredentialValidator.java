package com.fachada.cagepa.fachada_cagepa.padroes.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CredentialValidator {

    private static final int USERNAME_MIN = 3;
    private static final int USERNAME_MAX = 50;
    private static final int PASSWORD_MIN = 8;
    private static final int PASSWORD_MAX = 128;
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private final BCryptPasswordEncoder passwordEncoder;

    public CredentialValidator() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void validarUsername(String username) throws InvalidCredentialsException {
        if (username == null || username.isBlank()) {
            throw new InvalidCredentialsException("Username nao pode estar vazio.");
        }

        if (username.length() < USERNAME_MIN || username.length() > USERNAME_MAX) {
            throw new InvalidCredentialsException("Username deve ter entre " + USERNAME_MIN + " e " + USERNAME_MAX + " caracteres.");
        }

        if (!username.matches("^[a-zA-Z0-9_-]+$")) {
            throw new InvalidCredentialsException("Username pode conter apenas letras, numeros, underscore e hifen.");
        }
    }

    public void validarSenha(String password) throws InvalidCredentialsException {
        if (password == null || password.isBlank()) {
            throw new InvalidCredentialsException("Senha nao pode estar vazia.");
        }

        if (password.length() < PASSWORD_MIN || password.length() > PASSWORD_MAX) {
            throw new InvalidCredentialsException("Senha deve ter entre " + PASSWORD_MIN + " e " + PASSWORD_MAX + " caracteres.");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidCredentialsException("Senha deve conter letra maiuscula, minuscula, numero e caractere especial (@$!%*?&).");
        }
    }

    public String criptografarSenha(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean validarSenhaVsHash(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }
}
