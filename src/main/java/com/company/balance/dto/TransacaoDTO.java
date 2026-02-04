package com.company.balance.dto;

import com.company.balance.enums.Moeda;
import com.company.balance.enums.StatusTransacao;
import com.company.balance.enums.TipoTransacao;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class TransacaoDTO {
    private UUID id;
    
    @JsonProperty("type")
    private TipoTransacao tipo;
    
    @JsonProperty("amount")
    private BigDecimal valor;
    
    @JsonProperty("currency")
    private Moeda moeda;
    
    private StatusTransacao status;
    
    private Long timestamp;
}
