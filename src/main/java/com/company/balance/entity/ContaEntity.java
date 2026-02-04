package com.company.balance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contas")
@Getter
@Setter
@NoArgsConstructor
public class ContaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID titular;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(nullable = false, length = 3)
    private String moeda;

    @Column(name = "ultima_atualizacao", nullable = false)
    private OffsetDateTime ultimaAtualizacao;

    @Version
    private Long versao;

    public void atualizarSaldo(BigDecimal novoSaldo, OffsetDateTime dataEvento) {
        if (dataEvento.isAfter(this.ultimaAtualizacao)) {
            this.saldo = novoSaldo;
            this.ultimaAtualizacao = dataEvento;
        }
    }
}
