package com.company.balance.service;

import com.company.balance.dto.RespostaSaldoDTO;
import com.company.balance.entity.ContaEntity;
import com.company.balance.exception.ContaNaoEncontradaException;
import com.company.balance.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultaSaldoService {

    private final ContaRepository contaRepository;

    public RespostaSaldoDTO consultar(UUID idConta) {
        ContaEntity conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(idConta));

        return RespostaSaldoDTO.from(conta);
    }
}
