package com.company.balance.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SaldoDTO {
    private BigDecimal valor;
    private String moeda;
}