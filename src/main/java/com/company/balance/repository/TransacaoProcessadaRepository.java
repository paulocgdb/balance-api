package com.company.balance.repository;

import com.company.balance.entity.TransacaoProcessadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransacaoProcessadaRepository
        extends JpaRepository<TransacaoProcessadaEntity, UUID> {
}
