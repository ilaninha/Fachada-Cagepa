package com.fachada.cagepa.fachada_cagepa.infra.repositories;

import com.fachada.cagepa.fachada_cagepa.infra.entities.AuditoriaOperacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para acesso aos registros de auditoria.
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaOperacao, Long> {
    
    /**
     * Encontra todas as operações de um administrador
     */
    Page<AuditoriaOperacao> findByAdministradorId(Long administradorId, Pageable pageable);
    
    /**
     * Encontra todas as operações de um tipo
     */
    Page<AuditoriaOperacao> findByTipoOperacao(String tipoOperacao, Pageable pageable);
    
    /**
     * Encontra todas as operações de uma entidade específica
     */
    Page<AuditoriaOperacao> findByTipoEntidadeAndEntidadeId(String tipoEntidade, Long entidadeId, Pageable pageable);
    
    /**
     * Encontra todas as operações entre duas datas
     */
    Page<AuditoriaOperacao> findByDataOperacaoBetween(LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);
    
    /**
     * Encontra operações críticas
     */
    Page<AuditoriaOperacao> findByAdministradorIdAndCriticaTrue(Long administradorId, Pageable pageable);
    
    /**
     * Encontra operações com falha
     */
    @Query("SELECT a FROM AuditoriaOperacao a WHERE a.resultadoOperacao = 'FALHA' ORDER BY a.dataOperacao DESC")
    Page<AuditoriaOperacao> findFalhas(Pageable pageable);
    
    /**
     * Encontra operações de um tipo de entidade
     */
    Page<AuditoriaOperacao> findByTipoEntidade(String tipoEntidade, Pageable pageable);
    
    /**
     * Conta operações de um administrador em um período
     */
    long countByAdministradorIdAndDataOperacaoBetween(Long administradorId, LocalDateTime dataInicio, LocalDateTime dataFim);
    
    /**
     * Lista últimas operações de um administrador
     */
    @Query("SELECT a FROM AuditoriaOperacao a WHERE a.administrador.id = :adminId ORDER BY a.dataOperacao DESC LIMIT 10")
    List<AuditoriaOperacao> findUltimasOperacoes(@Param("adminId") Long adminId);
}
