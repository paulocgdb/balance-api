package com.company.balance.dto;

import com.company.balance.entity.ContaEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RespostaSaldoDTO {

    private UUID id;
    
    @JsonProperty("owner")
    private UUID titular;
    
    @JsonProperty("balance")
    private SaldoDTO saldo;
    
    @JsonProperty("updated_at")
    private OffsetDateTime atualizadoEm;

    public static RespostaSaldoDTO from(ContaEntity conta) {
        return new RespostaSaldoDTO(
                conta.getId(),
                conta.getTitular(),
                new SaldoDTO(conta.getSaldo(), conta.getMoeda()),
                conta.getUltimaAtualizacao()
        );
    }
}
