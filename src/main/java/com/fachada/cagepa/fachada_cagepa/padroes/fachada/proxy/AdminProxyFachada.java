package com.fachada.cagepa.fachada_cagepa.padroes.fachada.proxy;

import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidCredentialsException;
import com.fachada.cagepa.fachada_cagepa.padroes.config.JwtTokenProvider;
import com.fachada.cagepa.fachada_cagepa.padroes.fachada.PainelCagepaFacade;
import com.fachada.cagepa.fachada_cagepa.padroes.autenticacao.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AdminProxyFachada {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ApplicationContext applicationContext;

    private String tokenAtual;
    private Long adminAtualId;
    private String adminAtualUsername;

    public void login(String username, String password) throws InvalidCredentialsException {
        this.tokenAtual = administradorService.login(username, password);
        this.adminAtualId = jwtTokenProvider.extrairAdminId(this.tokenAtual);
        this.adminAtualUsername = jwtTokenProvider.extrairUsername(this.tokenAtual);

        System.out.println("Login realizado com sucesso para: " + this.adminAtualUsername);
        
        // Iniciar monitoramento apos login bem-sucedido
        try {
            PainelCagepaFacade painelFacade = applicationContext.getBean(PainelCagepaFacade.class);
            if (painelFacade != null) {
                painelFacade.iniciarMonitoramento();
            }
        } catch (Exception e) {
            System.err.println("Aviso: Nao foi possivel iniciar o monitoramento: " + e.getMessage());
        }
    }

    public void criarAdministrador(String username, String password) throws InvalidCredentialsException {
        if (!estaAutenticado()) {
            throw new InvalidCredentialsException("Nao autenticado. Realize login primeiro.");
        }

        administradorService.criarAdministrador(username, password, this.adminAtualId);
        System.out.println("Administrador criado com sucesso: " + username);
    }

    public void desativarAdministrador(String username) throws InvalidCredentialsException {
        if (!estaAutenticado()) {
            throw new InvalidCredentialsException("Nao autenticado. Realize login primeiro.");
        }

        if (this.adminAtualUsername.equals(username)) {
            throw new InvalidCredentialsException("Nao e possivel desativar sua propria conta.");
        }

        administradorService.desativarAdministrador(username);
        System.out.println("Administrador desativado com sucesso.");
    }

    public void logout() {
        this.tokenAtual = null;
        this.adminAtualId = null;
        this.adminAtualUsername = null;
        System.out.println("Logout realizado com sucesso.");
        
        // Parar monitoramento apos logout
        try {
            PainelCagepaFacade painelFacade = applicationContext.getBean(PainelCagepaFacade.class);
            if (painelFacade != null) {
                painelFacade.pararMonitoramento();
            }
        } catch (Exception e) {
            System.err.println("Aviso: Nao foi possivel parar o monitoramento: " + e.getMessage());
        }
    }

    public boolean estaAutenticado() {
        return this.tokenAtual != null && !this.tokenAtual.isEmpty();
    }

    public String obterToken() {
        return this.tokenAtual;
    }

    public Long obterAdminAtualId() {
        return this.adminAtualId;
    }

    public String obterAdminAtualUsername() {
        return this.adminAtualUsername;
    }

    public void criarAdministradorPadrao() throws InvalidCredentialsException {
        administradorService.criarAdministrador("admin", "Admin123@", null);
        System.out.println("Administrador padrao criado: admin/Admin123@");
    }
}
