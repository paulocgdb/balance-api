package com.company.balance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContaDTO {
    private UUID id;
    
    @JsonProperty("owner")
    private UUID titular;
    
    @JsonProperty("balance")
    private SaldoDTO saldo;
}
