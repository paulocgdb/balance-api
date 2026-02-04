package com.company.balance.service;

import com.company.balance.dto.ContaDTO;
import com.company.balance.dto.MensagemTransacaoSqsDTO;
import com.company.balance.dto.SaldoDTO;
import com.company.balance.dto.TransacaoDTO;
import com.company.balance.entity.ContaEntity;
import com.company.balance.entity.TransacaoProcessadaEntity;
import com.company.balance.enums.Moeda;
import com.company.balance.enums.StatusTransacao;
import com.company.balance.enums.TipoTransacao;
import com.company.balance.repository.ContaRepository;
import com.company.balance.repository.TransacaoProcessadaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizacaoSaldoServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoProcessadaRepository transacaoProcessadaRepository;

    @InjectMocks
    private AtualizacaoSaldoService service;

    @Test
    @DisplayName("Deve ignorar transação se já foi processada (Idempotência)")
    void deveIgnorarTransacaoJaProcessada() {
        UUID idTransacao = UUID.randomUUID();
        MensagemTransacaoSqsDTO mensagem = criarMensagem(idTransacao, UUID.randomUUID(), 1000L);

        when(transacaoProcessadaRepository.existsById(idTransacao)).thenReturn(true);

        service.processar(mensagem);

        verify(transacaoProcessadaRepository, never()).save(any());
        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar nova conta se não existir")
    void deveCriarNovaContaSeNaoExistir() {
        UUID idTransacao = UUID.randomUUID();
        UUID idConta = UUID.randomUUID();
        MensagemTransacaoSqsDTO mensagem = criarMensagem(idTransacao, idConta, 1634874339000000L);

        when(transacaoProcessadaRepository.existsById(idTransacao)).thenReturn(false);
        when(contaRepository.buscarPorIdComLock(idConta)).thenReturn(Optional.empty());

        service.processar(mensagem);

        ArgumentCaptor<ContaEntity> contaCaptor = ArgumentCaptor.forClass(ContaEntity.class);
        verify(contaRepository).save(contaCaptor.capture());

        ContaEntity contaSalva = contaCaptor.getValue();
        assertEquals(idConta, contaSalva.getId());
        assertEquals(new BigDecimal("100.00"), contaSalva.getSaldo());
        assertEquals(Moeda.BRL, contaSalva.getMoeda());

        verify(transacaoProcessadaRepository).save(any(TransacaoProcessadaEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar saldo de conta existente")
    void deveAtualizarSaldoDeContaExistente() {
        UUID idTransacao = UUID.randomUUID();
        UUID idConta = UUID.randomUUID();
        long timestampMicros = 1751641364589998L; 
        MensagemTransacaoSqsDTO mensagem = criarMensagem(idTransacao, idConta, timestampMicros);

        ContaEntity contaExistente = new ContaEntity();
        contaExistente.setId(idConta);
        contaExistente.setSaldo(BigDecimal.ZERO);
        contaExistente.setUltimaAtualizacao(OffsetDateTime.MIN);

        when(transacaoProcessadaRepository.existsById(idTransacao)).thenReturn(false);
        when(contaRepository.buscarPorIdComLock(idConta)).thenReturn(Optional.of(contaExistente));

        service.processar(mensagem);

        ArgumentCaptor<ContaEntity> contaCaptor = ArgumentCaptor.forClass(ContaEntity.class);
        verify(contaRepository).save(contaCaptor.capture());

        ContaEntity contaAtualizada = contaCaptor.getValue();
        assertEquals(new BigDecimal("100.00"), contaAtualizada.getSaldo());
    }

    @Test
    @DisplayName("Não deve atualizar saldo se transação for mais antiga que a última atualização")
    void naoDeveAtualizarSaldoSeTransacaoAntiga() {
        UUID idTransacao = UUID.randomUUID();
        UUID idConta = UUID.randomUUID();
        long timestampAntigo = 1000000000000000L;
        MensagemTransacaoSqsDTO mensagem = criarMensagem(idTransacao, idConta, timestampAntigo);

        ContaEntity contaExistente = new ContaEntity();
        contaExistente.setId(idConta);
        contaExistente.setSaldo(new BigDecimal("500.00"));
        contaExistente.setUltimaAtualizacao(OffsetDateTime.now());

        when(transacaoProcessadaRepository.existsById(idTransacao)).thenReturn(false);
        when(contaRepository.buscarPorIdComLock(idConta)).thenReturn(Optional.of(contaExistente));

        service.processar(mensagem);

        ArgumentCaptor<ContaEntity> contaCaptor = ArgumentCaptor.forClass(ContaEntity.class);
        verify(contaRepository).save(contaCaptor.capture());

        ContaEntity contaSalva = contaCaptor.getValue();

        assertEquals(new BigDecimal("500.00"), contaSalva.getSaldo());
    }

    private MensagemTransacaoSqsDTO criarMensagem(UUID idTransacao, UUID idConta, Long timestamp) {
        MensagemTransacaoSqsDTO mensagem = new MensagemTransacaoSqsDTO();
        
        TransacaoDTO transacao = new TransacaoDTO();
        ReflectionTestUtils.setField(transacao, "id", idTransacao);
        ReflectionTestUtils.setField(transacao, "tipo", TipoTransacao.CREDIT);
        ReflectionTestUtils.setField(transacao, "valor", new BigDecimal("50.00"));
        ReflectionTestUtils.setField(transacao, "moeda", Moeda.BRL);
        ReflectionTestUtils.setField(transacao, "status", StatusTransacao.APPROVED);
        ReflectionTestUtils.setField(transacao, "timestamp", timestamp);

        ContaDTO conta = new ContaDTO();
        ReflectionTestUtils.setField(conta, "id", idConta);
        ReflectionTestUtils.setField(conta, "titular", UUID.randomUUID());
        
        SaldoDTO saldo = new SaldoDTO(new BigDecimal("100.00"), Moeda.BRL);
        ReflectionTestUtils.setField(conta, "saldo", saldo);

        ReflectionTestUtils.setField(mensagem, "transacao", transacao);
        ReflectionTestUtils.setField(mensagem, "conta", conta);
        
        return mensagem;
    }
}
