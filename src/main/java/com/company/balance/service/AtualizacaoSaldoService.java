package com.company.balance.service;

import com.company.balance.dto.MensagemTransacaoSqsDTO;
import com.company.balance.entity.ContaEntity;
import com.company.balance.entity.TransacaoProcessadaEntity;
import com.company.balance.repository.ContaRepository;
import com.company.balance.repository.TransacaoProcessadaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizacaoSaldoService {

    private final ContaRepository contaRepository;
    private final TransacaoProcessadaRepository transacaoProcessadaRepository;

    @Transactional
    public void processar(MensagemTransacaoSqsDTO mensagem) {

        UUID idTransacao = mensagem.getTransacao().getId();

        if (transacaoProcessadaRepository.existsById(idTransacao)) {
            return;
        }

        UUID idConta = mensagem.getConta().getId();

        ContaEntity conta = contaRepository
                .buscarPorIdComLock(idConta)
                .orElseGet(() -> criarConta(mensagem));

        conta.atualizarSaldo(
                mensagem.getConta().getSaldo().getValor(),
                mensagem.getTransacao().getTimestamp()
        );

        contaRepository.save(conta);

        transacaoProcessadaRepository.save(
                new TransacaoProcessadaEntity(
                        idTransacao,
                        idConta,
                        OffsetDateTime.now()
                )
        );
    }

    private ContaEntity criarConta(MensagemTransacaoSqsDTO mensagem) {
        ContaEntity conta = new ContaEntity();
        conta.setId(mensagem.getConta().getId());
        conta.setTitular(mensagem.getConta().getTitular());
        conta.setSaldo(mensagem.getConta().getSaldo().getValor());
        conta.setMoeda(mensagem.getConta().getSaldo().getMoeda());
        conta.setUltimaAtualizacao(mensagem.getTransacao().getTimestamp());
        return conta;
    }
}
