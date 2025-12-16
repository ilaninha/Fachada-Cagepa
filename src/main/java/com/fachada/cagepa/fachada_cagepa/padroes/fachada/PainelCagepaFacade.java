package com.fachada.cagepa.fachada_cagepa.padroes.fachada;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Endereco;
import com.fachada.cagepa.fachada_cagepa.infra.entities.Hidrometro;
import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaFisica;
import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaJuridica;
import com.fachada.cagepa.fachada_cagepa.infra.entities.ConfiguracaoLimiteConsumo;
import com.fachada.cagepa.fachada_cagepa.infra.entities.HistoricoNotificacao;
import com.fachada.cagepa.fachada_cagepa.infra.entities.AuditoriaOperacao;
import com.fachada.cagepa.fachada_cagepa.padroes.command.ConsumptionResult;
import com.fachada.cagepa.fachada_cagepa.padroes.config.ConfigBuilder;
import com.fachada.cagepa.fachada_cagepa.padroes.config.ConfigManager;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidConfigurationException;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidCredentialsException;
import com.fachada.cagepa.fachada_cagepa.padroes.fachada.proxy.AdminProxyFachada;
import com.fachada.cagepa.fachada_cagepa.padroes.fachada.proxy.ClienteProxyFachada;
import com.fachada.cagepa.fachada_cagepa.padroes.fachada.proxy.HidrometroProxyFachada;
import com.fachada.cagepa.fachada_cagepa.padroes.service.ConsumptionCalculationService;
import com.fachada.cagepa.fachada_cagepa.padroes.service.NotificationService;
import com.fachada.cagepa.fachada_cagepa.padroes.service.AuditoriaService;
import com.fachada.cagepa.fachada_cagepa.padroes.watcher.ImagemWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
public class PainelCagepaFacade {
    private final ConfigManager configManager;
    private static final String IMAGE_DIRECTORY_KEY = "imageDirectory";

    @Autowired(required = false)
    private ImagemWatcher imagemWatcher;

    @Autowired(required = false)
    private AdminProxyFachada adminProxyFachada;

    @Autowired(required = false)
    private ClienteProxyFachada clienteProxyFachada;

    @Autowired(required = false)
    private HidrometroProxyFachada hidrometroProxyFachada;
    
    @Autowired
    private ConsumptionCalculationService consumptionCalculationService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private AuditoriaService auditoriaService;

    public PainelCagepaFacade() {
        this.configManager = ConfigManager.getInstance();
    }

    @PostConstruct
    public void inicializarMonitoramento() {
        if (adminProxyFachada != null) {
            try {
                adminProxyFachada.criarAdministradorPadrao();
            } catch (InvalidCredentialsException e) {
            }
        }
        // Monitoramento agora é iniciado apenas apos login bem-sucedido
    }

    public void login(String username, String password) throws InvalidCredentialsException {
        if (adminProxyFachada != null) {
            adminProxyFachada.login(username, password);
            // Inicia monitoramento automaticamente se configurado e logado
            if (estaAutenticado() && isConfigured()) {
                iniciarMonitoramento();
            }
        } else {
            throw new InvalidCredentialsException("Servico de autenticacao nao disponivel.");
        }
    }

    public void criarAdministrador(String username, String password) throws InvalidCredentialsException {
        if (adminProxyFachada != null) {
            adminProxyFachada.criarAdministrador(username, password);
        } else {
            throw new InvalidCredentialsException("Servico de autenticacao nao disponivel.");
        }
    }

    public void desativarAdministrador(String username) throws InvalidCredentialsException {
        if (adminProxyFachada != null) {
            adminProxyFachada.desativarAdministrador(username);
        } else {
            throw new InvalidCredentialsException("Servico de autenticacao nao disponivel.");
        }
    }

    public void logout() {
        if (adminProxyFachada != null) {
            adminProxyFachada.logout();
        }
    }

    public boolean estaAutenticado() {
        return adminProxyFachada != null && adminProxyFachada.estaAutenticado();
    }

    public synchronized void configureImageDirectory(String directoryPath) throws InvalidConfigurationException {
        ConfigBuilder builder = new ConfigBuilder();
        builder.withImageDirectory(directoryPath).build();

        try {
            configManager.setConfiguration(IMAGE_DIRECTORY_KEY, directoryPath);
            
            if (imagemWatcher != null) {
                imagemWatcher.setDirectoryToWatch(directoryPath);
                imagemWatcher.start();
            }
        } catch (Exception e) {
            throw new InvalidConfigurationException("Erro ao salvar configuracao: " + e.getMessage(), e);
        }
    }

    public String getImageDirectory() {
        return configManager.getConfiguration(IMAGE_DIRECTORY_KEY);
    }

    public boolean isConfigured() {
        return configManager.hasConfiguration(IMAGE_DIRECTORY_KEY);
    }

    public synchronized void iniciarMonitoramento() {
        if (isConfigured() && imagemWatcher != null) {
            String diretorio = getImageDirectory();
            imagemWatcher.setDirectoryToWatch(diretorio);
            imagemWatcher.start();
            System.out.println("Monitoramento de hidrometros iniciado.");
        } else if (!isConfigured()) {
            System.out.println("Aviso: Diretorio de imagens nao configurado. Monitoramento nao iniciado.");
        }
    }

    public synchronized void pararMonitoramento() {
        if (imagemWatcher != null) {
            imagemWatcher.stop();
            System.out.println("Monitoramento parado.");
        }
    }

