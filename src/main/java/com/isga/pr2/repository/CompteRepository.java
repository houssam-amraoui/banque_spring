package com.isga.pr2.repository;

import com.isga.pr2.model.ComptesBancaires;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompteRepository extends JpaRepository<ComptesBancaires, String> {

    @Query("SELECT c FROM ComptesBancaires c WHERE c.client.id = :clientId")
    List<ComptesBancaires> findByClientId(Long clientId);
}
