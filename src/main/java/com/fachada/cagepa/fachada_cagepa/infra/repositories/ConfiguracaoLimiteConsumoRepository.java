package com.fachada.cagepa.fachada_cagepa.infra.repositories;

import com.fachada.cagepa.fachada_cagepa.infra.entities.ConfiguracaoLimiteConsumo;
import com.fachada.cagepa.fachada_cagepa.infra.entities.Hidrometro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracaoLimiteConsumoRepository extends JpaRepository<ConfiguracaoLimiteConsumo, Long> {
    
    /**
     * Busca configuração de limite por hidrômetro
     */
    Optional<ConfiguracaoLimiteConsumo> findByHidrometro(Hidrometro hidrometro);
    
    /**
     * Lista todas as configurações ativas
     */
    @Query("SELECT c FROM ConfiguracaoLimiteConsumo c WHERE c.ativo = true")
    List<ConfiguracaoLimiteConsumo> findAllAtivas();
    
    /**
     * Lista configurações para um cliente específico
     */
    @Query("SELECT c FROM ConfiguracaoLimiteConsumo c " +
           "WHERE c.hidrometro.pessoaFisica.id = :pessoaFisicaId OR c.hidrometro.pessoaJuridica.id = :pessoaJuridicaId")
    List<ConfiguracaoLimiteConsumo> findByClienteIds(@Param("pessoaFisicaId") Long pessoaFisicaId, 
                                                      @Param("pessoaJuridicaId") Long pessoaJuridicaId);
}
