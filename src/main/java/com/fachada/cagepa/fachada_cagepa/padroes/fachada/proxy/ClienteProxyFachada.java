package com.fachada.cagepa.fachada_cagepa.padroes.fachada.proxy;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Endereco;
import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaFisica;
import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaJuridica;
import com.fachada.cagepa.fachada_cagepa.padroes.autenticacao.EnderecoService;
import com.fachada.cagepa.fachada_cagepa.padroes.autenticacao.PessoaFisicaService;
import com.fachada.cagepa.fachada_cagepa.padroes.autenticacao.PessoaJuridicaService;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClienteProxyFachada {

    @Autowired
    private PessoaFisicaService pessoaFisicaService;

    @Autowired
    private PessoaJuridicaService pessoaJuridicaService;

    @Autowired
    private EnderecoService enderecoService;

    public void criarPessoaFisica(String nome, String cpf, String email, String telefone) throws InvalidConfigurationException {
        pessoaFisicaService.criarPessoaFisica(nome, cpf, email, telefone);
        System.out.println("Pessoa Fisica criada com sucesso: " + nome);
    }

    public void criarPessoaJuridica(String razaoSocial, String cnpj, String email, String telefone) throws InvalidConfigurationException {
        pessoaJuridicaService.criarPessoaJuridica(razaoSocial, cnpj, email, telefone);
        System.out.println("Pessoa Juridica criada com sucesso: " + razaoSocial);
    }

    public void inativarPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        pessoaFisicaService.inativarPessoaFisica(pessoaFisicaId);
        System.out.println("Pessoa Fisica inativada com sucesso.");
    }

    public void inativarPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        pessoaJuridicaService.inativarPessoaJuridica(pessoaJuridicaId);
        System.out.println("Pessoa Juridica inativada com sucesso.");
    }

    public PessoaFisica obterPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        return pessoaFisicaService.obterPessoaFisica(pessoaFisicaId);
    }

    public PessoaFisica obterPorCPF(String cpf) throws InvalidConfigurationException {
        return pessoaFisicaService.obterPorCPF(cpf);
    }

    public PessoaFisica obterPorEmail(String email) throws InvalidConfigurationException {
        return pessoaFisicaService.obterPorEmail(email);
    }

    public PessoaFisica obterPorTelefone(String telefone) throws InvalidConfigurationException {
        return pessoaFisicaService.obterPorTelefone(telefone);
    }

    public void inativarPorCPF(String cpf) throws InvalidConfigurationException {
        pessoaFisicaService.inativarPorCPF(cpf);
        System.out.println("Pessoa Fisica inativada com sucesso.");
    }

    public void inativarPorEmail(String email) throws InvalidConfigurationException {
        pessoaFisicaService.inativarPorEmail(email);
        System.out.println("Pessoa Fisica inativada com sucesso.");
    }

    public void inativarPorTelefone(String telefone) throws InvalidConfigurationException {
        pessoaFisicaService.inativarPorTelefone(telefone);
        System.out.println("Pessoa Fisica inativada com sucesso.");
    }

    public PessoaJuridica obterPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        return pessoaJuridicaService.obterPessoaJuridica(pessoaJuridicaId);
    }

    public PessoaJuridica obterPorCNPJ(String cnpj) throws InvalidConfigurationException {
        return pessoaJuridicaService.obterPorCNPJ(cnpj);
    }

    public PessoaJuridica obterPorEmailPJ(String email) throws InvalidConfigurationException {
        return pessoaJuridicaService.obterPorEmail(email);
    }

    public PessoaJuridica obterPorTelefonePJ(String telefone) throws InvalidConfigurationException {
        return pessoaJuridicaService.obterPorTelefone(telefone);
    }

    public void inativarPorCNPJ(String cnpj) throws InvalidConfigurationException {
        pessoaJuridicaService.inativarPorCNPJ(cnpj);
        System.out.println("Pessoa Juridica inativada com sucesso.");
    }

    public void inativarPorEmailPJ(String email) throws InvalidConfigurationException {
        pessoaJuridicaService.inativarPorEmail(email);
        System.out.println("Pessoa Juridica inativada com sucesso.");
    }

    public void inativarPorTelefonePJ(String telefone) throws InvalidConfigurationException {
        pessoaJuridicaService.inativarPorTelefone(telefone);
        System.out.println("Pessoa Juridica inativada com sucesso.");
    }

    public List<PessoaFisica> listarPessoasFisicas() {
        return pessoaFisicaService.listarPessoasFisicas();
    }

    public List<PessoaJuridica> listarPessoasJuridicas() {
        return pessoaJuridicaService.listarPessoasJuridicas();
    }

    public void adicionarEnderecoPessoaFisica(Long pessoaFisicaId, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        enderecoService.adicionarEnderecoPessoaFisica(pessoaFisicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
        System.out.println("Endereco adicionado com sucesso.");
    }

    public void adicionarEnderecoPessoaJuridica(Long pessoaJuridicaId, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        enderecoService.adicionarEnderecoPessoaJuridica(pessoaJuridicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
        System.out.println("Endereco adicionado com sucesso.");
    }

    public List<Endereco> listarEnderecosPessoaFisica(Long pessoaFisicaId) {
        return enderecoService.listarEnderecosPessoaFisica(pessoaFisicaId);
    }

    public List<Endereco> listarEnderecosPessoaJuridica(Long pessoaJuridicaId) {
        return enderecoService.listarEnderecosPessoaJuridica(pessoaJuridicaId);
    }

    public void deletarEndereco(Long enderecoId) throws InvalidConfigurationException {
        enderecoService.deletarEndereco(enderecoId);
        System.out.println("Endereco deletado com sucesso.");
    }

    public void criarEnderecoPessoaFisica(Long pessoaFisicaId, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        enderecoService.adicionarEnderecoPessoaFisica(pessoaFisicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
        System.out.println("Endereco criado com sucesso para Pessoa Fisica.");
    }

    public void criarEnderecoPessoaJuridica(Long pessoaJuridicaId, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        enderecoService.adicionarEnderecoPessoaJuridica(pessoaJuridicaId, logradouro, numero, complemento, bairro, cidade, estado, cep);
        System.out.println("Endereco criado com sucesso para Pessoa Juridica.");
    }
}