    public void inicializarAdminPadrao() {
        if (adminProxyFachada != null) {
            try {
                adminProxyFachada.criarAdministradorPadrao();
            } catch (InvalidCredentialsException e) {
            }
        }
    }

    // Métodos de Cliente - Pessoa Física
    public void criarPessoaFisica(String nome, String cpf, String email, String telefone) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            try {
                clienteProxyFachada.criarPessoaFisica(nome, cpf, email, telefone);
                
                // Registra criação na auditoria
                if (auditoriaService != null) {
                    try {
                        var pessoaFisica = clienteProxyFachada.obterPorCPF(cpf);
                        if (pessoaFisica != null) {
                            String dadosNovos = String.format("PessoaFisica(nome=%s, cpf=%s, email=%s, telefone=%s)", 
                                nome, cpf, email, telefone);
                            auditoriaService.registrarCriacaoEntidade(null, "PessoaFisica", pessoaFisica.getId(),
                                "Criação de nova pessoa física: " + nome, dadosNovos);
                        }
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                // Registra falha na auditoria
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "CREATE", "PessoaFisica", null,
                            "Falha ao criar pessoa física: " + nome, e.getMessage(), true);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao criar pessoa física: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            try {
                var pessoaFisica = clienteProxyFachada.obterPessoaFisica(pessoaFisicaId);
                String dadosAntigos = pessoaFisica != null ? pessoaFisica.toString() : null;
                
                clienteProxyFachada.inativarPessoaFisica(pessoaFisicaId);
                
                // Registra inativação na auditoria
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarDelecaoEntidade(null, "PessoaFisica", pessoaFisicaId,
                            "Inativação de pessoa física", dadosAntigos);
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "DELETE", "PessoaFisica", pessoaFisicaId,
                            "Falha ao inativar pessoa física", e.getMessage(), true);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao inativar pessoa física: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaFisicaByCPF(String cpf) throws InvalidConfigurationException {
        inativarPessoaFisicaPorCPF(cpf);
    }

    public PessoaFisica obterPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPessoaFisica(pessoaFisicaId);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public List<PessoaFisica> listarPessoasFisicas() throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.listarPessoasFisicas();
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public PessoaFisica obterPessoaFisicaPorCPF(String cpf) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPorCPF(cpf);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public PessoaFisica obterPessoaFisicaPorEmail(String email) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPorEmail(email);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public PessoaFisica obterPessoaFisicaPorTelefone(String telefone) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPorTelefone(telefone);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaFisicaPorCPF(String cpf) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            clienteProxyFachada.inativarPorCPF(cpf);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaFisicaPorEmail(String email) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            clienteProxyFachada.inativarPorEmail(email);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaFisicaPorTelefone(String telefone) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            clienteProxyFachada.inativarPorTelefone(telefone);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    // Métodos de Cliente - Pessoa Jurídica
    public void criarPessoaJuridica(String razaoSocial, String cnpj, String email, String telefone) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            try {
                clienteProxyFachada.criarPessoaJuridica(razaoSocial, cnpj, email, telefone);
                
                // Registra criação na auditoria
                if (auditoriaService != null) {
                    try {
                        var pessoaJuridica = clienteProxyFachada.obterPorCNPJ(cnpj);
                        if (pessoaJuridica != null) {
                            String dadosNovos = String.format("PessoaJuridica(razaoSocial=%s, cnpj=%s, email=%s, telefone=%s)",
                                razaoSocial, cnpj, email, telefone);
                            auditoriaService.registrarCriacaoEntidade(null, "PessoaJuridica", pessoaJuridica.getId(),
                                "Criação de nova pessoa jurídica: " + razaoSocial, dadosNovos);
                        }
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "CREATE", "PessoaJuridica", null,
                            "Falha ao criar pessoa jurídica: " + razaoSocial, e.getMessage(), true);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao criar pessoa jurídica: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            try {
                var pessoaJuridica = clienteProxyFachada.obterPessoaJuridica(pessoaJuridicaId);
                String dadosAntigos = pessoaJuridica != null ? pessoaJuridica.toString() : null;
                
                clienteProxyFachada.inativarPessoaJuridica(pessoaJuridicaId);
                
                // Registra inativação na auditoria
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarDelecaoEntidade(null, "PessoaJuridica", pessoaJuridicaId,
                            "Inativação de pessoa jurídica", dadosAntigos);
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "DELETE", "PessoaJuridica", pessoaJuridicaId,
                            "Falha ao inativar pessoa jurídica", e.getMessage(), true);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao inativar pessoa jurídica: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaJuridicaByCNPJ(String cnpj) throws InvalidConfigurationException {
        inativarPessoaJuridicaPorCNPJ(cnpj);
    }

    public PessoaJuridica obterPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPessoaJuridica(pessoaJuridicaId);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public List<PessoaJuridica> listarPessoasJuridicas() throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.listarPessoasJuridicas();
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public PessoaJuridica obterPessoaJuridicaPorCNPJ(String cnpj) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPorCNPJ(cnpj);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public PessoaJuridica obterPessoaJuridicaPorEmail(String email) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPorEmailPJ(email);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public PessoaJuridica obterPessoaJuridicaPorTelefone(String telefone) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPorTelefonePJ(telefone);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaJuridicaPorCNPJ(String cnpj) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            clienteProxyFachada.inativarPorCNPJ(cnpj);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaJuridicaPorEmail(String email) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            clienteProxyFachada.inativarPorEmailPJ(email);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void inativarPessoaJuridicaPorTelefone(String telefone) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            clienteProxyFachada.inativarPorTelefonePJ(telefone);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    // Métodos de Endereço
    public void adicionarEnderecoPessoaFisica(Long pessoaFisicaId, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            try {
                clienteProxyFachada.adicionarEnderecoPessoaFisica(pessoaFisicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
                
                // Registra adição de endereço na auditoria
                if (auditoriaService != null) {
                    try {
                        String dadosNovos = String.format("Endereco(logradouro=%s, numero=%s, complemento=%s, bairro=%s, cidade=%s, estado=%s, cep=%s)",
                            logradouro, numero, complemento, bairro, cidade, estado, cep);
                        auditoriaService.registrarCriacaoEntidade(null, "Endereco", null,
                            "Adição de endereço a pessoa física " + pessoaFisicaId, dadosNovos);
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "CREATE", "Endereco", null,
                            "Falha ao adicionar endereço pessoa física " + pessoaFisicaId, e.getMessage(), false);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao adicionar endereço: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void adicionarEnderecoPessoaJuridica(Long pessoaJuridicaId, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            try {
                clienteProxyFachada.adicionarEnderecoPessoaJuridica(pessoaJuridicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
                
                // Registra adição de endereço na auditoria
                if (auditoriaService != null) {
                    try {
                        String dadosNovos = String.format("Endereco(logradouro=%s, numero=%s, complemento=%s, bairro=%s, cidade=%s, estado=%s, cep=%s)",
                            logradouro, numero, complemento, bairro, cidade, estado, cep);
                        auditoriaService.registrarCriacaoEntidade(null, "Endereco", null,
                            "Adição de endereço a pessoa jurídica " + pessoaJuridicaId, dadosNovos);
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "CREATE", "Endereco", null,
                            "Falha ao adicionar endereço pessoa jurídica " + pessoaJuridicaId, e.getMessage(), false);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao adicionar endereço: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public List<Endereco> listarEnderecosPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.listarEnderecosPessoaFisica(pessoaFisicaId);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public List<Endereco> listarEnderecosPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.listarEnderecosPessoaJuridica(pessoaJuridicaId);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void deletarEndereco(Long enderecoId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            try {
                clienteProxyFachada.deletarEndereco(enderecoId);
                
                // Registra deleção de endereço na auditoria
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarDelecaoEntidade(null, "Endereco", enderecoId,
                            "Deleção de endereço", null);
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "DELETE", "Endereco", enderecoId,
                            "Falha ao deletar endereço", e.getMessage(), false);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao deletar endereço: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }
    
    // Métodos auxiliares para obter IDs de clientes
    public Long obterIdPessoaFisicaPorCpf(String cpf) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPessoaFisicaPorCPF(cpf);
        if (pessoaFisica == null) {
            throw new InvalidConfigurationException("Pessoa Fisica com CPF " + cpf + " nao encontrada.");
        }
        return pessoaFisica.getId();
    }
    
    public Long obterIdPessoaJuridicaPorCnpj(String cnpj) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPessoaJuridicaPorCNPJ(cnpj);
        if (pessoaJuridica == null) {
            throw new InvalidConfigurationException("Pessoa Juridica com CNPJ " + cnpj + " nao encontrada.");
        }
        return pessoaJuridica.getId();
    }

    // Métodos de Hidrômetro
    public void cadastrarHidrometroPessoaFisica(String sha, Long limiteConsumoMensal, Long pessoaFisicaId, Long enderecoId)
            throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            try {
                hidrometroProxyFachada.cadastrarHidrometroPessoaFisica(sha, limiteConsumoMensal, pessoaFisicaId, enderecoId);
                
                // Registra criação na auditoria
                if (auditoriaService != null) {
                    try {
                        String dadosNovos = String.format("Hidrometro(sha=%s, limiteConsumo=%d, pessoaFisica=%d, endereco=%d)",
                            sha, limiteConsumoMensal, pessoaFisicaId, enderecoId);
                        auditoriaService.registrarCriacaoEntidade(null, "Hidrometro", null,
                            "Criação de hidrometro para pessoa física: SHA " + sha, dadosNovos);
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "CREATE", "Hidrometro", null,
                            "Falha ao cadastrar hidrometro pessoa física: " + sha, e.getMessage(), true);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao cadastrar hidrometro: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public void cadastrarHidrometroPessoaJuridica(String sha, Long limiteConsumoMensal, Long pessoaJuridicaId, Long enderecoId)
            throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            try {
                hidrometroProxyFachada.cadastrarHidrometroPessoaJuridica(sha, limiteConsumoMensal, pessoaJuridicaId, enderecoId);
                
                // Registra criação na auditoria
                if (auditoriaService != null) {
                    try {
                        String dadosNovos = String.format("Hidrometro(sha=%s, limiteConsumo=%d, pessoaJuridica=%d, endereco=%d)",
                            sha, limiteConsumoMensal, pessoaJuridicaId, enderecoId);
                        auditoriaService.registrarCriacaoEntidade(null, "Hidrometro", null,
                            "Criação de hidrometro para pessoa jurídica: SHA " + sha, dadosNovos);
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "CREATE", "Hidrometro", null,
                            "Falha ao cadastrar hidrometro pessoa jurídica: " + sha, e.getMessage(), true);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao cadastrar hidrometro: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public void cadastrarHidrometroPessoaFisicaComEndereco(String sha, Long limiteConsumoMensal, Long pessoaFisicaId,
                                                           String logradouro, String numero, String complemento, String bairro,
                                                           String cidade, String estado, String cep) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            hidrometroProxyFachada.cadastrarHidrometroPessoaFisicaComEndereco(sha, limiteConsumoMensal, pessoaFisicaId,
                    logradouro, numero, complemento, bairro, cidade, estado, cep);
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public void cadastrarHidrometroPessoaJuridicaComEndereco(String sha, Long limiteConsumoMensal, Long pessoaJuridicaId,
                                                             String logradouro, String numero, String complemento, String bairro,
                                                             String cidade, String estado, String cep) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            hidrometroProxyFachada.cadastrarHidrometroPessoaJuridicaComEndereco(sha, limiteConsumoMensal, pessoaJuridicaId,
                    logradouro, numero, complemento, bairro, cidade, estado, cep);
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public Hidrometro obterHidrometroPorSHA(String sha) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            return hidrometroProxyFachada.obterPorSHA(sha);
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public List<Hidrometro> listarHidrometrosPorPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            return hidrometroProxyFachada.listarPorPessoaFisica(pessoaFisicaId);
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public List<Hidrometro> listarHidrometrosPorPessoaFisicaAtivos(Long pessoaFisicaId) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            return hidrometroProxyFachada.listarPorPessoaFisicaAtivos(pessoaFisicaId);
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public List<Hidrometro> listarHidrometrosPorPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            return hidrometroProxyFachada.listarPorPessoaJuridica(pessoaJuridicaId);
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public List<Hidrometro> listarHidrometrosPorPessoaJuridicaAtivos(Long pessoaJuridicaId) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            return hidrometroProxyFachada.listarPorPessoaJuridicaAtivos(pessoaJuridicaId);
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public void ativarHidrometro(Long hidrometroId) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            try {
                hidrometroProxyFachada.ativarHidrometro(hidrometroId);
                
                // Registra ativação na auditoria
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarMudancaConfiguracao(null, "Hidrometro", hidrometroId,
                            "Ativação de hidrometro", "ativo=false", "ativo=true");
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "UPDATE", "Hidrometro", hidrometroId,
                            "Falha ao ativar hidrometro", e.getMessage(), false);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao ativar hidrometro: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public void desativarHidrometro(Long hidrometroId) throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            try {
                hidrometroProxyFachada.desativarHidrometro(hidrometroId);
                
                // Registra desativação na auditoria
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarMudancaConfiguracao(null, "Hidrometro", hidrometroId,
                            "Desativação de hidrometro", "ativo=true", "ativo=false");
                    } catch (Exception e) {
                        // Silencia erros de auditoria
                    }
                }
            } catch (Exception e) {
                if (auditoriaService != null) {
                    try {
                        auditoriaService.registrarFalha(null, "UPDATE", "Hidrometro", hidrometroId,
                            "Falha ao desativar hidrometro", e.getMessage(), false);
                    } catch (Exception ignored) {}
                }
                throw new InvalidConfigurationException("Erro ao desativar hidrometro: " + e.getMessage());
            }
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public List<Hidrometro> listarHidrometrosAtivos() throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            return hidrometroProxyFachada.listarAtivos();
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    public List<Hidrometro> listarHidrometrosInativos() throws InvalidConfigurationException {
        if (hidrometroProxyFachada != null) {
            return hidrometroProxyFachada.listarInativos();
        } else {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
    }

    // Métodos auxiliares para obter por ID
    public PessoaFisica obterPessoaFisicaPorId(Long pessoaFisicaId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPessoaFisica(pessoaFisicaId);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public PessoaJuridica obterPessoaJuridicaPorId(Long pessoaJuridicaId) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            return clienteProxyFachada.obterPessoaJuridica(pessoaJuridicaId);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    // Métodos para criar endereços
    public void criarEnderecoPessoaFisica(Long pessoaFisicaId, String logradouro, String numero, String complemento,
                                          String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            clienteProxyFachada.criarEnderecoPessoaFisica(pessoaFisicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    public void criarEnderecoPessoaJuridica(Long pessoaJuridicaId, String logradouro, String numero, String complemento,
                                            String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        if (clienteProxyFachada != null) {
            clienteProxyFachada.criarEnderecoPessoaJuridica(pessoaJuridicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
        } else {
            throw new InvalidConfigurationException("Servico de clientes nao disponivel.");
        }
    }

    // Métodos para listar hidrometros por CPF/CNPJ
    public List<Hidrometro> listarHidrometrosPorCPF(String cpf) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPessoaFisicaPorCPF(cpf);
        return listarHidrometrosPorPessoaFisica(pessoaFisica.getId());
    }

    public List<Hidrometro> listarHidrometrosPorCNPJ(String cnpj) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPessoaJuridicaPorCNPJ(cnpj);
        return listarHidrometrosPorPessoaJuridica(pessoaJuridica.getId());
    }

    // Métodos com CPF/CNPJ para endereços
    public void adicionarEnderecoPessoaFisicaPorCPF(String cpf, String logradouro, String numero, String complemento, 
                                                     String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPessoaFisicaPorCPF(cpf);
        adicionarEnderecoPessoaFisica(pessoaFisica.getId(), logradouro, numero, complemento, bairro, cidade, estado, cep);
    }

    public void adicionarEnderecoPessoaJuridicaPorCNPJ(String cnpj, String logradouro, String numero, String complemento, 
                                                       String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPessoaJuridicaPorCNPJ(cnpj);
        adicionarEnderecoPessoaJuridica(pessoaJuridica.getId(), logradouro, numero, complemento, bairro, cidade, estado, cep);
    }

    public List<Endereco> listarEnderecosPessoaFisicaPorCPF(String cpf) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPessoaFisicaPorCPF(cpf);
        return listarEnderecosPessoaFisica(pessoaFisica.getId());
    }

    public List<Endereco> listarEnderecosPessoaJuridicaPorCNPJ(String cnpj) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPessoaJuridicaPorCNPJ(cnpj);
        return listarEnderecosPessoaJuridica(pessoaJuridica.getId());
    }

    // Métodos com CPF/CNPJ para cadastro de hidrometros
    public void cadastrarHidrometroPessoaFisicaPorCPF(String sha, Long limiteConsumoMensal, String cpf, Long enderecoId)
            throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPessoaFisicaPorCPF(cpf);
        cadastrarHidrometroPessoaFisica(sha, limiteConsumoMensal, pessoaFisica.getId(), enderecoId);
    }

    public void cadastrarHidrometroPessoaJuridicaPorCNPJ(String sha, Long limiteConsumoMensal, String cnpj, Long enderecoId)
            throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPessoaJuridicaPorCNPJ(cnpj);
        cadastrarHidrometroPessoaJuridica(sha, limiteConsumoMensal, pessoaJuridica.getId(), enderecoId);
    }

    public void cadastrarHidrometroPessoaFisicaComEnderecoPorCPF(String sha, Long limiteConsumoMensal, String cpf,
                                                                 String logradouro, String numero, String complemento, String bairro,
                                                                 String cidade, String estado, String cep) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPessoaFisicaPorCPF(cpf);
        cadastrarHidrometroPessoaFisicaComEndereco(sha, limiteConsumoMensal, pessoaFisica.getId(),
                logradouro, numero, complemento, bairro, cidade, estado, cep);
    }

    public void cadastrarHidrometroPessoaJuridicaComEnderecoPorCNPJ(String sha, Long limiteConsumoMensal, String cnpj,
                                                                    String logradouro, String numero, String complemento, String bairro,
                                                                    String cidade, String estado, String cep) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPessoaJuridicaPorCNPJ(cnpj);
        cadastrarHidrometroPessoaJuridicaComEndereco(sha, limiteConsumoMensal, pessoaJuridica.getId(),
                logradouro, numero, complemento, bairro, cidade, estado, cep);
    }
    /**
     * Calcula o consumo de um hidrômetro específico em um período.
     * 
     * @param sha Identificador do hidrômetro
     * @param periodType Tipo de período (daily, weekly, monthly, annual)
     * @return Resultado do cálculo com detalhes
     */
    public ConsumptionResult obterConsumoHidrometro(String sha, String periodType) 
            throws InvalidConfigurationException {
        if (consumptionCalculationService == null) {
            throw new InvalidConfigurationException("Servico de calculo de consumo nao disponivel.");
        }
        return consumptionCalculationService.calculateIndividualConsumption(sha, periodType);
    }
    
    /**
     * Calcula o consumo de um hidrômetro em todos os períodos suportados.
     * 
     * @param sha Identificador do hidrômetro
     * @return Mapa com resultado para cada período
     */
    public Map<String, ConsumptionResult> obterConsumoHidrometroPeriodos(String sha) 
            throws InvalidConfigurationException {
        if (consumptionCalculationService == null) {
            throw new InvalidConfigurationException("Servico de calculo de consumo nao disponivel.");
        }
        return consumptionCalculationService.calculateAllPeriods(sha);
    }
    
    /**
     * Calcula o consumo total de um cliente (Pessoa Física) somando todos os seus hidrômetros.
     * 
     * @param pessoaFisicaId ID da Pessoa Física
     * @param periodType Tipo de período
     * @return Resultado agregado do consumo total
     */
    public ConsumptionResult obterConsumoPessoaFisica(Long pessoaFisicaId, String periodType) 
            throws InvalidConfigurationException {
        if (consumptionCalculationService == null) {
            throw new InvalidConfigurationException("Servico de calculo de consumo nao disponivel.");
        }
        return consumptionCalculationService.calculateClientConsumption(pessoaFisicaId, periodType);
    }
    
    /**
     * Calcula o consumo total de um cliente em todos os períodos.
     * 
     * @param pessoaFisicaId ID da Pessoa Física
     * @return Mapa com resultado para cada período
     */
    public Map<String, ConsumptionResult> obterConsumoPessoaFisicaPeriodos(Long pessoaFisicaId) 
            throws InvalidConfigurationException {
        if (consumptionCalculationService == null) {
            throw new InvalidConfigurationException("Servico de calculo de consumo nao disponivel.");
        }
        return consumptionCalculationService.calculateClientAllPeriods(pessoaFisicaId);
    }
    
    /**
     * Calcula o consumo total de um cliente (Pessoa Física) por CPF em um período.
     * 
     * @param cpf CPF da pessoa física
     * @param periodType Tipo de período
     * @return Resultado agregado do consumo total
     */
    public ConsumptionResult obterConsumoPorCPF(String cpf, String periodType) 
            throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPessoaFisicaPorCPF(cpf);
        return obterConsumoPessoaFisica(pessoaFisica.getId(), periodType);
    }
    
    /**
     * Calcula o consumo total de um cliente (Pessoa Física) por CPF em todos os períodos.
     * 
     * @param cpf CPF da pessoa física
     * @return Mapa com resultado para cada período
     */
    public Map<String, ConsumptionResult> obterConsumoPorCPFPeriodos(String cpf) 
            throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPessoaFisicaPorCPF(cpf);
        return obterConsumoPessoaFisicaPeriodos(pessoaFisica.getId());
    }
    
    /**
     * Calcula o consumo total de uma empresa (Pessoa Jurídica) somando todos os seus hidrômetros.
     * 
     * @param pessoaJuridicaId ID da Pessoa Jurídica
     * @param periodType Tipo de período
     * @return Resultado agregado do consumo total
     */
    public ConsumptionResult obterConsumoPessoaJuridica(Long pessoaJuridicaId, String periodType) 
            throws InvalidConfigurationException {
        if (consumptionCalculationService == null) {
            throw new InvalidConfigurationException("Servico de calculo de consumo nao disponivel.");
        }
        return consumptionCalculationService.calculateCompanyConsumption(pessoaJuridicaId, periodType);
    }
    
    /**
     * Calcula o consumo total de uma empresa em todos os períodos.
     * 
     * @param pessoaJuridicaId ID da Pessoa Jurídica
     * @return Mapa com resultado para cada período
     */
    public Map<String, ConsumptionResult> obterConsumoPessoaJuridicaPeriodos(Long pessoaJuridicaId) 
            throws InvalidConfigurationException {
        if (consumptionCalculationService == null) {
            throw new InvalidConfigurationException("Servico de calculo de consumo nao disponivel.");
        }
        return consumptionCalculationService.calculateCompanyAllPeriods(pessoaJuridicaId);
    }
    
    /**
     * Calcula o consumo total de uma empresa por CNPJ em um período.
     * 
     * @param cnpj CNPJ da empresa
     * @param periodType Tipo de período
     * @return Resultado agregado do consumo total
     */
    public ConsumptionResult obterConsumoPorCNPJ(String cnpj, String periodType) 
            throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPessoaJuridicaPorCNPJ(cnpj);
        return obterConsumoPessoaJuridica(pessoaJuridica.getId(), periodType);
    }
    
    /**
     * Calcula o consumo total de uma empresa por CNPJ em todos os períodos.
     * 
     * @param cnpj CNPJ da empresa
     * @return Mapa com resultado para cada período
     */
    public Map<String, ConsumptionResult> obterConsumoPorCNPJPeriodos(String cnpj) 
            throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPessoaJuridicaPorCNPJ(cnpj);
        return obterConsumoPessoaJuridicaPeriodos(pessoaJuridica.getId());
    }
    
    /**
     * Lista todos os períodos de cálculo suportados.
     * 
     * @return Lista com [daily, weekly, monthly, annual]
     */
    public List<String> obterPeriodosSuportados() 
            throws InvalidConfigurationException {
        if (consumptionCalculationService == null) {
            throw new InvalidConfigurationException("Servico de calculo de consumo nao disponivel.");
        }
        return consumptionCalculationService.getSupportedPeriods();
    }
    
    /**
     * Compara o consumo de um hidrômetro entre dois períodos.
     * Útil para análise de tendências.
     * 
     * @param sha Identificador do hidrômetro
     * @param periodType1 Primeiro período
     * @param periodType2 Segundo período
     * @return Array com [consumoPeriodo1, consumoPeriodo2, variacao]
     */
    public Long[] compararConsumo(String sha, String periodType1, String periodType2) 
            throws InvalidConfigurationException {
        if (consumptionCalculationService == null) {
            throw new InvalidConfigurationException("Servico de calculo de consumo nao disponivel.");
        }
        return consumptionCalculationService.compareConsumption(sha, periodType1, periodType2);
    }
    /**
     * Configura um limite de consumo mensal para um hidrômetro.
     * Quando o consumo atingir o percentual do limite, notificação será enviada.
     * 
     * @param hidrometroId ID do hidrômetro
     * @param limiteConsumMensal Limite em m³
     * @param percentualLimite Percentual que dispara notificação (ex: 70)
     * @return Configuração criada/atualizada
     */
    public ConfiguracaoLimiteConsumo configurarLimiteNotificacao(Long hidrometroId, Long limiteConsumMensal, Integer percentualLimite) 
            throws InvalidConfigurationException {
        if (notificationService == null) {
            throw new InvalidConfigurationException("Servico de notificacao nao disponivel.");
        }
        try {
            var config = notificationService.configurarLimite(hidrometroId, limiteConsumMensal, percentualLimite);
            
            // Registra configuração na auditoria
            if (auditoriaService != null) {
                try {
                    String dadosNovos = String.format("Limite=%s m³, Percentual=%s%%", limiteConsumMensal, percentualLimite);
                    auditoriaService.registrarMudancaConfiguracao(null, "ConfiguracaoLimiteConsumo", hidrometroId,
                        "Configuração de limite de consumo para hidrometro", null, dadosNovos);
                } catch (Exception e) {
                    // Silencia erros de auditoria
                }
            }
            return config;
        } catch (Exception e) {
            if (auditoriaService != null) {
                try {
                    auditoriaService.registrarFalha(null, "CONFIG_CHANGE", "ConfiguracaoLimiteConsumo", hidrometroId,
                        "Falha ao configurar limite", e.getMessage(), true);
                } catch (Exception ignored) {}
            }
            throw new InvalidConfigurationException("Erro ao configurar limite: " + e.getMessage());
        }
    }
    
    /**
     * Verifica o consumo mensal de um hidrômetro e envia notificação se necessário.
     * Detecta quando o consumo atinge 70% (ou percentual configurado) do limite.
     * Evita enviar duplicatas no mesmo dia.
     * 
     * @param sha Identificador do hidrômetro
     */
    public void verificarEEnviarNotificacao(String sha) 
            throws InvalidConfigurationException {
        if (notificationService == null) {
            throw new InvalidConfigurationException("Servico de notificacao nao disponivel.");
        }
        notificationService.verificarENotificar(sha);
    }
    
    /**
     * Consulta histórico de notificações enviadas para um hidrômetro específico.
     * 
     * @param hidrometroId ID do hidrômetro
     * @return Lista com histórico de notificações
     */
    public List<HistoricoNotificacao> consultarHistoricoNotificacoesHidrometro(Long hidrometroId) 
            throws InvalidConfigurationException {
        if (notificationService == null) {
            throw new InvalidConfigurationException("Servico de notificacao nao disponivel.");
        }
        return notificationService.consultarHistoricoHidrometro(hidrometroId);
    }
    
    /**
     * Consulta histórico de notificações para um cliente (Pessoa Física).
     * 
     * @param pessoaFisicaId ID da Pessoa Física
     * @return Lista com histórico de notificações
     */
    public List<HistoricoNotificacao> consultarHistoricoNotificacoesPessoaFisica(Long pessoaFisicaId) 
            throws InvalidConfigurationException {
        if (notificationService == null) {
            throw new InvalidConfigurationException("Servico de notificacao nao disponivel.");
        }
        return notificationService.consultarHistoricoPessoaFisica(pessoaFisicaId);
    }
    
    /**
     * Consulta histórico de notificações para um cliente (Pessoa Jurídica).
     * 
     * @param pessoaJuridicaId ID da Pessoa Jurídica
     * @return Lista com histórico de notificações
     */
    public List<HistoricoNotificacao> consultarHistoricoNotificacoesPessoaJuridica(Long pessoaJuridicaId) 
            throws InvalidConfigurationException {
        if (notificationService == null) {
            throw new InvalidConfigurationException("Servico de notificacao nao disponivel.");
        }
        return notificationService.consultarHistoricoPessoaJuridica(pessoaJuridicaId);
    }
    
    /**
     * Obtém a configuração de limite para um hidrômetro.
     * 
     * @param hidrometroId ID do hidrômetro
     * @return Configuração de limite do hidrômetro
     */
    public ConfiguracaoLimiteConsumo obterConfiguracaoLimite(Long hidrometroId) 
            throws InvalidConfigurationException {
        if (notificationService == null) {
            throw new InvalidConfigurationException("Servico de notificacao nao disponivel.");
        }
        return notificationService.obterConfiguracao(hidrometroId);
    }
    
    /**
     * Desativa as notificações para um hidrômetro específico.
     * 
     * @param hidrometroId ID do hidrômetro
     */
    public void desativarNotificacoes(Long hidrometroId) 
            throws InvalidConfigurationException {
        if (notificationService == null) {
            throw new InvalidConfigurationException("Servico de notificacao nao disponivel.");
        }
        try {
            notificationService.desativarNotificacoes(hidrometroId);
            
            // Registra desativação na auditoria
            if (auditoriaService != null) {
                try {
                    auditoriaService.registrarMudancaConfiguracao(null, "ConfiguracaoLimiteConsumo", hidrometroId,
                        "Desativação de notificações", "ativo=true", "ativo=false");
                } catch (Exception e) {
                    // Silencia erros de auditoria
                }
            }
        } catch (Exception e) {
            if (auditoriaService != null) {
                try {
                    auditoriaService.registrarFalha(null, "CONFIG_CHANGE", "ConfiguracaoLimiteConsumo", hidrometroId,
                        "Falha ao desativar notificações", e.getMessage(), true);
                } catch (Exception ignored) {}
            }
            throw new InvalidConfigurationException("Erro ao desativar notificações: " + e.getMessage());
        }
    }
    
    /**
     * Reativa as notificações para um hidrômetro específico.
     * 
     * @param hidrometroId ID do hidrômetro
     */
    public void reativarNotificacoes(Long hidrometroId) 
            throws InvalidConfigurationException {
        if (notificationService == null) {
            throw new InvalidConfigurationException("Servico de notificacao nao disponivel.");
        }
        try {
            notificationService.reativarNotificacoes(hidrometroId);
            
            // Registra reativação na auditoria
            if (auditoriaService != null) {
                try {
                    auditoriaService.registrarMudancaConfiguracao(null, "ConfiguracaoLimiteConsumo", hidrometroId,
                        "Reativação de notificações", "ativo=false", "ativo=true");
                } catch (Exception e) {
                    // Silencia erros de auditoria
                }
            }
        } catch (Exception e) {
            if (auditoriaService != null) {
                try {
                    auditoriaService.registrarFalha(null, "CONFIG_CHANGE", "ConfiguracaoLimiteConsumo", hidrometroId,
                        "Falha ao reativar notificações", e.getMessage(), true);
                } catch (Exception ignored) {}
            }
            throw new InvalidConfigurationException("Erro ao reativar notificações: " + e.getMessage());
        }
    }
    
    // ===== MÉTODOS DE NOTIFICAÇÃO POR SHA =====
    
    /**
     * Configura um limite de consumo mensal para um hidrômetro usando SHA.
     * 
     * @param sha Identificador SHA do hidrômetro
     * @param limiteConsumMensal Limite em m³
     * @param percentualLimite Percentual que dispara notificação
     * @return Configuração criada/atualizada
     */
    public ConfiguracaoLimiteConsumo configurarLimiteNotificacaoPorSha(String sha, Long limiteConsumMensal, Integer percentualLimite)
            throws InvalidConfigurationException {
        if (hidrometroProxyFachada == null) {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
        Hidrometro hidrometro = hidrometroProxyFachada.obterPorSHA(sha);
        if (hidrometro == null) {
            throw new InvalidConfigurationException("Hidrometro com SHA " + sha + " nao encontrado.");
        }
        return configurarLimiteNotificacao(hidrometro.getId(), limiteConsumMensal, percentualLimite);
    }
    
    /**
     * Consulta histórico de notificações para um hidrômetro usando SHA.
     * 
     * @param sha Identificador SHA do hidrômetro
     * @return Lista com histórico de notificações
     */
    public List<HistoricoNotificacao> consultarHistoricoNotificacoesPorSha(String sha)
            throws InvalidConfigurationException {
        if (hidrometroProxyFachada == null) {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
        Hidrometro hidrometro = hidrometroProxyFachada.obterPorSHA(sha);
        if (hidrometro == null) {
            throw new InvalidConfigurationException("Hidrometro com SHA " + sha + " nao encontrado.");
        }
        return consultarHistoricoNotificacoesHidrometro(hidrometro.getId());
    }
    
    /**
     * Desativa as notificações para um hidrômetro usando SHA.
     * 
     * @param sha Identificador SHA do hidrômetro
     */
    public void desativarNotificacoesPorSha(String sha)
            throws InvalidConfigurationException {
        if (hidrometroProxyFachada == null) {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
        Hidrometro hidrometro = hidrometroProxyFachada.obterPorSHA(sha);
        if (hidrometro == null) {
            throw new InvalidConfigurationException("Hidrometro com SHA " + sha + " nao encontrado.");
        }
        desativarNotificacoes(hidrometro.getId());
    }
    
    /**
     * Reativa as notificações para um hidrômetro usando SHA.
     * 
     * @param sha Identificador SHA do hidrômetro
     */
    public void reativarNotificacoesPorSha(String sha)
            throws InvalidConfigurationException {
        if (hidrometroProxyFachada == null) {
            throw new InvalidConfigurationException("Servico de hidrometros nao disponivel.");
        }
        Hidrometro hidrometro = hidrometroProxyFachada.obterPorSHA(sha);
        if (hidrometro == null) {
            throw new InvalidConfigurationException("Hidrometro com SHA " + sha + " nao encontrado.");
        }
        reativarNotificacoes(hidrometro.getId());
    }
    /**
     * Consulta operações de auditoria de um administrador
     */
    public List<AuditoriaOperacao> consultarAuditoriaAdministrador(Long administradorId) 
            throws InvalidConfigurationException {
        if (auditoriaService == null) {
            throw new InvalidConfigurationException("Servico de auditoria nao disponivel.");
        }
        return auditoriaService.obterUltimasOperacoes(administradorId);
    }
    
    /**
     * Consulta operações críticas de um administrador
     */
    public String consultarOperacoesCriticas(Long administradorId) 
            throws InvalidConfigurationException {
        if (auditoriaService == null) {
            throw new InvalidConfigurationException("Servico de auditoria nao disponivel.");
        }
        var operacoesCriticas = auditoriaService.obterUltimasOperacoes(administradorId);
        StringBuilder sb = new StringBuilder();
        sb.append("=== OPERAÇÕES CRÍTICAS DO ADMINISTRADOR ===\n");
        for (var op : operacoesCriticas) {
            if (op.getCritica()) {
                sb.append("\n- Tipo: ").append(op.getTipoOperacao());
                sb.append("\n  Entidade: ").append(op.getTipoEntidade());
                sb.append("\n  Descrição: ").append(op.getDescricao());
                sb.append("\n  Resultado: ").append(op.getResultadoOperacao());
                sb.append("\n  Data: ").append(op.getDataOperacao());
            }
        }
        return sb.toString();
    }
    
    /**
     * Consulta histórico de auditoria de uma entidade
     */
    public String consultarAuditoriaEntidade(String tipoEntidade, Long entidadeId) 
            throws InvalidConfigurationException {
        if (auditoriaService == null) {
            throw new InvalidConfigurationException("Servico de auditoria nao disponivel.");
        }
        var operacoes = auditoriaService.obterOperacoesEntidade(tipoEntidade, entidadeId, 
                org.springframework.data.domain.PageRequest.of(0, 50)).getContent();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== AUDITORIA: ").append(tipoEntidade).append(" (ID: ").append(entidadeId).append(") ===\n");
        for (var op : operacoes) {
            sb.append("\n[").append(op.getTipoOperacao()).append("] ");
            sb.append(op.getDataOperacao()).append(" - ");
            sb.append("Admin: ").append(op.getAdministrador().getUsername()).append(" - ");
            sb.append("Resultado: ").append(op.getResultadoOperacao());
            if (op.getDescricao() != null) {
                sb.append("\n  Descrição: ").append(op.getDescricao());
            }
        }
        return sb.toString();
    }
}
