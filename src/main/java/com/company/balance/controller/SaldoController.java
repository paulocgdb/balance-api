package com.company.balance.controller;

import com.company.balance.dto.RespostaSaldoDTO;
import com.company.balance.service.ConsultaSaldoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/saldos")
@RequiredArgsConstructor
public class SaldoController {

    private final ConsultaSaldoService consultaSaldoService;

    @GetMapping("/{idConta}")
    public RespostaSaldoDTO consultar(@PathVariable UUID idConta) {
        return consultaSaldoService.consultar(idConta);
    }
}
