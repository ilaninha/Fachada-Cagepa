package com.fachada.cagepa.fachada_cagepa.infra.repositories;

import com.fachada.cagepa.fachada_cagepa.infra.entities.Hidrometro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HidrometroRepository extends JpaRepository<Hidrometro, Long> {
    Optional<Hidrometro> findBySha(String sha);

    @Query("SELECT h FROM Hidrometro h LEFT JOIN FETCH h.endereco WHERE h.sha = :sha")
    Optional<Hidrometro> findByShaWithEndereco(@Param("sha") String sha);

    List<Hidrometro> findByPessoaFisicaIdAndAtivo(Long pessoaFisicaId, boolean ativo);

    List<Hidrometro> findByPessoaFisicaId(Long pessoaFisicaId);

    List<Hidrometro> findByPessoaJuridicaIdAndAtivo(Long pessoaJuridicaId, boolean ativo);

    List<Hidrometro> findByPessoaJuridicaId(Long pessoaJuridicaId);

    boolean existsBySha(String sha);

    boolean existsByEnderecoId(Long enderecoId);

    List<Hidrometro> findByAtivoTrue();

    List<Hidrometro> findByAtivoFalse();
}
