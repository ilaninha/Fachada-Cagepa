package com.fachada.cagepa.fachada_cagepa.padroes.fachada.proxy;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Hidrometro;
import com.fachada.cagepa.fachada_cagepa.padroes.autenticacao.HidrometroService;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HidrometroProxyFachada {

    @Autowired
    private HidrometroService hidrometroService;

    public void cadastrarHidrometroPessoaFisica(String sha, Long limiteConsumoMensal, Long pessoaFisicaId, Long enderecoId)
            throws InvalidConfigurationException {
        hidrometroService.cadastrarHidrometroPessoaFisica(sha, limiteConsumoMensal, pessoaFisicaId, enderecoId);
        System.out.println("Hidrometro cadastrado com sucesso para Pessoa Fisica.");
    }

    public void cadastrarHidrometroPessoaJuridica(String sha, Long limiteConsumoMensal, Long pessoaJuridicaId, Long enderecoId)
            throws InvalidConfigurationException {
        hidrometroService.cadastrarHidrometroPessoaJuridica(sha, limiteConsumoMensal, pessoaJuridicaId, enderecoId);
        System.out.println("Hidrometro cadastrado com sucesso para Pessoa Juridica.");
    }

    public void cadastrarHidrometroPessoaFisicaComEndereco(String sha, Long limiteConsumoMensal, Long pessoaFisicaId,
                                                           String logradouro, String numero, String complemento, String bairro,
                                                           String cidade, String estado, String cep) throws InvalidConfigurationException {
        hidrometroService.cadastrarHidrometroPessoaFisicaComEndereco(sha, limiteConsumoMensal, pessoaFisicaId,
                logradouro, numero, complemento, bairro, cidade, estado, cep);
        System.out.println("Hidrometro cadastrado com sucesso para Pessoa Fisica com novo endereco.");
    }

    public void cadastrarHidrometroPessoaJuridicaComEndereco(String sha, Long limiteConsumoMensal, Long pessoaJuridicaId,
                                                             String logradouro, String numero, String complemento, String bairro,
                                                             String cidade, String estado, String cep) throws InvalidConfigurationException {
        hidrometroService.cadastrarHidrometroPessoaJuridicaComEndereco(sha, limiteConsumoMensal, pessoaJuridicaId,
                logradouro, numero, complemento, bairro, cidade, estado, cep);
        System.out.println("Hidrometro cadastrado com sucesso para Pessoa Juridica com novo endereco.");
    }

    public Hidrometro obterPorSHA(String sha) throws InvalidConfigurationException {
        return hidrometroService.obterPorSHA(sha);
    }

    public List<Hidrometro> listarPorPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        return hidrometroService.listarPorPessoaFisica(pessoaFisicaId);
    }

    public List<Hidrometro> listarPorPessoaFisicaAtivos(Long pessoaFisicaId) throws InvalidConfigurationException {
        return hidrometroService.listarPorPessoaFisicaAtivos(pessoaFisicaId);
    }

    public List<Hidrometro> listarPorPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        return hidrometroService.listarPorPessoaJuridica(pessoaJuridicaId);
    }

    public List<Hidrometro> listarPorPessoaJuridicaAtivos(Long pessoaJuridicaId) throws InvalidConfigurationException {
        return hidrometroService.listarPorPessoaJuridicaAtivos(pessoaJuridicaId);
    }

    public void ativarHidrometro(Long hidrometroId) throws InvalidConfigurationException {
        hidrometroService.ativarHidrometro(hidrometroId);
        System.out.println("Hidrometro ativado com sucesso.");
    }

    public void desativarHidrometro(Long hidrometroId) throws InvalidConfigurationException {
        hidrometroService.desativarHidrometro(hidrometroId);
        System.out.println("Hidrometro desativado com sucesso.");
    }

    public List<Hidrometro> listarAtivos() {
        return hidrometroService.listarAtivos();
    }

    public List<Hidrometro> listarInativos() {
        return hidrometroService.listarInativos();
    }
}
