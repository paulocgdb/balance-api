package com.company.balance.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class TransacaoDTO {
    private UUID id;
    private String tipo;
    private BigDecimal valor;
    private String moeda;
    private String status;
    private OffsetDateTime timestamp;
}
