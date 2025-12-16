package com.fachada.cagepa.fachada_cagepa.padroes.autenticacao;

import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaJuridica;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.PessoaJuridicaRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.config.ClienteValidator;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidConfigurationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PessoaJuridicaService {

    private final PessoaJuridicaRepository pessoaJuridicaRepository;
    private final ClienteValidator clienteValidator;

    public PessoaJuridicaService(PessoaJuridicaRepository pessoaJuridicaRepository, ClienteValidator clienteValidator) {
        this.pessoaJuridicaRepository = pessoaJuridicaRepository;
        this.clienteValidator = clienteValidator;
    }

    @Transactional
    public void criarPessoaJuridica(String razaoSocial, String cnpj, String email, String telefone) throws InvalidConfigurationException {
        if (razaoSocial == null || razaoSocial.trim().isEmpty()) {
            throw new InvalidConfigurationException("Razao Social nao pode estar vazia.");
        }

        clienteValidator.validarCNPJ(cnpj);
        clienteValidator.validarEmail(email);
        clienteValidator.validarTelefone(telefone);

        String cnpjLimpo = clienteValidator.limparCNPJ(cnpj);
        String telefoneLimpo = clienteValidator.limparTelefone(telefone);

        if (pessoaJuridicaRepository.findByCnpj(cnpjLimpo).isPresent()) {
            throw new InvalidConfigurationException("CNPJ ja cadastrado.");
        }

        if (pessoaJuridicaRepository.findByEmail(email).isPresent()) {
            throw new InvalidConfigurationException("Email ja cadastrado.");
        }

        if (pessoaJuridicaRepository.findByTelefone(telefoneLimpo).isPresent()) {
            throw new InvalidConfigurationException("Telefone ja cadastrado.");
        }

        PessoaJuridica pessoaJuridica = PessoaJuridica.builder()
                .razaoSocial(razaoSocial.trim())
                .cnpj(cnpjLimpo)
                .email(email.trim())
                .telefone(telefoneLimpo)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .build();

        pessoaJuridicaRepository.save(pessoaJuridica);
    }

    @Transactional
    public void inativarPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = pessoaJuridicaRepository.findById(pessoaJuridicaId)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Juridica nao encontrada."));

        pessoaJuridica.setAtivo(false);
        pessoaJuridica.setDataInativacao(LocalDateTime.now());
        pessoaJuridicaRepository.save(pessoaJuridica);
    }

    public PessoaJuridica obterPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        return pessoaJuridicaRepository.findById(pessoaJuridicaId)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Juridica nao encontrada."));
    }

    public PessoaJuridica obterPorCNPJ(String cnpj) throws InvalidConfigurationException {
        String cnpjLimpo = clienteValidator.limparCNPJ(cnpj);
        return pessoaJuridicaRepository.findByCnpj(cnpjLimpo)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Juridica com CNPJ " + cnpj + " nao encontrada."));
    }

    public PessoaJuridica obterPorEmail(String email) throws InvalidConfigurationException {
        return pessoaJuridicaRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Juridica com email " + email + " nao encontrada."));
    }

    public PessoaJuridica obterPorTelefone(String telefone) throws InvalidConfigurationException {
        String telefoneLimpo = clienteValidator.limparTelefone(telefone);
        return pessoaJuridicaRepository.findByTelefone(telefoneLimpo)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Juridica com telefone " + telefone + " nao encontrada."));
    }

    @Transactional
    public void inativarPorCNPJ(String cnpj) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPorCNPJ(cnpj);
        pessoaJuridica.setAtivo(false);
        pessoaJuridica.setDataInativacao(LocalDateTime.now());
        pessoaJuridicaRepository.save(pessoaJuridica);
    }

    @Transactional
    public void inativarPorEmail(String email) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPorEmail(email);
        pessoaJuridica.setAtivo(false);
        pessoaJuridica.setDataInativacao(LocalDateTime.now());
        pessoaJuridicaRepository.save(pessoaJuridica);
    }

    @Transactional
    public void inativarPorTelefone(String telefone) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = obterPorTelefone(telefone);
        pessoaJuridica.setAtivo(false);
        pessoaJuridica.setDataInativacao(LocalDateTime.now());
        pessoaJuridicaRepository.save(pessoaJuridica);
    }

    public List<PessoaJuridica> listarPessoasJuridicas() {
        return pessoaJuridicaRepository.findByAtivo(true);
    }

    public List<PessoaJuridica> listarTodas() {
        return pessoaJuridicaRepository.findAll();
    }
}
