package com.company.balance.dto;

import lombok.Getter;

@Getter
public class MensagemTransacaoSqsDTO {
    private TransacaoDTO transacao;
    private ContaDTO conta;
}