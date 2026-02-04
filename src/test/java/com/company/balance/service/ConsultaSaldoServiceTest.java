package com.company.balance.service;

import com.company.balance.dto.RespostaSaldoDTO;
import com.company.balance.entity.ContaEntity;
import com.company.balance.enums.Moeda;
import com.company.balance.exception.ContaNaoEncontradaException;
import com.company.balance.repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaSaldoServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ConsultaSaldoService service;

    @Test
    @DisplayName("Deve retornar saldo quando conta existe")
    void deveRetornarSaldoQuandoContaExiste() {
        UUID idConta = UUID.randomUUID();
        ContaEntity conta = new ContaEntity();
        conta.setId(idConta);
        conta.setTitular(UUID.randomUUID());
        conta.setSaldo(new BigDecimal("150.00"));
        conta.setMoeda(Moeda.BRL);
        conta.setUltimaAtualizacao(OffsetDateTime.now());

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));

        RespostaSaldoDTO resposta = service.consultar(idConta);

        assertNotNull(resposta);
        assertEquals(idConta, resposta.getId());
        assertEquals(new BigDecimal("150.00"), resposta.getSaldo().getValor());
        assertEquals(Moeda.BRL, resposta.getSaldo().getMoeda());
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta não existe")
    void deveLancarExcecaoQuandoContaNaoExiste() {
        UUID idConta = UUID.randomUUID();

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaException.class, () -> service.consultar(idConta));
    }
}
