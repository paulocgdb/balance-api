package com.company.balance.dto;

import com.company.balance.enums.Moeda;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaldoDTO {
    @JsonProperty("amount")
    private BigDecimal valor;
    @JsonProperty("currency")
    private Moeda moeda;
}