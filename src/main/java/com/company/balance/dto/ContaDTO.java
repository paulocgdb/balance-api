package com.company.balance.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ContaDTO {
    private UUID id;
    private UUID titular;
    private SaldoDTO saldo;
}
