package com.fachada.cagepa.fachada_cagepa.infra.repositories;

import com.fachada.cagepa.fachada_cagepa.infra.entities.LeituraDoHidrometro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraDoHidrometroRepository extends JpaRepository<LeituraDoHidrometro, Long> {
    List<LeituraDoHidrometro> findByShaHidrometro(String shaHidrometro);
    List<LeituraDoHidrometro> findByTipoHidrometro(String tipoHidrometro);
    
    /**
     * Busca todas as leituras de um hidrômetro dentro de um período específico,
     * ordenadas por timestamp para cálculo de consumo.
     */
    @Query("SELECT l FROM LeituraDoHidrometro l WHERE l.shaHidrometro = :sha " +
           "AND l.timestamp BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY l.timestamp ASC")
    List<LeituraDoHidrometro> findByPeriodAndSha(
        @Param("sha") String sha,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );
    
    /**
     * Busca a última leitura de um hidrômetro específico.
     */
    @Query("SELECT l FROM LeituraDoHidrometro l WHERE l.shaHidrometro = :sha " +
           "ORDER BY l.timestamp DESC LIMIT 1")
    LeituraDoHidrometro findLastBySha(@Param("sha") String sha);
    
    /**
     * Busca todas as leituras de um hidrômetro, ordenadas cronologicamente.
     */
    @Query("SELECT l FROM LeituraDoHidrometro l WHERE l.shaHidrometro = :sha " +
           "ORDER BY l.timestamp ASC")
    List<LeituraDoHidrometro> findAllByShaOrderedByTimestamp(@Param("sha") String sha);
}
