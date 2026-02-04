package com.company.balance.listener;

import com.company.balance.dto.MensagemTransacaoSqsDTO;
import com.company.balance.service.AtualizacaoSaldoService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransacaoSqsListener {

    private final AtualizacaoSaldoService atualizacaoSaldoService;

    @SqsListener("transacoes-financeiras-processadas")
    public void receber(MensagemTransacaoSqsDTO mensagem) {
        atualizacaoSaldoService.processar(mensagem);
    }
}
