package com.company.balance.repository;

import com.company.balance.entity.ContaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<ContaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ContaEntity c where c.id = :id")
    Optional<ContaEntity> buscarPorIdComLock(UUID id);
}
