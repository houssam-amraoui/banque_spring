package com.isga.pr2.repository;

import com.isga.pr2.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByCompteNumeroCompteOrderByDateOperationDesc(String numeroCompte);
}
