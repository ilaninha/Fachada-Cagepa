package com.fachada.cagepa.fachada_cagepa.infra.repositories;

import com.fachada.cagepa.fachada_cagepa.infra.entities.HistoricoNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoNotificacaoRepository extends JpaRepository<HistoricoNotificacao, Long> {
    
    /**
     * Verifica se já existe notificação para o mesmo hidrômetro e cliente no mesmo dia
     */
    @Query("SELECT hn FROM HistoricoNotificacao hn " +
           "WHERE hn.hidrometro.id = :hidrometroId " +
           "AND hn.dataEvento = :dataEvento " +
           "AND hn.statusEnvio = 'ENVIADO'")
    Optional<HistoricoNotificacao> findByHidrometroAndData(@Param("hidrometroId") Long hidrometroId,
                                                           @Param("dataEvento") LocalDate dataEvento);
    
    /**
     * Lista histórico de notificações para um hidrômetro específico
     */
    @Query("SELECT hn FROM HistoricoNotificacao hn " +
           "WHERE hn.hidrometro.id = :hidrometroId " +
           "ORDER BY hn.dataEnvio DESC")
    List<HistoricoNotificacao> findByHidrometroId(@Param("hidrometroId") Long hidrometroId);
    
    /**
     * Lista histórico de notificações para um cliente (Pessoa Física)
     */
    @Query("SELECT hn FROM HistoricoNotificacao hn " +
           "WHERE hn.pessoaFisica.id = :pessoaFisicaId " +
           "ORDER BY hn.dataEnvio DESC")
    List<HistoricoNotificacao> findByPessoaFisicaId(@Param("pessoaFisicaId") Long pessoaFisicaId);
    
    /**
     * Lista histórico de notificações para um cliente (Pessoa Jurídica)
     */
    @Query("SELECT hn FROM HistoricoNotificacao hn " +
           "WHERE hn.pessoaJuridica.id = :pessoaJuridicaId " +
           "ORDER BY hn.dataEnvio DESC")
    List<HistoricoNotificacao> findByPessoaJuridicaId(@Param("pessoaJuridicaId") Long pessoaJuridicaId);
    
    /**
     * Contagem de notificações por data para um hidrômetro
     */
    @Query("SELECT COUNT(hn) FROM HistoricoNotificacao hn " +
           "WHERE hn.hidrometro.id = :hidrometroId " +
           "AND hn.dataEvento = :dataEvento")
    Long countByHidrometroAndData(@Param("hidrometroId") Long hidrometroId,
                                  @Param("dataEvento") LocalDate dataEvento);
}
