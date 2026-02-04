package com.company.balance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MensagemTransacaoSqsDTO {
    @JsonProperty("transaction")
    private TransacaoDTO transacao;
    @JsonProperty("account")
    private ContaDTO conta;
}