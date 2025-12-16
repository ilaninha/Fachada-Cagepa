package com.fachada.cagepa.fachada_cagepa.infra.repositories;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByPessoaFisicaId(Long pessoaFisicaId);
    List<Endereco> findByPessoaJuridicaId(Long pessoaJuridicaId);
    Endereco findByLogradouroAndNumeroAndBairroAndCidadeAndEstadoAndCep(String logradouro, String numero, String bairro, String cidade, String estado, String cep);
}
