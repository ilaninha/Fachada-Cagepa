package com.fachada.cagepa.fachada_cagepa.padroes.autenticacao;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Administrador;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.AdministradorRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.config.CredentialValidator;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidCredentialsException;
import com.fachada.cagepa.fachada_cagepa.padroes.config.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final CredentialValidator credentialValidator;
    private final JwtTokenProvider jwtTokenProvider;

    public AdministradorService(AdministradorRepository administradorRepository, CredentialValidator credentialValidator, JwtTokenProvider jwtTokenProvider) {
        this.administradorRepository = administradorRepository;
        this.credentialValidator = credentialValidator;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public String login(String username, String password) throws InvalidCredentialsException {
        Optional<Administrador> administrador = administradorRepository.findByUsername(username);

        if (administrador.isEmpty()) {
            throw new InvalidCredentialsException("Username ou senha incorretos.");
        }

        Administrador admin = administrador.get();

        if (!admin.getAtivo()) {
            throw new InvalidCredentialsException("Administrador desativado.");
        }

        if (!credentialValidator.validarSenhaVsHash(password, admin.getPassword())) {
            throw new InvalidCredentialsException("Username ou senha incorretos.");
        }

        return jwtTokenProvider.gerarToken(admin.getId(), admin.getUsername());
    }

    @Transactional
    public void criarAdministrador(String username, String password, Long criadoPorId) throws InvalidCredentialsException {
        credentialValidator.validarUsername(username);
        credentialValidator.validarSenha(password);

        if (administradorRepository.findByUsername(username).isPresent()) {
            throw new InvalidCredentialsException("Username ja existe.");
        }

        String senhaEncriptada = credentialValidator.criptografarSenha(password);

        Administrador novoAdmin = Administrador.builder()
                .username(username)
                .password(senhaEncriptada)
                .ativo(true)
                .criadoPor(criadoPorId != null ? criadoPorId : 0L)
                .dataCriacao(LocalDateTime.now())
                .build();

        administradorRepository.save(novoAdmin);
    }

    @Transactional
    public void desativarAdministrador(String username) throws InvalidCredentialsException {
        Optional<Administrador> administrador = administradorRepository.findByUsername(username);

        if (administrador.isEmpty()) {
            throw new InvalidCredentialsException("Administrador nao encontrado.");
        }

        Administrador admin = administrador.get();
        admin.setAtivo(false);
        admin.setDataDesativacao(LocalDateTime.now());
        administradorRepository.save(admin);
    }

    public Administrador obterAdministrador(Long adminId) throws InvalidCredentialsException {
        Optional<Administrador> administrador = administradorRepository.findById(adminId);

        if (administrador.isEmpty()) {
            throw new InvalidCredentialsException("Administrador nao encontrado.");
        }

        return administrador.get();
    }
}
