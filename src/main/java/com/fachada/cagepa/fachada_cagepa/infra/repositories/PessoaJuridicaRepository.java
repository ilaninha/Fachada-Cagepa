package com.fachada.cagepa.fachada_cagepa.infra.repositories;

import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long> {
    Optional<PessoaJuridica> findByCnpj(String cnpj);
    Optional<PessoaJuridica> findByEmail(String email);
    Optional<PessoaJuridica> findByTelefone(String telefone);
    List<PessoaJuridica> findByAtivo(boolean ativo);
}
