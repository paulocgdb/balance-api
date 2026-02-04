package com.company.balance.listener;

import com.company.balance.dto.MensagemTransacaoSqsDTO;
import com.company.balance.service.AtualizacaoSaldoService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransacaoSqsListener {

    private final AtualizacaoSaldoService atualizacaoSaldoService;

    @SqsListener("transacoes-financeiras-processadas")
    public void receber(MensagemTransacaoSqsDTO mensagem) {
        try {
            atualizacaoSaldoService.processar(mensagem);
        } catch (DataIntegrityViolationException e) {
            log.warn("Concorrência detectada ao processar transação (Conta ou Transação já existente). Retentando... Erro: {}", e.getMessage());
            atualizacaoSaldoService.processar(mensagem);
        }
    }
}
