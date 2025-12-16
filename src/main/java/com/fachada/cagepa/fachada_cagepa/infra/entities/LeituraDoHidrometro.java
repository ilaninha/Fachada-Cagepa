package com.fachada.cagepa.fachada_cagepa.infra.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "leitura_dos_hidrometros", indexes = {
        @Index(name = "idx_sha_hidrometro", columnList = "sha_hidrometro")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeituraDoHidrometro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false, name = "sha_hidrometro", length = 64)
    private String shaHidrometro;

    @Column(nullable = false)
    private Long valorLeitura;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String tipoHidrometro;
}
