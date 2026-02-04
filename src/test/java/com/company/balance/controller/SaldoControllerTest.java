package com.company.balance.controller;

import com.company.balance.dto.RespostaSaldoDTO;
import com.company.balance.dto.SaldoDTO;
import com.company.balance.enums.Moeda;
import com.company.balance.exception.ContaNaoEncontradaException;
import com.company.balance.service.ConsultaSaldoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaldoController.class)
@ActiveProfiles("test")
class SaldoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultaSaldoService consultaSaldoService;

    @Test
    @DisplayName("Deve retornar status 200 e dados da conta quando encontrada")
    void deveRetornar200QuandoContaEncontrada() throws Exception {
        UUID idConta = UUID.randomUUID();
        UUID idTitular = UUID.randomUUID();
        
        RespostaSaldoDTO resposta = new RespostaSaldoDTO(
                idConta,
                idTitular,
                new SaldoDTO(new BigDecimal("200.50"), Moeda.BRL),
                OffsetDateTime.now()
        );

        when(consultaSaldoService.consultar(idConta)).thenReturn(resposta);

        mockMvc.perform(get("/api/v1/saldos/{idConta}", idConta))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idConta.toString()))
                .andExpect(jsonPath("$.owner").value(idTitular.toString()))
                .andExpect(jsonPath("$.balance.amount").value(200.50))
                .andExpect(jsonPath("$.balance.currency").value("BRL"));
    }

    @Test
    @DisplayName("Deve retornar status 404 quando conta não encontrada")
    void deveRetornar404QuandoContaNaoEncontrada() throws Exception {
        UUID idConta = UUID.randomUUID();

        when(consultaSaldoService.consultar(idConta))
                .thenThrow(new ContaNaoEncontradaException(idConta));

        mockMvc.perform(get("/api/v1/saldos/{idConta}", idConta))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").exists());
    }
}
