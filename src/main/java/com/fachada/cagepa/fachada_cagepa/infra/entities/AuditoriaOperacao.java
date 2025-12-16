package com.fachada.cagepa.fachada_cagepa.infra.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidade para registrar auditoria de operações do sistema.
 * Rastreia CRUDs, administrador responsável, timestamp e resultado.
 */
@Entity
@Table(name = "auditoria_operacoes", indexes = {
    @Index(name = "idx_admin_id", columnList = "administrador_id"),
    @Index(name = "idx_data_operacao", columnList = "data_operacao"),
    @Index(name = "idx_tipo_operacao", columnList = "tipo_operacao"),
    @Index(name = "idx_entidade", columnList = "tipo_entidade")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaOperacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Administrador que realizou a operação
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_id", nullable = false)
    private Administrador administrador;
    
    /**
     * Data e hora da operação (UTC)
     */
    @Column(name = "data_operacao", nullable = false)
    private LocalDateTime dataOperacao;
    
    /**
     * Tipo de operação: CREATE, READ, UPDATE, DELETE, LOGIN, CONFIG_CHANGE, etc.
     */
    @Column(name = "tipo_operacao", nullable = false, length = 50)
    private String tipoOperacao;
    
    /**
     * Tipo de entidade afetada: PessoaFisica, PessoaJuridica, Hidrometro, etc.
     */
    @Column(name = "tipo_entidade", nullable = false, length = 100)
    private String tipoEntidade;
    
    /**
     * ID da entidade afetada
     */
    @Column(name = "entidade_id")
    private Long entidadeId;
    
    /**
     * Descrição breve da operação
     */
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;
    
    /**
     * Dados anteriores (para UPDATE/DELETE em formato JSON)
     */
    @Column(name = "dados_anteriores", columnDefinition = "TEXT")
    private String dadosAnteriores;
    
    /**
     * Dados novos (para CREATE/UPDATE em formato JSON)
     */
    @Column(name = "dados_novos", columnDefinition = "TEXT")
    private String dadosNovos;
    
    /**
     * Resultado da operação: SUCESSO, FALHA, PARCIAL
     */
    @Column(name = "resultado_operacao", nullable = false, length = 20)
    private String resultadoOperacao;
    
    /**
     * Mensagem de erro (se houver)
     */
    @Column(name = "mensagem_erro", columnDefinition = "TEXT")
    private String mensagemErro;
    
    /**
     * Flag indicando se operação foi crítica/importante
     */
    @Column(name = "critica", nullable = false)
    @Builder.Default
    private Boolean critica = false;
}
