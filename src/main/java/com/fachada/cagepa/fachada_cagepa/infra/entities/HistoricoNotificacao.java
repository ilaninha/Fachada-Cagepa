package com.fachada.cagepa.fachada_cagepa.infra.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade para registrar histórico de notificações de consumo.
 * Armazena informações sobre cada notificação enviada ao cliente.
 */
@Entity
@Table(name = "historico_notificacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoNotificacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hidrometro_id", nullable = false)
    private Hidrometro hidrometro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_fisica_id")
    private PessoaFisica pessoaFisica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_juridica_id")
    private PessoaJuridica pessoaJuridica;
    
    /**
     * Email para o qual a notificação foi enviada
     */
    @Column(nullable = false)
    private String emailDestino;
    
    /**
     * Consumo mensal (em m³) quando a notificação foi disparada
     */
    @Column(nullable = false)
    private Long consumoMensal;
    
    /**
     * Limite configurado (em m³)
     */
    @Column(nullable = false)
    private Long limitConfigurado;
    
    /**
     * Percentual do limite atingido (ex: 75% do limite)
     */
    @Column(nullable = false)
    private Integer percentualAtingido;
    
    /**
     * Data e hora do envio da notificação
     */
    @Column(nullable = false)
    private LocalDateTime dataEnvio;
    
    /**
     * Data do evento (para evitar duplicatas no mesmo dia)
     */
    @Column(nullable = false)
    private LocalDate dataEvento;
    
    /**
     * Status do envio (ENVIADO, FALHA, PENDENTE)
     */
    @Column(nullable = false)
    @Builder.Default
    private String statusEnvio = "ENVIADO";
    
    /**
     * Mensagem com detalhes do envio ou erro
     */
    @Column(columnDefinition = "TEXT")
    private String mensagem;
}
