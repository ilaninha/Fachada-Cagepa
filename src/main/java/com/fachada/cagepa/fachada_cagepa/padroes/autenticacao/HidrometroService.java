package com.fachada.cagepa.fachada_cagepa.padroes.autenticacao;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Endereco;
import com.fachada.cagepa.fachada_cagepa.infra.entities.Hidrometro;
import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaFisica;
import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaJuridica;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.EnderecoRepository;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.HidrometroRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.config.InvalidConfigurationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HidrometroService {

    private final HidrometroRepository hidrometry;
    private final EnderecoRepository enderecoRepository;
    private final PessoaFisicaService pessoaFisicaService;
    private final PessoaJuridicaService pessoaJuridicaService;

    public HidrometroService(HidrometroRepository hidrometry, EnderecoRepository enderecoRepository,
                              PessoaFisicaService pessoaFisicaService, PessoaJuridicaService pessoaJuridicaService) {
        this.hidrometry = hidrometry;
        this.enderecoRepository = enderecoRepository;
        this.pessoaFisicaService = pessoaFisicaService;
        this.pessoaJuridicaService = pessoaJuridicaService;
    }

    @Transactional
    public void cadastrarHidrometroPessoaFisica(String sha, Long limiteConsumoMensal, Long pessoaFisicaId, Long enderecoId)
            throws InvalidConfigurationException {
        validarSHA(sha);
        validarLimiteConsumo(limiteConsumoMensal);

        if (hidrometry.existsBySha(sha)) {
            throw new InvalidConfigurationException("Hidrometro com SHA " + sha + " ja cadastrado.");
        }

        PessoaFisica pessoaFisica = pessoaFisicaService.obterPessoaFisica(pessoaFisicaId);
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new InvalidConfigurationException("Endereco nao encontrado."));

        if (endereco.getPessoaFisica() == null || !endereco.getPessoaFisica().getId().equals(pessoaFisicaId)) {
            throw new InvalidConfigurationException("Endereco nao pertence ao cliente informado.");
        }

        // Validar se o endereco ja tem um hidrometro vinculado
        if (hidrometry.existsByEnderecoId(enderecoId)) {
            throw new InvalidConfigurationException("Endereco ja possui um hidrometro vinculado.");
        }

        Hidrometro hidrometro = Hidrometro.builder()
                .sha(sha.trim())
                .limiteConsumoMensal(limiteConsumoMensal)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .endereco(endereco)
                .pessoaFisica(pessoaFisica)
                .build();

        hidrometry.save(hidrometro);
    }

    @Transactional
    public void cadastrarHidrometroPessoaJuridica(String sha, Long limiteConsumoMensal, Long pessoaJuridicaId, Long enderecoId)
            throws InvalidConfigurationException {
        validarSHA(sha);
        validarLimiteConsumo(limiteConsumoMensal);

        if (hidrometry.existsBySha(sha)) {
            throw new InvalidConfigurationException("Hidrometro com SHA " + sha + " ja cadastrado.");
        }

        PessoaJuridica pessoaJuridica = pessoaJuridicaService.obterPessoaJuridica(pessoaJuridicaId);
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new InvalidConfigurationException("Endereco nao encontrado."));

        if (endereco.getPessoaJuridica() == null || !endereco.getPessoaJuridica().getId().equals(pessoaJuridicaId)) {
            throw new InvalidConfigurationException("Endereco nao pertence ao cliente informado.");
        }

        // Validar se o endereco ja tem um hidrometro vinculado
        if (hidrometry.existsByEnderecoId(enderecoId)) {
            throw new InvalidConfigurationException("Endereco ja possui um hidrometro vinculado.");
        }

        Hidrometro hidrometro = Hidrometro.builder()
                .sha(sha.trim())
                .limiteConsumoMensal(limiteConsumoMensal)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .endereco(endereco)
                .pessoaJuridica(pessoaJuridica)
                .build();

        hidrometry.save(hidrometro);
    }

    @Transactional
    public void cadastrarHidrometroPessoaFisicaComEndereco(String sha, Long limiteConsumoMensal, Long pessoaFisicaId,
                                                           String logradouro, String numero, String complemento, String bairro,
                                                           String cidade, String estado, String cep) throws InvalidConfigurationException {
        validarSHA(sha);
        validarLimiteConsumo(limiteConsumoMensal);

        if (hidrometry.existsBySha(sha)) {
            throw new InvalidConfigurationException("Hidrometro com SHA " + sha + " ja cadastrado.");
        }

        PessoaFisica pessoaFisica = pessoaFisicaService.obterPessoaFisica(pessoaFisicaId);

        Endereco enderecoExistente = enderecoRepository.findByLogradouroAndNumeroAndBairroAndCidadeAndEstadoAndCep(
                logradouro, numero, bairro, cidade, estado, cep);

        if (enderecoExistente != null && enderecoExistente.getPessoaFisica() != null && 
            enderecoExistente.getPessoaFisica().getId().equals(pessoaFisicaId)) {
            // Endereco ja existe para este cliente, reutilizar
            cadastrarHidrometroPessoaFisica(sha, limiteConsumoMensal, pessoaFisicaId, enderecoExistente.getId());
        } else if (enderecoExistente == null) {
            // Criar novo endereco
            Endereco novoEndereco = Endereco.builder()
                    .logradouro(logradouro.trim())
                    .numero(numero.trim())
                    .complemento(complemento != null ? complemento.trim() : null)
                    .bairro(bairro.trim())
                    .cidade(cidade.trim())
                    .estado(estado.trim())
                    .cep(cep.replaceAll("\\D", ""))
                    .pessoaFisica(pessoaFisica)
                    .build();

            Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);
            cadastrarHidrometroPessoaFisica(sha, limiteConsumoMensal, pessoaFisicaId, enderecoSalvo.getId());
        } else {
            throw new InvalidConfigurationException("Endereco existente pertence a outro cliente.");
        }
    }

    @Transactional
    public void cadastrarHidrometroPessoaJuridicaComEndereco(String sha, Long limiteConsumoMensal, Long pessoaJuridicaId,
                                                             String logradouro, String numero, String complemento, String bairro,
                                                             String cidade, String estado, String cep) throws InvalidConfigurationException {
        validarSHA(sha);
        validarLimiteConsumo(limiteConsumoMensal);

        if (hidrometry.existsBySha(sha)) {
            throw new InvalidConfigurationException("Hidrometro com SHA " + sha + " ja cadastrado.");
        }

        PessoaJuridica pessoaJuridica = pessoaJuridicaService.obterPessoaJuridica(pessoaJuridicaId);

        Endereco enderecoExistente = enderecoRepository.findByLogradouroAndNumeroAndBairroAndCidadeAndEstadoAndCep(
                logradouro, numero, bairro, cidade, estado, cep);

        if (enderecoExistente != null && enderecoExistente.getPessoaJuridica() != null && 
            enderecoExistente.getPessoaJuridica().getId().equals(pessoaJuridicaId)) {
            // Endereco ja existe para este cliente, reutilizar
            cadastrarHidrometroPessoaJuridica(sha, limiteConsumoMensal, pessoaJuridicaId, enderecoExistente.getId());
        } else if (enderecoExistente == null) {
            // Criar novo endereco
            Endereco novoEndereco = Endereco.builder()
                    .logradouro(logradouro.trim())
                    .numero(numero.trim())
                    .complemento(complemento != null ? complemento.trim() : null)
                    .bairro(bairro.trim())
                    .cidade(cidade.trim())
                    .estado(estado.trim())
                    .cep(cep.replaceAll("\\D", ""))
                    .pessoaJuridica(pessoaJuridica)
                    .build();

            Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);
            cadastrarHidrometroPessoaJuridica(sha, limiteConsumoMensal, pessoaJuridicaId, enderecoSalvo.getId());
        } else {
            throw new InvalidConfigurationException("Endereco existente pertence a outro cliente.");
        }
    }

    @Transactional(readOnly = true)
    public Hidrometro obterPorSHA(String sha) throws InvalidConfigurationException {
        return hidrometry.findByShaWithEndereco(sha.trim())
                .orElseThrow(() -> new InvalidConfigurationException("Hidrometro com SHA " + sha + " nao encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Hidrometro> listarPorPessoaFisica(Long pessoaFisicaId) throws InvalidConfigurationException {
        pessoaFisicaService.obterPessoaFisica(pessoaFisicaId);
        return hidrometry.findByPessoaFisicaId(pessoaFisicaId);
    }

    @Transactional(readOnly = true)
    public List<Hidrometro> listarPorPessoaFisicaAtivos(Long pessoaFisicaId) throws InvalidConfigurationException {
        pessoaFisicaService.obterPessoaFisica(pessoaFisicaId);
        return hidrometry.findByPessoaFisicaIdAndAtivo(pessoaFisicaId, true);
    }

    @Transactional(readOnly = true)
    public List<Hidrometro> listarPorPessoaJuridica(Long pessoaJuridicaId) throws InvalidConfigurationException {
        pessoaJuridicaService.obterPessoaJuridica(pessoaJuridicaId);
        return hidrometry.findByPessoaJuridicaId(pessoaJuridicaId);
    }

    @Transactional(readOnly = true)
    public List<Hidrometro> listarPorPessoaJuridicaAtivos(Long pessoaJuridicaId) throws InvalidConfigurationException {
        pessoaJuridicaService.obterPessoaJuridica(pessoaJuridicaId);
        return hidrometry.findByPessoaJuridicaIdAndAtivo(pessoaJuridicaId, true);
    }

    @Transactional
    public void ativarHidrometro(Long hidrometroId) throws InvalidConfigurationException {
        Hidrometro hidrometro = hidrometry.findById(hidrometroId)
                .orElseThrow(() -> new InvalidConfigurationException("Hidrometro nao encontrado."));

        if (hidrometro.isAtivo()) {
            throw new InvalidConfigurationException("Hidrometro ja esta ativo.");
        }

        hidrometro.setAtivo(true);
        hidrometro.setDataInativacao(null);
        hidrometry.save(hidrometro);
    }

    @Transactional
    public void desativarHidrometro(Long hidrometroId) throws InvalidConfigurationException {
        Hidrometro hidrometro = hidrometry.findById(hidrometroId)
                .orElseThrow(() -> new InvalidConfigurationException("Hidrometro nao encontrado."));

        if (!hidrometro.isAtivo()) {
            throw new InvalidConfigurationException("Hidrometro ja esta inativo.");
        }

        hidrometro.setAtivo(false);
        hidrometro.setDataInativacao(LocalDateTime.now());
        hidrometry.save(hidrometro);
    }

    @Transactional(readOnly = true)
    public List<Hidrometro> listarAtivos() {
        return hidrometry.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Hidrometro> listarInativos() {
        return hidrometry.findByAtivoFalse();
    }

    private void validarSHA(String sha) throws InvalidConfigurationException {
        if (sha == null || sha.trim().isEmpty()) {
            throw new InvalidConfigurationException("SHA nao pode estar vazio.");
        }
        
        String shaTrimmed = sha.trim();
        
        // Validar tamanho máximo (64 caracteres)
        if (shaTrimmed.length() > 64) {
            throw new InvalidConfigurationException("SHA nao pode ter mais de 64 caracteres.");
        }
        
        // Validar se contém apenas números e letras
        if (!shaTrimmed.matches("^[a-zA-Z0-9]+$")) {
            throw new InvalidConfigurationException("SHA deve conter apenas letras e números.");
        }
    }

    private void validarLimiteConsumo(Long limiteConsumo) throws InvalidConfigurationException {
        if (limiteConsumo == null || limiteConsumo <= 0) {
            throw new InvalidConfigurationException("Limite de consumo deve ser maior que zero.");
        }
    }
}
