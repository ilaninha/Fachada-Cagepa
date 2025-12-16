package com.fachada.cagepa.fachada_cagepa.infra.repositories;

import com.fachada.cagepa.fachada_cagepa.infra.entities.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {
    Optional<PessoaFisica> findByCpf(String cpf);
    Optional<PessoaFisica> findByEmail(String email);
    Optional<PessoaFisica> findByTelefone(String telefone);
    List<PessoaFisica> findByAtivo(boolean ativo);
}
