package com.company.balance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes_processadas")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoProcessadaEntity {

    @Id
    private UUID idTransacao;

    @Column(nullable = false)
    private UUID idConta;

    @Column(nullable = false)
    private OffsetDateTime dataProcessamento;
}
