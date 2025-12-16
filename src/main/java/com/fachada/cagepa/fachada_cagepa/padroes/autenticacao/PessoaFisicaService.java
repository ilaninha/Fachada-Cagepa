package com.fachada.cagepa.fachada_cagepa.padroes.autenticacao;

import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaFisica;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.PessoaFisicaRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.config.ClienteValidator;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidConfigurationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PessoaFisicaService {

    private final PessoaFisicaRepository pessoaFisicaRepository;
    private final ClienteValidator clienteValidator;

    public PessoaFisicaService(PessoaFisicaRepository pessoaFisicaRepository, ClienteValidator clienteValidator) {
        this.pessoaFisicaRepository = pessoaFisicaRepository;
        this.clienteValidator = clienteValidator;
    }

    @Transactional
    public void criarPessoaFisica(String nome, String cpf, String email, String telefone) throws InvalidConfigurationException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new InvalidConfigurationException("Nome nao pode estar vazio.");
        }

        clienteValidator.validarCPF(cpf);
        clienteValidator.validarEmail(email);
        clienteValidator.validarTelefone(telefone);

        String cpfLimpo = clienteValidator.limparCPF(cpf);
        String telefoneLimpo = clienteValidator.limparTelefone(telefone);

        if (pessoaFisicaRepository.findByCpf(cpfLimpo).isPresent()) {
            throw new InvalidConfigurationException("CPF ja cadastrado.");
        }

        if (pessoaFisicaRepository.findByEmail(email).isPresent()) {
            throw new InvalidConfigurationException("Email ja cadastrado.");
        }

        if (pessoaFisicaRepository.findByTelefone(telefoneLimpo).isPresent()) {
            throw new InvalidConfigurationException("Telefone ja cadastrado.");
        }

        PessoaFisica pessoaFisica = PessoaFisica.builder()
                .nome(nome.trim())
                .cpf(cpfLimpo)
                .email(email.trim())
                .telefone(telefoneLimpo)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .build();

        pessoaFisicaRepository.save(pessoaFisica);
    }

    @Transactional
    public void inativarPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = pessoaFisicaRepository.findById(pessoaFisicaId)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Fisica nao encontrada."));

        pessoaFisica.setAtivo(false);
        pessoaFisica.setDataInativacao(LocalDateTime.now());
        pessoaFisicaRepository.save(pessoaFisica);
    }

    public PessoaFisica obterPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        return pessoaFisicaRepository.findById(pessoaFisicaId)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Fisica nao encontrada."));
    }

    public PessoaFisica obterPorCPF(String cpf) throws InvalidConfigurationException {
        String cpfLimpo = clienteValidator.limparCPF(cpf);
        return pessoaFisicaRepository.findByCpf(cpfLimpo)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Fisica com CPF " + cpf + " nao encontrada."));
    }

    public PessoaFisica obterPorEmail(String email) throws InvalidConfigurationException {
        return pessoaFisicaRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Fisica com email " + email + " nao encontrada."));
    }

    public PessoaFisica obterPorTelefone(String telefone) throws InvalidConfigurationException {
        String telefoneLimpo = clienteValidator.limparTelefone(telefone);
        return pessoaFisicaRepository.findByTelefone(telefoneLimpo)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Fisica com telefone " + telefone + " nao encontrada."));
    }

    @Transactional
    public void inativarPorCPF(String cpf) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPorCPF(cpf);
        pessoaFisica.setAtivo(false);
        pessoaFisica.setDataInativacao(LocalDateTime.now());
        pessoaFisicaRepository.save(pessoaFisica);
    }

    @Transactional
    public void inativarPorEmail(String email) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPorEmail(email);
        pessoaFisica.setAtivo(false);
        pessoaFisica.setDataInativacao(LocalDateTime.now());
        pessoaFisicaRepository.save(pessoaFisica);
    }

    @Transactional
    public void inativarPorTelefone(String telefone) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = obterPorTelefone(telefone);
        pessoaFisica.setAtivo(false);
        pessoaFisica.setDataInativacao(LocalDateTime.now());
        pessoaFisicaRepository.save(pessoaFisica);
    }

    public List<PessoaFisica> listarPessoasFisicas() {
        return pessoaFisicaRepository.findByAtivo(true);
    }

    public List<PessoaFisica> listarTodas() {
        return pessoaFisicaRepository.findAll();
    }
}
