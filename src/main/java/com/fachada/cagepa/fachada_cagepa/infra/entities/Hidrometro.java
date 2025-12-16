package com.fachada.cagepa.fachada_cagepa.infra.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hidrometro", uniqueConstraints = {
        @UniqueConstraint(columnNames = "sha"),
        @UniqueConstraint(columnNames = "endereco_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hidrometro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String sha;

    @Column(nullable = false)
    private Long limiteConsumoMensal;

    @Column(nullable = false)
    private boolean ativo;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column
    private LocalDateTime dataInativacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_fisica_id")
    private PessoaFisica pessoaFisica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_juridica_id")
    private PessoaJuridica pessoaJuridica;
}
