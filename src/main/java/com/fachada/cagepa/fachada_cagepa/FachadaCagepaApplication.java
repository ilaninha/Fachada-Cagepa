package com.fachada.cagepa.fachada_cagepa;

import com.fachada.cagepa.fachada_cagepa.infra.entities.HistoricoNotificacao;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidConfigurationException;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidCredentialsException;
import com.fachada.cagepa.fachada_cagepa.padroes.fachada.PainelCagepaFacade;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class FachadaCagepaApplication implements CommandLineRunner {

	@Autowired
	private PainelCagepaFacade painelCagepaFacade;

	public static void main(String[] args) {
		SpringApplication.run(FachadaCagepaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		exibirMenuAutenticacao(reader);
		
		if (painelCagepaFacade.estaAutenticado()) {
			exibirMenuPrincipal(reader);
		}
	}

	private void exibirMenuAutenticacao(BufferedReader reader) throws Exception {
		boolean autenticado = false;

		while (!autenticado) {
			exibirMenuLogin();
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					realizarLogin(reader);
					autenticado = painelCagepaFacade.estaAutenticado();
					break;
				case "2":
					System.out.println("Encerrando aplicacao...");
					System.exit(0);
					break;
				default:
					System.out.println("Opcao invalida! Tente novamente.");
			}
		}
	}

	private void exibirMenuLogin() {
		System.out.println("\n=== MENU DE AUTENTICACAO ===");
		System.out.println("1 - Login");
		System.out.println("2 - Sair");
		System.out.print("Selecione uma opcao: ");
	}

	private void realizarLogin(BufferedReader reader) {
		try {
			System.out.print("Username: ");
			String username = reader.readLine().trim();

			System.out.print("Senha: ");
			String password = reader.readLine().trim();

			painelCagepaFacade.login(username, password);
			System.out.println("Autenticacao realizada com sucesso!");

		} catch (InvalidCredentialsException e) {
			System.err.println("Erro na autenticacao: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void exibirMenuPrincipal(BufferedReader reader) throws Exception {
		boolean sair = false;

		while (!sair) {
			exibirMenu();
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					configurarDiretorio(reader);
					break;
				case "2":
					exibirDiretorioAtual();
					break;
				case "3":
					criarAdministrador(reader);
					break;
				case "4":
					desativarAdministrador(reader);
					break;
				case "5":
					exibirMenuClientes(reader);
					break;
				case "6":
					exibirMenuConsumo(reader);
					break;
				case "7":
					exibirMenuAuditoria(reader);
					break;
				case "8":
					logout();
					sair = true;
					break;
				default:
					System.out.println("Opcao invalida! Tente novamente.");
			}
		}
	}

	private void exibirMenu() {
		System.out.println("\n=== MENU PRINCIPAL ===");
		System.out.println("1 - Configurar diretorio de imagens");
		System.out.println("2 - Exibir diretorio atual");
		System.out.println("3 - Criar administrador");
		System.out.println("4 - Desativar administrador");
		System.out.println("5 - Gerenciar Clientes");
		System.out.println("6 - Consultar Consumo de Agua");
		System.out.println("7 - Consultar Auditoria");
		System.out.println("8 - Logout");
		System.out.print("Selecione uma opcao: ");
	}

	private void configurarDiretorio(BufferedReader reader) {
		try {
			System.out.print("Insira o caminho do diretorio de imagens dos hidrometros: ");
			String caminho = reader.readLine().trim();

			if (caminho.isEmpty()) {
				System.out.println("Erro: O caminho nao pode estar vazio.");
				return;
			}

			painelCagepaFacade.configureImageDirectory(caminho);
			System.out.println("Configuracao salva com sucesso!");
			System.out.println("Diretorio configurado: " + caminho);

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro na configuracao: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void exibirDiretorioAtual() {
		if (painelCagepaFacade.isConfigured()) {
			String diretorio = painelCagepaFacade.getImageDirectory();
			System.out.println("\nDiretorio de imagens configurado: " + diretorio);
		} else {
			System.out.println("\nNenhum diretorio configurado ainda. Use a opcao 1 para configurar.");
		}
	}

	private void criarAdministrador(BufferedReader reader) {
		try {
			System.out.print("Username do novo administrador: ");
			String username = reader.readLine().trim();

			System.out.print("Senha do novo administrador: ");
			String password = reader.readLine().trim();

			painelCagepaFacade.criarAdministrador(username, password);
			System.out.println("Administrador criado com sucesso!");

		} catch (InvalidCredentialsException e) {
			System.err.println("Erro ao criar administrador: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void desativarAdministrador(BufferedReader reader) {
		try {
			System.out.print("Username do administrador a desativar: ");
			String username = reader.readLine().trim();

			painelCagepaFacade.desativarAdministrador(username);
			System.out.println("Administrador desativado com sucesso!");

		} catch (InvalidCredentialsException e) {
			System.err.println("Erro ao desativar administrador: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void logout() {
		painelCagepaFacade.logout();
		painelCagepaFacade.pararMonitoramento();
		System.out.println("Logout realizado. Encerrando aplicacao...");
	}

	private void exibirMenuClientes(BufferedReader reader) throws Exception {
		boolean sairSubMenu = false;

		while (!sairSubMenu) {
			exibirMenuClientesOpcoes();
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					exibirMenuPessoaFisica(reader);
					break;
				case "2":
					exibirMenuPessoaJuridica(reader);
					break;
				case "3":
					exibirMenuEnderecos(reader);
					break;
				case "4":
					exibirMenuHidrometros(reader);
					break;
				case "5":
					sairSubMenu = true;
					break;
				default:
					System.out.println("Opcao invalida! Tente novamente.");
			}
		}
	}

	private void exibirMenuClientesOpcoes() {
		System.out.println("\n=== MENU DE CLIENTES ===");
		System.out.println("1 - Pessoa Fisica");
		System.out.println("2 - Pessoa Juridica");
		System.out.println("3 - Gerenciar Enderecos");
		System.out.println("4 - Gerenciar Hidrometros (SHAs)");
		System.out.println("5 - Voltar");
		System.out.print("Selecione uma opcao: ");
	}

	private void exibirMenuPessoaFisica(BufferedReader reader) throws Exception {
		boolean sairSubMenu = false;

		while (!sairSubMenu) {
			exibirMenuPessoaFisicaOpcoes();
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					criarPessoaFisica(reader);
					break;
				case "2":
					listarPessoasFisicas();
					break;
				case "3":
					consultarPessoaFisica(reader);
					break;
				case "4":
					inativarPessoaFisica(reader);
					break;
				case "5":
					gerenciarSHAsPessoaFisica(reader);
					break;
				case "6":
					sairSubMenu = true;
					break;
				default:
					System.out.println("Opcao invalida! Tente novamente.");
			}
		}
	}

	private void exibirMenuPessoaFisicaOpcoes() {
		System.out.println("\n=== MENU PESSOA FISICA ===");
		System.out.println("1 - Criar");
		System.out.println("2 - Listar");
		System.out.println("3 - Consultar");
		System.out.println("4 - Inativar");
		System.out.println("5 - Gerenciar SHAs (Hidrometros)");
		System.out.println("6 - Voltar");
		System.out.print("Selecione uma opcao: ");
	}

	private void exibirMenuPessoaJuridica(BufferedReader reader) throws Exception {
		boolean sairSubMenu = false;

		while (!sairSubMenu) {
			exibirMenuPessoaJuridicaOpcoes();
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					criarPessoaJuridica(reader);
					break;
				case "2":
					listarPessoasJuridicas();
					break;
				case "3":
					consultarPessoaJuridica(reader);
					break;
				case "4":
					inativarPessoaJuridica(reader);
					break;
				case "5":
					gerenciarSHAsPessoaJuridica(reader);
					break;
				case "6":
					sairSubMenu = true;
					break;
				default:
					System.out.println("Opcao invalida! Tente novamente.");
			}
		}
	}

	private void exibirMenuPessoaJuridicaOpcoes() {
		System.out.println("\n=== MENU PESSOA JURIDICA ===");
		System.out.println("1 - Criar");
		System.out.println("2 - Listar");
		System.out.println("3 - Consultar");
		System.out.println("4 - Inativar");
		System.out.println("5 - Gerenciar SHAs (Hidrometros)");
		System.out.println("6 - Voltar");
		System.out.print("Selecione uma opcao: ");
	}

	private void criarPessoaFisica(BufferedReader reader) {
		try {
			System.out.print("Nome: ");
			String nome = reader.readLine().trim();

			System.out.print("CPF (ex: 123.456.789-00): ");
			String cpf = reader.readLine().trim();

			System.out.print("Email: ");
			String email = reader.readLine().trim();

			System.out.print("Telefone (ex: 11 98765-4321): ");
			String telefone = reader.readLine().trim();

			painelCagepaFacade.criarPessoaFisica(nome, cpf, email, telefone);

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao criar Pessoa Fisica: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void listarPessoasFisicas() {
		try {
			var pessoasFisicas = painelCagepaFacade.listarPessoasFisicas();

			if (pessoasFisicas.isEmpty()) {
				System.out.println("\nNenhuma Pessoa Fisica cadastrada.");
				return;
			}

			System.out.println("\n=== PESSOAS FISICAS CADASTRADAS ===");
			for (var pf : pessoasFisicas) {
				System.out.println("ID: " + pf.getId() + " | Nome: " + pf.getNome() + " | CPF: " + pf.getCpf());
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao listar: " + e.getMessage());
		}
	}

	private void consultarPessoaFisica(BufferedReader reader) throws IOException {
		try {
			System.out.println("\n=== CONSULTAR PESSOA FISICA ===");
			System.out.println("1 - Por CPF");
			System.out.println("2 - Por Email");
			System.out.println("3 - Por Telefone");
			System.out.print("Selecione a opcao: ");
			String opcao = reader.readLine().trim();

			var pf = switch (opcao) {
				case "1" -> {
					System.out.print("CPF: ");
					String cpf = reader.readLine().trim();
					yield painelCagepaFacade.obterPessoaFisicaPorCPF(cpf);
				}
				case "2" -> {
					System.out.print("Email: ");
					String email = reader.readLine().trim();
					yield painelCagepaFacade.obterPessoaFisicaPorEmail(email);
				}
				case "3" -> {
					System.out.print("Telefone: ");
					String telefone = reader.readLine().trim();
					yield painelCagepaFacade.obterPessoaFisicaPorTelefone(telefone);
				}
				default -> throw new InvalidConfigurationException("Opcao invalida!");
			};

			System.out.println("\n=== DADOS PESSOA FISICA ===");
			System.out.println("ID: " + pf.getId());
			System.out.println("Nome: " + pf.getNome());
			System.out.println("CPF: " + pf.getCpf());
			System.out.println("Email: " + pf.getEmail());
			System.out.println("Telefone: " + pf.getTelefone());
			System.out.println("Ativo: " + (pf.isAtivo() ? "Sim" : "Nao"));

			var enderecos = painelCagepaFacade.listarEnderecosPessoaFisica(pf.getId());
			if (!enderecos.isEmpty()) {
				System.out.println("\nEnderecos:");
				for (var endereco : enderecos) {
					System.out.println("  - " + endereco.getLogradouro() + ", " + endereco.getNumero() + " - " + endereco.getCidade() + "/" + endereco.getEstado());
				}
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void inativarPessoaFisica(BufferedReader reader) throws IOException {
		try {
			System.out.println("\n=== INATIVAR PESSOA FISICA ===");
			System.out.println("1 - Por CPF");
			System.out.println("2 - Por Email");
			System.out.println("3 - Por Telefone");
			System.out.print("Selecione a opcao: ");
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1" -> {
					System.out.print("CPF: ");
					String cpf = reader.readLine().trim();
					painelCagepaFacade.inativarPessoaFisicaPorCPF(cpf);
					System.out.println("Pessoa Fisica inativada com sucesso!");
				}
				case "2" -> {
					System.out.print("Email: ");
					String email = reader.readLine().trim();
					painelCagepaFacade.inativarPessoaFisicaPorEmail(email);
					System.out.println("Pessoa Fisica inativada com sucesso!");
				}
				case "3" -> {
					System.out.print("Telefone: ");
					String telefone = reader.readLine().trim();
					painelCagepaFacade.inativarPessoaFisicaPorTelefone(telefone);
					System.out.println("Pessoa Fisica inativada com sucesso!");
				}
				default -> System.out.println("Opcao invalida!");
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void criarPessoaJuridica(BufferedReader reader) {
		try {
			System.out.print("Razao Social: ");
			String razaoSocial = reader.readLine().trim();

			System.out.print("CNPJ (ex: 12.345.678/0001-90): ");
			String cnpj = reader.readLine().trim();

			System.out.print("Email: ");
			String email = reader.readLine().trim();

			System.out.print("Telefone (ex: 11 98765-4321): ");
			String telefone = reader.readLine().trim();

			painelCagepaFacade.criarPessoaJuridica(razaoSocial, cnpj, email, telefone);

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao criar Pessoa Juridica: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void listarPessoasJuridicas() {
		try {
			var pessoasJuridicas = painelCagepaFacade.listarPessoasJuridicas();

			if (pessoasJuridicas.isEmpty()) {
				System.out.println("\nNenhuma Pessoa Juridica cadastrada.");
				return;
			}

			System.out.println("\n=== PESSOAS JURIDICAS CADASTRADAS ===");
			for (var pj : pessoasJuridicas) {
				System.out.println("ID: " + pj.getId() + " | Razao Social: " + pj.getRazaoSocial() + " | CNPJ: " + pj.getCnpj());
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao listar: " + e.getMessage());
		}
	}

	private void consultarPessoaJuridica(BufferedReader reader) throws IOException {
		try {
			System.out.println("\n=== CONSULTAR PESSOA JURIDICA ===");
			System.out.println("1 - Por CNPJ");
			System.out.println("2 - Por Email");
			System.out.println("3 - Por Telefone");
			System.out.print("Selecione a opcao: ");
			String opcao = reader.readLine().trim();

			var pj = switch (opcao) {
				case "1" -> {
					System.out.print("CNPJ: ");
					String cnpj = reader.readLine().trim();
					yield painelCagepaFacade.obterPessoaJuridicaPorCNPJ(cnpj);
				}
				case "2" -> {
					System.out.print("Email: ");
					String email = reader.readLine().trim();
					yield painelCagepaFacade.obterPessoaJuridicaPorEmail(email);
				}
				case "3" -> {
					System.out.print("Telefone: ");
					String telefone = reader.readLine().trim();
					yield painelCagepaFacade.obterPessoaJuridicaPorTelefone(telefone);
				}
				default -> throw new InvalidConfigurationException("Opcao invalida!");
			};

			System.out.println("\n=== DADOS PESSOA JURIDICA ===");
			System.out.println("ID: " + pj.getId());
			System.out.println("Razao Social: " + pj.getRazaoSocial());
			System.out.println("CNPJ: " + pj.getCnpj());
			System.out.println("Email: " + pj.getEmail());
			System.out.println("Telefone: " + pj.getTelefone());
			System.out.println("Ativo: " + (pj.isAtivo() ? "Sim" : "Nao"));

			var enderecos = painelCagepaFacade.listarEnderecosPessoaJuridica(pj.getId());
			if (!enderecos.isEmpty()) {
				System.out.println("\nEnderecos:");
				for (var endereco : enderecos) {
					System.out.println("  - " + endereco.getLogradouro() + ", " + endereco.getNumero() + " - " + endereco.getCidade() + "/" + endereco.getEstado());
				}
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void inativarPessoaJuridica(BufferedReader reader) throws IOException {
		try {
			System.out.println("\n=== INATIVAR PESSOA JURIDICA ===");
			System.out.println("1 - Por CNPJ");
			System.out.println("2 - Por Email");
			System.out.println("3 - Por Telefone");
			System.out.print("Selecione a opcao: ");
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1" -> {
					System.out.print("CNPJ: ");
					String cnpj = reader.readLine().trim();
					painelCagepaFacade.inativarPessoaJuridicaPorCNPJ(cnpj);
					System.out.println("Pessoa Juridica inativada com sucesso!");
				}
				case "2" -> {
					System.out.print("Email: ");
					String email = reader.readLine().trim();
					painelCagepaFacade.inativarPessoaJuridicaPorEmail(email);
					System.out.println("Pessoa Juridica inativada com sucesso!");
				}
				case "3" -> {
					System.out.print("Telefone: ");
					String telefone = reader.readLine().trim();
					painelCagepaFacade.inativarPessoaJuridicaPorTelefone(telefone);
					System.out.println("Pessoa Juridica inativada com sucesso!");
				}
				default -> System.out.println("Opcao invalida!");
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void exibirMenuHidrometros(BufferedReader reader) throws Exception {
		boolean sairSubMenu = false;

		while (!sairSubMenu) {
			exibirMenuHidrometrosOpcoes();
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					cadastrarHidrometro(reader);
					break;
				case "2":
					listarHidrometrosPorCliente(reader);
					break;
				case "3":
					buscarHidrometroPorSHA(reader);
					break;
				case "4":
					ativarDesativarHidrometro(reader);
					break;
				case "5":
					sairSubMenu = true;
					break;
				default:
					System.out.println("Opcao invalida! Tente novamente.");
			}
		}
	}

	private void exibirMenuHidrometrosOpcoes() {
		System.out.println("\n=== MENU HIDROMETROS ===");
		System.out.println("1 - Cadastrar Hidrometro");
		System.out.println("2 - Listar Hidrometros por Cliente");
		System.out.println("3 - Buscar Hidrometro por SHA");
		System.out.println("4 - Ativar/Desativar Hidrometro");
		System.out.println("5 - Voltar");
		System.out.print("Selecione uma opcao: ");
	}

	private void cadastrarHidrometro(BufferedReader reader) {
		try {
			System.out.println("\n=== CADASTRAR HIDROMETRO ===");
			System.out.println("1 - Pessoa Fisica com Endereco Existente");
			System.out.println("2 - Pessoa Fisica com Novo Endereco");
			System.out.println("3 - Pessoa Juridica com Endereco Existente");
			System.out.println("4 - Pessoa Juridica com Novo Endereco");
			System.out.print("Selecione uma opcao: ");
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					cadastrarHidrometroPFEnderecoExistente(reader);
					break;
				case "2":
					cadastrarHidrometroPFNovoEndereco(reader);
					break;
				case "3":
					cadastrarHidrometroPJEnderecoExistente(reader);
					break;
				case "4":
					cadastrarHidrometroPJNovoEndereco(reader);
					break;
				default:
					System.out.println("Opcao invalida!");
			}

		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void cadastrarHidrometroPFEnderecoExistente(BufferedReader reader) throws IOException {
		try {
			System.out.print("SHA do hidrometro (64 caracteres hexadecimais): ");
			String sha = reader.readLine().trim();

			System.out.print("Limite de consumo mensal (em litros): ");
			Long limiteConsumo = Long.parseLong(reader.readLine().trim());

			System.out.print("CPF da Pessoa Fisica: ");
			String cpf = reader.readLine().trim();
			var pessoaFisica = painelCagepaFacade.obterPessoaFisicaPorCPF(cpf);
			if (pessoaFisica == null) {
				System.out.println("Pessoa Fisica nao encontrada com o CPF fornecido.");
				return;
			}
			Long pessoaFisicaId = pessoaFisica.getId();

			var enderecosDisp = painelCagepaFacade.listarEnderecosPessoaFisica(pessoaFisicaId);
			if (enderecosDisp.isEmpty()) {
				System.out.println("Nenhum endereco cadastrado para esta Pessoa Fisica.");
				return;
			}

			System.out.println("\nEnderecos disponiveis:");
			for (var e : enderecosDisp) {
				System.out.println("ID: " + e.getId() + " | " + e.getLogradouro() + ", " + e.getNumero() + " - " + e.getCidade() + "/" + e.getEstado());
			}

			System.out.print("Selecione o ID do endereco: ");
			Long enderecoId = Long.parseLong(reader.readLine().trim());

			painelCagepaFacade.cadastrarHidrometroPessoaFisica(sha, limiteConsumo, pessoaFisicaId, enderecoId);
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void cadastrarHidrometroPFNovoEndereco(BufferedReader reader) throws IOException {
		try {
			System.out.print("SHA do hidrometro (64 caracteres hexadecimais): ");
			String sha = reader.readLine().trim();

			System.out.print("Limite de consumo mensal (em litros): ");
			Long limiteConsumo = Long.parseLong(reader.readLine().trim());

			System.out.print("CPF da Pessoa Fisica: ");
			String cpf = reader.readLine().trim();
			var pessoaFisica = painelCagepaFacade.obterPessoaFisicaPorCPF(cpf);
			if (pessoaFisica == null) {
				System.out.println("Pessoa Fisica nao encontrada com o CPF fornecido.");
				return;
			}
			Long pessoaFisicaId = pessoaFisica.getId();

			System.out.print("Logradouro: ");
			String logradouro = reader.readLine().trim();

			System.out.print("Numero: ");
			String numero = reader.readLine().trim();

			System.out.print("Complemento (opcional): ");
			String complemento = reader.readLine().trim();

			System.out.print("Bairro: ");
			String bairro = reader.readLine().trim();

			System.out.print("Cidade: ");
			String cidade = reader.readLine().trim();

			System.out.print("Estado (UF): ");
			String estado = reader.readLine().trim();

			System.out.print("CEP (8 digitos): ");
			String cep = reader.readLine().trim();

			painelCagepaFacade.cadastrarHidrometroPessoaFisicaComEndereco(sha, limiteConsumo, pessoaFisicaId,
					logradouro, numero, complemento, bairro, cidade, estado, cep);
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void cadastrarHidrometroPJEnderecoExistente(BufferedReader reader) throws IOException {
		try {
			System.out.print("SHA do hidrometro (64 caracteres hexadecimais): ");
			String sha = reader.readLine().trim();

			System.out.print("Limite de consumo mensal (em litros): ");
			Long limiteConsumo = Long.parseLong(reader.readLine().trim());

			System.out.print("ID da Pessoa Juridica: ");
			Long pessoaJuridicaId = Long.parseLong(reader.readLine().trim());

			var enderecosDisp = painelCagepaFacade.listarEnderecosPessoaJuridica(pessoaJuridicaId);
			if (enderecosDisp.isEmpty()) {
				System.out.println("Nenhum endereco cadastrado para esta Pessoa Juridica.");
				return;
			}

			System.out.println("\nEnderecos disponiveis:");
			for (var e : enderecosDisp) {
				System.out.println("ID: " + e.getId() + " | " + e.getLogradouro() + ", " + e.getNumero() + " - " + e.getCidade() + "/" + e.getEstado());
			}

			System.out.print("Selecione o ID do endereco: ");
			Long enderecoId = Long.parseLong(reader.readLine().trim());

			painelCagepaFacade.cadastrarHidrometroPessoaJuridica(sha, limiteConsumo, pessoaJuridicaId, enderecoId);
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void cadastrarHidrometroPJNovoEndereco(BufferedReader reader) throws IOException {
		try {
			System.out.print("SHA do hidrometro (64 caracteres hexadecimais): ");
			String sha = reader.readLine().trim();

			System.out.print("Limite de consumo mensal (em litros): ");
			Long limiteConsumo = Long.parseLong(reader.readLine().trim());

			System.out.print("ID da Pessoa Juridica: ");
			Long pessoaJuridicaId = Long.parseLong(reader.readLine().trim());

			System.out.print("Logradouro: ");
			String logradouro = reader.readLine().trim();

			System.out.print("Numero: ");
			String numero = reader.readLine().trim();

			System.out.print("Complemento (opcional): ");
			String complemento = reader.readLine().trim();

			System.out.print("Bairro: ");
			String bairro = reader.readLine().trim();

			System.out.print("Cidade: ");
			String cidade = reader.readLine().trim();

			System.out.print("Estado (UF): ");
			String estado = reader.readLine().trim();

			System.out.print("CEP (8 digitos): ");
			String cep = reader.readLine().trim();

			painelCagepaFacade.cadastrarHidrometroPessoaJuridicaComEndereco(sha, limiteConsumo, pessoaJuridicaId,
					logradouro, numero, complemento, bairro, cidade, estado, cep);
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void listarHidrometrosPorCliente(BufferedReader reader) throws IOException {
		try {
			System.out.println("\n=== LISTAR HIDROMETROS ===");
			System.out.println("1 - Pessoa Fisica");
			System.out.println("2 - Pessoa Juridica");
			System.out.print("Selecione uma opcao: ");
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1" -> {
					System.out.print("CPF da Pessoa Fisica: ");
					String cpf = reader.readLine().trim();
					var hidrometros = painelCagepaFacade.listarHidrometrosPorCPF(cpf);

					if (hidrometros.isEmpty()) {
						System.out.println("Nenhum hidrometro cadastrado.");
						return;
					}

					System.out.println("\n=== HIDROMETROS ===");
					for (var h : hidrometros) {
						System.out.println("ID: " + h.getId() + " | SHA: " + h.getSha() + " | Limite: " + h.getLimiteConsumoMensal() +
								"L | Ativo: " + (h.isAtivo() ? "Sim" : "Nao"));
					}
				}
				case "2" -> {
					System.out.print("CNPJ da Pessoa Juridica: ");
					String cnpj = reader.readLine().trim();
					var hidrometros = painelCagepaFacade.listarHidrometrosPorCNPJ(cnpj);

					if (hidrometros.isEmpty()) {
						System.out.println("Nenhum hidrometro cadastrado.");
						return;
					}

					System.out.println("\n=== HIDROMETROS ===");
					for (var h : hidrometros) {
						System.out.println("ID: " + h.getId() + " | SHA: " + h.getSha() + " | Limite: " + h.getLimiteConsumoMensal() +
								"L | Ativo: " + (h.isAtivo() ? "Sim" : "Nao"));
					}
				}
				default -> System.out.println("Opcao invalida!");
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void buscarHidrometroPorSHA(BufferedReader reader) {
		try {
			System.out.print("Digite o SHA do hidrometro: ");
			String sha = reader.readLine().trim();

			var hidrometro = painelCagepaFacade.obterHidrometroPorSHA(sha);

			System.out.println("\n=== DADOS HIDROMETRO ===");
			System.out.println("ID: " + hidrometro.getId());
			System.out.println("SHA: " + hidrometro.getSha());
			System.out.println("Limite de Consumo: " + hidrometro.getLimiteConsumoMensal() + "L");
			System.out.println("Ativo: " + (hidrometro.isAtivo() ? "Sim" : "Nao"));
			System.out.println("Endereco: " + hidrometro.getEndereco().getLogradouro() + ", " +
					hidrometro.getEndereco().getNumero() + " - " + hidrometro.getEndereco().getCidade() + "/" +
					hidrometro.getEndereco().getEstado());

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void ativarDesativarHidrometro(BufferedReader reader) {
		try {
			System.out.println("\n=== ATIVAR/DESATIVAR HIDROMETRO ===");
			System.out.println("1 - Ativar");
			System.out.println("2 - Desativar");
			System.out.print("Selecione uma opcao: ");
			String opcao = reader.readLine().trim();

			System.out.print("ID do hidrometro: ");
			Long hidrometroId = Long.parseLong(reader.readLine().trim());

			switch (opcao) {
				case "1":
					painelCagepaFacade.ativarHidrometro(hidrometroId);
					System.out.println("Hidrometro ativado com sucesso!");
					break;
				case "2":
					painelCagepaFacade.desativarHidrometro(hidrometroId);
					System.out.println("Hidrometro desativado com sucesso!");
					break;
				default:
					System.out.println("Opcao invalida!");
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	// ========== GERENCIAR SHAs (HIDROMETROS) ==========

	private void gerenciarSHAsPessoaFisica(BufferedReader reader) throws IOException {
		try {
			System.out.print("CPF da Pessoa Fisica (ex: 123.456.789-00): ");
			String cpf = reader.readLine().trim();
			var pessoaFisica = painelCagepaFacade.obterPessoaFisicaPorCPF(cpf);
			Long pessoaFisicaId = pessoaFisica.getId();

			boolean sairSubMenu = false;
			while (!sairSubMenu) {
				exibirMenuSHAsOpcoes();
				String opcao = reader.readLine().trim();

				switch (opcao) {
					case "1":
						cadastrarSHAPessoaFisica(reader, pessoaFisicaId);
						break;
					case "2":
						listarSHAsPessoaFisica(pessoaFisicaId);
						break;
					case "3":
						sairSubMenu = true;
						break;
					default:
						System.out.println("Opcao invalida!");
				}
			}
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void gerenciarSHAsPessoaJuridica(BufferedReader reader) throws IOException {
		try {
			System.out.print("CNPJ da Pessoa Juridica (ex: 12.345.678/0001-90): ");
			String cnpj = reader.readLine().trim();
			var pessoaJuridica = painelCagepaFacade.obterPessoaJuridicaPorCNPJ(cnpj);
			Long pessoaJuridicaId = pessoaJuridica.getId();

			boolean sairSubMenu = false;
			while (!sairSubMenu) {
				exibirMenuSHAsOpcoes();
				String opcao = reader.readLine().trim();

				switch (opcao) {
					case "1":
						cadastrarSHAPessoaJuridica(reader, pessoaJuridicaId);
						break;
					case "2":
						listarSHAsPessoaJuridica(pessoaJuridicaId);
						break;
					case "3":
						sairSubMenu = true;
						break;
					default:
						System.out.println("Opcao invalida!");
				}
			}
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void exibirMenuSHAsOpcoes() {
		System.out.println("\n=== GERENCIAR SHAs ===");
		System.out.println("1 - Cadastrar novo SHA");
		System.out.println("2 - Listar SHAs");
		System.out.println("3 - Voltar");
		System.out.print("Selecione uma opcao: ");
	}

	private void cadastrarSHAPessoaFisica(BufferedReader reader, Long pessoaFisicaId) throws IOException {
		try {
			System.out.print("SHA do hidrometro (64 caracteres hexadecimais): ");
			String sha = reader.readLine().trim();

			System.out.print("Limite de consumo mensal (em litros): ");
			Long limiteConsumo = Long.parseLong(reader.readLine().trim());

			var enderecosDisp = painelCagepaFacade.listarEnderecosPessoaFisica(pessoaFisicaId);

			if (enderecosDisp.isEmpty()) {
				System.out.println("\nNenhum endereco cadastrado. Deseja criar um novo?");
				System.out.print("Digite 'S' para sim, qualquer outra tecla para nao: ");
				String resposta = reader.readLine().trim().toUpperCase();

				if ("S".equals(resposta)) {
					cadastrarEnderecoPessoaFisica(reader, pessoaFisicaId);
					// Recarrega enderecos apos criar novo
					enderecosDisp = painelCagepaFacade.listarEnderecosPessoaFisica(pessoaFisicaId);
				} else {
					return;
				}
			}

			System.out.println("\nEnderecos disponiveis:");
			for (var e : enderecosDisp) {
				System.out.println("ID: " + e.getId() + " | " + e.getLogradouro() + ", " + e.getNumero() + " - " + e.getCidade() + "/" + e.getEstado());
			}

			System.out.print("Selecione o ID do endereco: ");
			Long enderecoId = Long.parseLong(reader.readLine().trim());

			painelCagepaFacade.cadastrarHidrometroPessoaFisica(sha, limiteConsumo, pessoaFisicaId, enderecoId);
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void cadastrarSHAPessoaJuridica(BufferedReader reader, Long pessoaJuridicaId) throws IOException {
		try {
			System.out.print("SHA do hidrometro (64 caracteres hexadecimais): ");
			String sha = reader.readLine().trim();

			System.out.print("Limite de consumo mensal (em litros): ");
			Long limiteConsumo = Long.parseLong(reader.readLine().trim());

			var enderecosDisp = painelCagepaFacade.listarEnderecosPessoaJuridica(pessoaJuridicaId);

			if (enderecosDisp.isEmpty()) {
				System.out.println("\nNenhum endereco cadastrado. Deseja criar um novo?");
				System.out.print("Digite 'S' para sim, qualquer outra tecla para nao: ");
				String resposta = reader.readLine().trim().toUpperCase();

				if ("S".equals(resposta)) {
					cadastrarEnderecoPessoaJuridica(reader, pessoaJuridicaId);
					// Recarrega enderecos apos criar novo
					enderecosDisp = painelCagepaFacade.listarEnderecosPessoaJuridica(pessoaJuridicaId);
				} else {
					return;
				}
			}

			System.out.println("\nEnderecos disponiveis:");
			for (var e : enderecosDisp) {
				System.out.println("ID: " + e.getId() + " | " + e.getLogradouro() + ", " + e.getNumero() + " - " + e.getCidade() + "/" + e.getEstado());
			}

			System.out.print("Selecione o ID do endereco: ");
			Long enderecoId = Long.parseLong(reader.readLine().trim());

			painelCagepaFacade.cadastrarHidrometroPessoaJuridica(sha, limiteConsumo, pessoaJuridicaId, enderecoId);
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void listarSHAsPessoaFisica(Long pessoaFisicaId) {
		try {
			var hidrometros = painelCagepaFacade.listarHidrometrosPorPessoaFisica(pessoaFisicaId);

			if (hidrometros.isEmpty()) {
				System.out.println("\nNenhum SHA cadastrado.");
				return;
			}

			System.out.println("\n=== SHAs CADASTRADOS ===");
			for (var h : hidrometros) {
				String shaDisplay = h.getSha().length() > 16 ? h.getSha().substring(0, 16) + "..." : h.getSha();
				System.out.println("ID: " + h.getId() + " | SHA: " + shaDisplay + " | Limite: " +
						h.getLimiteConsumoMensal() + "L | Ativo: " + (h.isAtivo() ? "Sim" : "Nao"));
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void listarSHAsPessoaJuridica(Long pessoaJuridicaId) {
		try {
			var hidrometros = painelCagepaFacade.listarHidrometrosPorPessoaJuridica(pessoaJuridicaId);

			if (hidrometros.isEmpty()) {
				System.out.println("\nNenhum SHA cadastrado.");
				return;
			}

			System.out.println("\n=== SHAs CADASTRADOS ===");
			for (var h : hidrometros) {
				String shaDisplay = h.getSha().length() > 16 ? h.getSha().substring(0, 16) + "..." : h.getSha();
				System.out.println("ID: " + h.getId() + " | SHA: " + shaDisplay + " | Limite: " +
						h.getLimiteConsumoMensal() + "L | Ativo: " + (h.isAtivo() ? "Sim" : "Nao"));
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	// ========== GERENCIAR ENDERECOS ==========

	private void exibirMenuEnderecos(BufferedReader reader) throws IOException {
		try {
			System.out.println("\n=== GERENCIAR ENDERECOS ===");
			System.out.println("1 - Pessoa Fisica");
			System.out.println("2 - Pessoa Juridica");
			System.out.print("Selecione uma opcao: ");
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					gerenciarEnderecosPessoaFisica(reader);
					break;
				case "2":
					gerenciarEnderecosPessoaJuridica(reader);
					break;
				default:
					System.out.println("Opcao invalida!");
			}
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void gerenciarEnderecosPessoaFisica(BufferedReader reader) throws IOException {
		try {
			System.out.print("CPF da Pessoa Fisica (ex: 123.456.789-00): ");
			String cpf = reader.readLine().trim();
			var pessoaFisica = painelCagepaFacade.obterPessoaFisicaPorCPF(cpf);
			Long pessoaFisicaId = pessoaFisica.getId();

			boolean sairSubMenu = false;
			while (!sairSubMenu) {
				exibirMenuEnderecoOpcoes();
				String opcao = reader.readLine().trim();

				switch (opcao) {
					case "1":
						cadastrarEnderecoPessoaFisica(reader, pessoaFisicaId);
						break;
					case "2":
						listarEnderecosPessoaFisica(pessoaFisicaId);
						break;
					case "3":
						deletarEnderecoPessoaFisica(reader);
						break;
					case "4":
						sairSubMenu = true;
						break;
					default:
						System.out.println("Opcao invalida!");
				}
			}
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void gerenciarEnderecosPessoaJuridica(BufferedReader reader) throws IOException {
		try {
			System.out.print("CNPJ da Pessoa Juridica (ex: 12.345.678/0001-90): ");
			String cnpj = reader.readLine().trim();
			var pessoaJuridica = painelCagepaFacade.obterPessoaJuridicaPorCNPJ(cnpj);
			Long pessoaJuridicaId = pessoaJuridica.getId();

			boolean sairSubMenu = false;
			while (!sairSubMenu) {
				exibirMenuEnderecoOpcoes();
				String opcao = reader.readLine().trim();

				switch (opcao) {
					case "1":
						cadastrarEnderecoPessoaJuridica(reader, pessoaJuridicaId);
						break;
					case "2":
						listarEnderecosPessoaJuridica(pessoaJuridicaId);
						break;
					case "3":
						deletarEnderecoPessoaJuridica(reader);
						break;
					case "4":
						sairSubMenu = true;
						break;
					default:
						System.out.println("Opcao invalida!");
				}
			}
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void exibirMenuEnderecoOpcoes() {
		System.out.println("\n=== OPCOES ENDERECO ===");
		System.out.println("1 - Cadastrar novo endereco");
		System.out.println("2 - Listar enderecos");
		System.out.println("3 - Deletar endereco");
		System.out.println("4 - Voltar");
		System.out.print("Selecione uma opcao: ");
	}

	private void cadastrarEnderecoPessoaFisica(BufferedReader reader, Long pessoaFisicaId) throws IOException {
		try {
			System.out.print("Logradouro: ");
			String logradouro = reader.readLine().trim();

			System.out.print("Numero: ");
			String numero = reader.readLine().trim();

			System.out.print("Complemento (opcional): ");
			String complemento = reader.readLine().trim();

			System.out.print("Bairro: ");
			String bairro = reader.readLine().trim();

			System.out.print("Cidade: ");
			String cidade = reader.readLine().trim();

			System.out.print("Estado (UF): ");
			String estado = reader.readLine().trim();

			System.out.print("CEP (8 digitos): ");
			String cep = reader.readLine().trim();

			painelCagepaFacade.criarEnderecoPessoaFisica(pessoaFisicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
			System.out.println("Endereco cadastrado com sucesso!");
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void cadastrarEnderecoPessoaJuridica(BufferedReader reader, Long pessoaJuridicaId) throws IOException {
		try {
			System.out.print("Logradouro: ");
			String logradouro = reader.readLine().trim();

			System.out.print("Numero: ");
			String numero = reader.readLine().trim();

			System.out.print("Complemento (opcional): ");
			String complemento = reader.readLine().trim();

			System.out.print("Bairro: ");
			String bairro = reader.readLine().trim();

			System.out.print("Cidade: ");
			String cidade = reader.readLine().trim();

			System.out.print("Estado (UF): ");
			String estado = reader.readLine().trim();

			System.out.print("CEP (8 digitos): ");
			String cep = reader.readLine().trim();

			painelCagepaFacade.criarEnderecoPessoaJuridica(pessoaJuridicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
			System.out.println("Endereco cadastrado com sucesso!");
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void listarEnderecosPessoaFisica(Long pessoaFisicaId) {
		try {
			var enderecos = painelCagepaFacade.listarEnderecosPessoaFisica(pessoaFisicaId);

			if (enderecos.isEmpty()) {
				System.out.println("\nNenhum endereco cadastrado.");
				return;
			}

			System.out.println("\n=== ENDERECOS ===");
			for (var e : enderecos) {
				System.out.println("ID: " + e.getId() + " | " + e.getLogradouro() + ", " + e.getNumero() + " - " +
						e.getBairro() + " | " + e.getCidade() + "/" + e.getEstado());
			}
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void listarEnderecosPessoaJuridica(Long pessoaJuridicaId) {
		try {
			var enderecos = painelCagepaFacade.listarEnderecosPessoaJuridica(pessoaJuridicaId);

			if (enderecos.isEmpty()) {
				System.out.println("\nNenhum endereco cadastrado.");
				return;
			}

			System.out.println("\n=== ENDERECOS ===");
			for (var e : enderecos) {
				System.out.println("ID: " + e.getId() + " | " + e.getLogradouro() + ", " + e.getNumero() + " - " +
						e.getBairro() + " | " + e.getCidade() + "/" + e.getEstado());
			}
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void deletarEnderecoPessoaFisica(BufferedReader reader) throws IOException {
		try {
			System.out.print("ID do endereco a deletar: ");
			Long enderecoId = Long.parseLong(reader.readLine().trim());

			painelCagepaFacade.deletarEndereco(enderecoId);
			System.out.println("Endereco deletado com sucesso!");
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void deletarEnderecoPessoaJuridica(BufferedReader reader) throws IOException {
		try {
			System.out.print("ID do endereco a deletar: ");
			Long enderecoId = Long.parseLong(reader.readLine().trim());

			painelCagepaFacade.deletarEndereco(enderecoId);
			System.out.println("Endereco deletado com sucesso!");
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	private void exibirMenuConsumo(BufferedReader reader) throws Exception {
		boolean sairSubMenu = false;

		while (!sairSubMenu) {
			exibirMenuConsumoOpcoes();
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					consultarConsumoHidrometro(reader);
					break;
				case "2":
					consultarConsumoPessoaFisica(reader);
					break;
				case "3":
					consultarConsumoPessoaJuridica(reader);
					break;
				case "4":
					compararConsumo(reader);
					break;
				case "5":
					exibirMenuNotificacoes(reader);
					break;
				case "6":
					sairSubMenu = true;
					break;
				default:
					System.out.println("Opcao invalida! Tente novamente.");
			}
		}
	}

	private void exibirMenuConsumoOpcoes() {
		System.out.println("\n=== MENU DE CONSUMO ===");
		System.out.println("1 - Consumo de hidrometro especifico");
		System.out.println("2 - Consumo total de Pessoa Fisica (CPF)");
		System.out.println("3 - Consumo total de Pessoa Juridica (CNPJ)");
		System.out.println("4 - Comparar consumo entre periodos");
		System.out.println("5 - Gerenciamento de notificacoes");
		System.out.println("6 - Voltar");
		System.out.print("Selecione uma opcao: ");
	}
	
	private void exibirMenuNotificacoes(BufferedReader reader) throws Exception {
		boolean sairSubMenu = false;

		while (!sairSubMenu) {
			exibirMenuNotificacoesOpcoes();
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					configurarLimiteNotificacao(reader);
					break;
				case "2":
					verificarNotificacoes(reader);
					break;
				case "3":
					consultarHistoricoNotificacoes(reader);
					break;
				case "4":
					desativarNotificacoes(reader);
					break;
				case "5":
					reativarNotificacoes(reader);
					break;
				case "6":
					sairSubMenu = true;
					break;
				default:
					System.out.println("Opcao invalida! Tente novamente.");
			}
		}
	}

	private void exibirMenuNotificacoesOpcoes() {
		System.out.println("\n=== MENU DE NOTIFICACOES ===");
		System.out.println("1 - Configurar limite de consumo (por ID do hidrometro)");
		System.out.println("2 - Verificar notificacoes de consumo (por SHA)");
		System.out.println("3 - Consultar historico de notificacoes");
		System.out.println("4 - Desativar notificacoes");
		System.out.println("5 - Reativar notificacoes");
		System.out.println("6 - Voltar");
		System.out.print("Selecione uma opcao: ");
	}
	
	private void configurarLimiteNotificacao(BufferedReader reader) {
		try {
			System.out.print("Digite o SHA do hidrometro: ");
			String sha = reader.readLine().trim();

			System.out.print("Digite o limite mensal de consumo em m³: ");
			Long limiteConsumMensal = Long.parseLong(reader.readLine().trim());

			System.out.print("Digite o percentual que dispara notificacao (ex: 70): ");
			Integer percentualLimite = Integer.parseInt(reader.readLine().trim());

			var config = painelCagepaFacade.configurarLimiteNotificacaoPorSha(sha, limiteConsumMensal, percentualLimite);
			System.out.println("✓ Limite configurado com sucesso!");
			System.out.println("  Hidrometro ID: " + config.getHidrometro().getId());
			System.out.println("  Limite: " + config.getLimiteConsumMensal() + " m³");
			System.out.println("  Percentual: " + config.getPercentualLimite() + "%");

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao configurar limite: " + e.getMessage());
		} catch (NumberFormatException e) {
			System.err.println("Erro: Valores digitados nao sao validos. Use numeros inteiros.");
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}
	
	private void verificarNotificacoes(BufferedReader reader) {
		try {
			System.out.print("Digite o SHA do hidrometro: ");
			String sha = reader.readLine().trim();

			painelCagepaFacade.verificarEEnviarNotificacao(sha);
			System.out.println("✓ Notificacoes verificadas e enviadas se necessario!");

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao verificar notificacoes: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}
	
	private void consultarHistoricoNotificacoes(BufferedReader reader) {
		try {
			System.out.println("\n=== CONSULTAR HISTORICO ===");
			System.out.println("1 - Por SHA do hidrometro");
			System.out.println("2 - Por CPF (Pessoa Fisica)");
			System.out.println("3 - Por CNPJ (Pessoa Juridica)");
			System.out.print("Selecione uma opcao: ");
			String opcao = reader.readLine().trim();

			switch (opcao) {
				case "1":
					System.out.print("Digite o SHA do hidrometro: ");
					String sha = reader.readLine().trim();
					var historicoHidrometro = painelCagepaFacade.consultarHistoricoNotificacoesPorSha(sha);
					exibirHistoricoNotificacoes(historicoHidrometro);
					break;
				case "2":
					System.out.print("Digite o CPF (Pessoa Fisica): ");
					String cpf = reader.readLine().trim();
					Long pessoaFisicaId = painelCagepaFacade.obterIdPessoaFisicaPorCpf(cpf);
					var historicoPessoaFisica = painelCagepaFacade.consultarHistoricoNotificacoesPessoaFisica(pessoaFisicaId);
					exibirHistoricoNotificacoes(historicoPessoaFisica);
					break;
				case "3":
					System.out.print("Digite o CNPJ (Pessoa Juridica): ");
					String cnpj = reader.readLine().trim();
					Long pessoaJuridicaId = painelCagepaFacade.obterIdPessoaJuridicaPorCnpj(cnpj);
					var historicoPessoaJuridica = painelCagepaFacade.consultarHistoricoNotificacoesPessoaJuridica(pessoaJuridicaId);
					exibirHistoricoNotificacoes(historicoPessoaJuridica);
					break;
				default:
					System.out.println("Opcao invalida!");
			}

		} catch (NumberFormatException e) {
			System.err.println("Erro: CPF/CNPJ digitado nao e valido. Use um formato valido.");
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao consultar historico: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}
	
	private void exibirHistoricoNotificacoes(java.util.List<HistoricoNotificacao> historico) {
		if (historico.isEmpty()) {
			System.out.println("Nenhuma notificacao encontrada.");
		} else {
			System.out.println("\n=== HISTORICO DE NOTIFICACOES ===");
			for (var notif : historico) {
				System.out.println("\n- Data de Envio: " + notif.getDataEnvio());
				System.out.println("  Email: " + notif.getEmailDestino());
				System.out.println("  Consumo: " + notif.getConsumoMensal() + " m³");
				System.out.println("  Limite Configurado: " + notif.getLimitConfigurado() + " m³");
				System.out.println("  Percentual Atingido: " + notif.getPercentualAtingido() + "%");
				System.out.println("  Status: " + notif.getStatusEnvio());
				if (notif.getMensagem() != null && !notif.getMensagem().isEmpty()) {
					System.out.println("  Mensagem: " + notif.getMensagem());
				}
			}
		}
	}
	
	private void desativarNotificacoes(BufferedReader reader) {
		try {
			System.out.print("Digite o SHA do hidrometro para desativar notificacoes: ");
			String sha = reader.readLine().trim();

			painelCagepaFacade.desativarNotificacoesPorSha(sha);
			System.out.println("✓ Notificacoes desativadas com sucesso!");

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao desativar notificacoes: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}
	
	private void reativarNotificacoes(BufferedReader reader) {
		try {
			System.out.print("Digite o SHA do hidrometro para reativar notificacoes: ");
			String sha = reader.readLine().trim();

			painelCagepaFacade.reativarNotificacoesPorSha(sha);
			System.out.println("✓ Notificacoes reativadas com sucesso!");

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro ao reativar notificacoes: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar entrada: " + e.getMessage());
		}
	}

	private void consultarConsumoHidrometro(BufferedReader reader) {
		try {
			System.out.print("Digite o SHA do hidrometro: ");
			String sha = reader.readLine().trim();

			System.out.println("Periodos disponiveis: daily, weekly, monthly, annual");
			System.out.print("Digite o periodo: ");
			String periodo = reader.readLine().trim();

			var resultado = painelCagepaFacade.obterConsumoHidrometro(sha, periodo);

			System.out.println("\n=== RESULTADO DO CONSUMO ===");
			System.out.println("SHA: " + resultado.getShaHidrometro());
			System.out.println("Periodo: " + resultado.getPeriodDescription());
			System.out.println("Consumo: " + resultado.getConsumptionValue() + " m³");
			System.out.println("Data Inicio: " + resultado.getDataInicio());
			System.out.println("Data Fim: " + resultado.getDataFim());
			System.out.println("Leitura Inicial: " + resultado.getLeituraInicial() + " m³");
			System.out.println("Leitura Final: " + resultado.getLeituraFinal() + " m³");
			
			// Verificar automaticamente notificacoes de consumo
			try {
				painelCagepaFacade.verificarEEnviarNotificacao(sha);
			} catch (Exception notifEx) {
				// Notificacao nao afeta resultado de consumo
			}

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println("Erro: Periodo invalido! " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar: " + e.getMessage());
		}
	}

	private void consultarConsumoPessoaFisica(BufferedReader reader) {
		try {
			System.out.print("Digite o CPF (sem formatacao): ");
			String cpf = reader.readLine().trim();

			System.out.println("\nPeriodos disponiveis: daily, weekly, monthly, annual");
			System.out.print("Digite o periodo: ");
			String periodo = reader.readLine().trim();

			var resultado = painelCagepaFacade.obterConsumoPorCPF(cpf, periodo);

			System.out.println("\n=== CONSUMO TOTAL DA PESSOA FISICA ===");
			System.out.println("Periodo: " + resultado.getPeriodDescription());
			System.out.println("Consumo Total: " + resultado.getConsumptionValue() + " m³");
			System.out.println("Data Inicio: " + resultado.getDataInicio());
			System.out.println("Data Fim: " + resultado.getDataFim());

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println("Erro: Periodo invalido! " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar: " + e.getMessage());
		}
	}

	private void consultarConsumoPessoaJuridica(BufferedReader reader) {
		try {
			System.out.print("Digite o CNPJ (sem formatacao): ");
			String cnpj = reader.readLine().trim();

			System.out.println("\nPeriodos disponiveis: daily, weekly, monthly, annual");
			System.out.print("Digite o periodo: ");
			String periodo = reader.readLine().trim();

			var resultado = painelCagepaFacade.obterConsumoPorCNPJ(cnpj, periodo);

			System.out.println("\n=== CONSUMO TOTAL DA EMPRESA ===");
			System.out.println("Periodo: " + resultado.getPeriodDescription());
			System.out.println("Consumo Total: " + resultado.getConsumptionValue() + " m³");
			System.out.println("Data Inicio: " + resultado.getDataInicio());
			System.out.println("Data Fim: " + resultado.getDataFim());

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println("Erro: Periodo invalido! " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar: " + e.getMessage());
		}
	}

	private void compararConsumo(BufferedReader reader) {
		try {
			System.out.print("Digite o SHA do hidrometro: ");
			String sha = reader.readLine().trim();

			System.out.println("\nPeriodos disponiveis: daily, weekly, monthly, annual");
			System.out.print("Primeiro periodo: ");
			String periodo1 = reader.readLine().trim();

			System.out.print("Segundo periodo: ");
			String periodo2 = reader.readLine().trim();

			Long[] comparacao = painelCagepaFacade.compararConsumo(sha, periodo1, periodo2);

			System.out.println("\n=== COMPARACAO DE CONSUMO ===");
			System.out.println("SHA: " + sha);
			System.out.println("Consumo " + periodo1 + ": " + comparacao[0] + " m³");
			System.out.println("Consumo " + periodo2 + ": " + comparacao[1] + " m³");
			System.out.println("Variacao: " + (comparacao[2] > 0 ? "+" : "") + comparacao[2] + " m³");
			System.out.println("Tendencia: " + (comparacao[2] > 0 ? "AUMENTO" : comparacao[2] < 0 ? "REDUCAO" : "ESTAVEL"));

		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println("Erro: Periodo invalido! " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar: " + e.getMessage());
		}
	}

	private void exibirMenuAuditoria(BufferedReader reader) {
		boolean sair = false;
		while (!sair) {
			exibirMenuAuditoriaOpcoes();
			try {
				String opcao = reader.readLine().trim();

				switch (opcao) {
					case "1":
						consultarAuditoriaAdministrador(reader);
						break;
					case "2":
						consultarOperacoesCriticas(reader);
						break;
					case "3":
						consultarAuditoriaEntidade(reader);
						break;
					case "4":
						sair = true;
						break;
					default:
						System.out.println("Opcao invalida! Tente novamente.");
				}
			} catch (Exception e) {
				System.err.println("Erro ao processar entrada: " + e.getMessage());
			}
		}
	}

	private void exibirMenuAuditoriaOpcoes() {
		System.out.println("\n=== MENU DE AUDITORIA ===");
		System.out.println("1 - Consultar auditoria de administrador");
		System.out.println("2 - Consultar operacoes criticas");
		System.out.println("3 - Consultar auditoria de entidade");
		System.out.println("4 - Voltar ao menu principal");
		System.out.print("Selecione uma opcao: ");
	}

	private void consultarAuditoriaAdministrador(BufferedReader reader) {
		try {
			System.out.print("Digite o ID do administrador: ");
			Long administradorId = Long.parseLong(reader.readLine().trim());

			var operacoes = painelCagepaFacade.consultarAuditoriaAdministrador(administradorId);
			
			System.out.println("\n=== AUDITORIA DO ADMINISTRADOR (ID: " + administradorId + ") ===");
			if (operacoes.isEmpty()) {
				System.out.println("Nenhuma operacao encontrada para este administrador.");
			} else {
				for (var op : operacoes) {
					System.out.println("\n[" + op.getTipoOperacao() + "] " + op.getDataOperacao());
					System.out.println("  Entidade: " + op.getTipoEntidade());
					if (op.getEntidadeId() != null) {
						System.out.println("  ID Entidade: " + op.getEntidadeId());
					}
					System.out.println("  Descricao: " + op.getDescricao());
					System.out.println("  Resultado: " + op.getResultadoOperacao());
					if (op.getCritica()) {
						System.out.println("  ⚠️ OPERACAO CRITICA");
					}
				}
			}

		} catch (NumberFormatException e) {
			System.err.println("Erro: ID invalido!");
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar: " + e.getMessage());
		}
	}

	private void consultarOperacoesCriticas(BufferedReader reader) {
		try {
			System.out.print("Digite o ID do administrador: ");
			Long administradorId = Long.parseLong(reader.readLine().trim());

			String resultado = painelCagepaFacade.consultarOperacoesCriticas(administradorId);
			System.out.println("\n" + resultado);

		} catch (NumberFormatException e) {
			System.err.println("Erro: ID invalido!");
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar: " + e.getMessage());
		}
	}

	private void consultarAuditoriaEntidade(BufferedReader reader) {
		try {
			System.out.print("Digite o tipo de entidade (ex: PessoaFisica, PessoaJuridica, Hidrometro): ");
			String tipoEntidade = reader.readLine().trim();

			System.out.print("Digite o ID da entidade: ");
			Long entidadeId = Long.parseLong(reader.readLine().trim());

			String resultado = painelCagepaFacade.consultarAuditoriaEntidade(tipoEntidade, entidadeId);
			System.out.println("\n" + resultado);

		} catch (NumberFormatException e) {
			System.err.println("Erro: ID invalido!");
		} catch (InvalidConfigurationException e) {
			System.err.println("Erro: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao processar: " + e.getMessage());
		}
	}
}
