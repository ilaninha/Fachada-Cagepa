package com.fachada.cagepa.fachada_cagepa.padroes.autenticacao;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Endereco;
import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaFisica;
import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaJuridica;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.EnderecoRepository;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.PessoaFisicaRepository;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.PessoaJuridicaRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidConfigurationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final PessoaFisicaRepository pessoaFisicaRepository;
    private final PessoaJuridicaRepository pessoaJuridicaRepository;

    public EnderecoService(EnderecoRepository enderecoRepository, PessoaFisicaRepository pessoaFisicaRepository, PessoaJuridicaRepository pessoaJuridicaRepository) {
        this.enderecoRepository = enderecoRepository;
        this.pessoaFisicaRepository = pessoaFisicaRepository;
        this.pessoaJuridicaRepository = pessoaJuridicaRepository;
    }

    @Transactional
    public void adicionarEnderecoPessoaFisica(Long pessoaFisicaId, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        PessoaFisica pessoaFisica = pessoaFisicaRepository.findById(pessoaFisicaId)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Fisica nao encontrada."));

        validarEndereco(logradouro, numero, bairro, cidade, estado, cep);

        Endereco endereco = Endereco.builder()
                .logradouro(logradouro.trim())
                .numero(numero.trim())
                .complemento(complemento != null ? complemento.trim() : null)
                .bairro(bairro.trim())
                .cidade(cidade.trim())
                .estado(estado.trim().toUpperCase())
                .cep(cep.replaceAll("\\D", ""))
                .pessoaFisica(pessoaFisica)
                .build();

        enderecoRepository.save(endereco);
    }

    @Transactional
    public void adicionarEnderecoPessoaJuridica(Long pessoaJuridicaId, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        PessoaJuridica pessoaJuridica = pessoaJuridicaRepository.findById(pessoaJuridicaId)
                .orElseThrow(() -> new InvalidConfigurationException("Pessoa Juridica nao encontrada."));

        validarEndereco(logradouro, numero, bairro, cidade, estado, cep);

        Endereco endereco = Endereco.builder()
                .logradouro(logradouro.trim())
                .numero(numero.trim())
                .complemento(complemento != null ? complemento.trim() : null)
                .bairro(bairro.trim())
                .cidade(cidade.trim())
                .estado(estado.trim().toUpperCase())
                .cep(cep.replaceAll("\\D", ""))
                .pessoaJuridica(pessoaJuridica)
                .build();

        enderecoRepository.save(endereco);
    }

    public List<Endereco> listarEnderecosPessoaFisica(Long pessoaFisicaId) {
        return enderecoRepository.findByPessoaFisicaId(pessoaFisicaId);
    }

    public List<Endereco> listarEnderecosPessoaJuridica(Long pessoaJuridicaId) {
        return enderecoRepository.findByPessoaJuridicaId(pessoaJuridicaId);
    }

    @Transactional
    public void deletarEndereco(Long enderecoId) throws InvalidConfigurationException {
        if (!enderecoRepository.existsById(enderecoId)) {
            throw new InvalidConfigurationException("Endereco nao encontrado.");
        }
        enderecoRepository.deleteById(enderecoId);
    }

    private void validarEndereco(String logradouro, String numero, String bairro, String cidade, String estado, String cep) throws InvalidConfigurationException {
        if (logradouro == null || logradouro.trim().isEmpty()) {
            throw new InvalidConfigurationException("Logradouro nao pode estar vazio.");
        }

        if (numero == null || numero.trim().isEmpty()) {
            throw new InvalidConfigurationException("Numero nao pode estar vazio.");
        }

        if (bairro == null || bairro.trim().isEmpty()) {
            throw new InvalidConfigurationException("Bairro nao pode estar vazio.");
        }

        if (cidade == null || cidade.trim().isEmpty()) {
            throw new InvalidConfigurationException("Cidade nao pode estar vazia.");
        }

        if (estado == null || estado.trim().isEmpty() || estado.trim().length() != 2) {
            throw new InvalidConfigurationException("Estado deve conter 2 caracteres.");
        }

        if (cep == null || cep.replaceAll("\\D", "").length() != 8) {
            throw new InvalidConfigurationException("CEP deve conter 8 digitos.");
        }
    }
}
