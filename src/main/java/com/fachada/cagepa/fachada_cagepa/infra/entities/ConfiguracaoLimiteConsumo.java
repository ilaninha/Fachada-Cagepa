package com.fachada.cagepa.fachada_cagepa.infra.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade para armazenar a configuração de limite de consumo por hidrômetro.
 * Define o limite mensal em m³ acima do qual notificações serão enviadas.
 */
@Entity
@Table(name = "configuracao_limite_consumo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracaoLimiteConsumo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hidrometro_id", nullable = false)
    private Hidrometro hidrometro;
    
    /**
     * Limite mensal em m³
     */
    @Column(nullable = false)
    private Long limiteConsumMensal;
    
    /**
     * Percentual do limite que dispara a notificação (ex: 70)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer percentualLimite = 70;
    
    /**
     * Indica se as notificações estão habilitadas para este hidrômetro
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
}
