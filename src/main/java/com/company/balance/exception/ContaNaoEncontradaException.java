package com.company.balance.exception;

import java.util.UUID;

public class ContaNaoEncontradaException extends RuntimeException {

    public ContaNaoEncontradaException(UUID idConta) {
        super("Conta não encontrada: " + idConta);
    }
}
