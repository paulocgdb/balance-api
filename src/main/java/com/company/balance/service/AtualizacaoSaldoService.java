package com.company.balance.service;

import com.company.balance.dto.MensagemTransacaoSqsDTO;
import com.company.balance.entity.ContaEntity;
import com.company.balance.entity.TransacaoProcessadaEntity;
import com.company.balance.repository.ContaRepository;
import com.company.balance.repository.TransacaoProcessadaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtualizacaoSaldoService {

    private final ContaRepository contaRepository;
    private final TransacaoProcessadaRepository transacaoProcessadaRepository;

    @Transactional
    public void processar(MensagemTransacaoSqsDTO mensagem) {

        UUID idTransacao = mensagem.getTransacao().getId();

        if (transacaoProcessadaRepository.existsById(idTransacao)) {
            log.info("Transação [{}] ignorada (Idempotência: já processada).", idTransacao);
            return;
        }

        UUID idConta = mensagem.getConta().getId();

        ContaEntity conta = contaRepository
                .buscarPorIdComLock(idConta)
                .orElseGet(() -> {
                    log.info("Conta [{}] não encontrada. Criando nova conta...", idConta);
                    return criarConta(mensagem);
                });

        OffsetDateTime dataTransacao = converterTimestamp(mensagem.getTransacao().getTimestamp());

        log.info("Atualizando saldo da conta [{}]. Valor: [{} {}], Tipo: [{}].",
                idConta,
                mensagem.getTransacao().getValor(),
                mensagem.getTransacao().getMoeda(),
                mensagem.getTransacao().getTipo());

        conta.atualizarSaldo(
                mensagem.getConta().getSaldo().getValor(),
                dataTransacao
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
        conta.setUltimaAtualizacao(converterTimestamp(mensagem.getTransacao().getTimestamp()));
        return conta;
    }

    private OffsetDateTime converterTimestamp(Long timestampMicros) {
        if (timestampMicros == null) return OffsetDateTime.now();
        long seconds = timestampMicros / 1_000_000;
        long nanos = (timestampMicros % 1_000_000) * 1_000;
        return Instant.ofEpochSecond(seconds, nanos).atOffset(UTC);
    }
}
