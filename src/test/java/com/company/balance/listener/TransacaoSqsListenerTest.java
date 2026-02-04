package com.company.balance.listener;

import com.company.balance.dto.ContaDTO;
import com.company.balance.dto.MensagemTransacaoSqsDTO;
import com.company.balance.dto.SaldoDTO;
import com.company.balance.dto.TransacaoDTO;
import com.company.balance.service.AtualizacaoSaldoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoSqsListenerTest {

    @Mock
    private AtualizacaoSaldoService service;

    @InjectMocks
    private TransacaoSqsListener listener;

    @Test
    @DisplayName("Deve processar mensagem com sucesso")
    void deveProcessarMensagemComSucesso() {
        MensagemTransacaoSqsDTO mensagem = criarMensagemDummy();

        doNothing().when(service).processar(mensagem);

        listener.receber(mensagem);

        verify(service, times(1)).processar(mensagem);
    }

    @Test
    @DisplayName("Deve retentar processamento em caso de erro de concorrência")
    void deveRetentarEmCasoDeConcorrencia() {
        MensagemTransacaoSqsDTO mensagem = criarMensagemDummy();

        doThrow(new DataIntegrityViolationException("Erro de chave duplicada"))
                .doNothing()
                .when(service).processar(mensagem);

        listener.receber(mensagem);

        verify(service, times(2)).processar(mensagem);
    }
    
    @Test
    @DisplayName("Deve propagar erro se falhar no retry")
    void devePropagarErroSeFalharNoRetry() {
        MensagemTransacaoSqsDTO mensagem = criarMensagemDummy();

        doThrow(new DataIntegrityViolationException("Erro de chave duplicada"))
                .doThrow(new DataIntegrityViolationException("Erro de chave duplicada novamente"))
                .when(service).processar(mensagem);

        try {
            listener.receber(mensagem);
        } catch (DataIntegrityViolationException e) {
        }

        verify(service, times(2)).processar(mensagem);
    }

    private MensagemTransacaoSqsDTO criarMensagemDummy() {
        MensagemTransacaoSqsDTO mensagem = new MensagemTransacaoSqsDTO();
        TransacaoDTO transacao = new TransacaoDTO();
        ReflectionTestUtils.setField(transacao, "id", UUID.randomUUID());
        ContaDTO conta = new ContaDTO();
        ReflectionTestUtils.setField(conta, "id", UUID.randomUUID());
        
        ReflectionTestUtils.setField(mensagem, "transacao", transacao);
        ReflectionTestUtils.setField(mensagem, "conta", conta);
        return mensagem;
    }
}
