package com.isga.pr2.service;

import com.isga.pr2.model.*;
import com.isga.pr2.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BanqueService {

    private final ClientRepository clientRepo;
    private final CompteRepository compteRepo;
    private final OperationRepository operationRepo;

    public BanqueService(ClientRepository clientRepo, CompteRepository compteRepo,
                         OperationRepository operationRepo) {
        this.clientRepo = clientRepo;
        this.compteRepo = compteRepo;
        this.operationRepo = operationRepo;
    }

    // ======== CLIENTS ========
    public List<Client> getAllClients() {
        return clientRepo.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable: " + id));
    }

    public Client saveClient(Client client) {
        return clientRepo.save(client);
    }

    public void deleteClient(Long id) {
        clientRepo.deleteById(id);
    }

    public List<Client> searchClients(String query) {
        return clientRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(query, query);
    }

    // ======== COMPTES ========
    public List<ComptesBancaires> getComptesByClient(Long clientId) {
        return compteRepo.findByClientId(clientId);
    }

    public ComptesBancaires getCompteByNumero(String numero) {
        return compteRepo.findById(numero)
                .orElseThrow(() -> new RuntimeException("Compte introuvable: " + numero));
    }

    public ComptesBancaires saveCompte(ComptesBancaires compte) {
        return compteRepo.save(compte);
    }

    // ======== OPERATIONS ========
    public List<Operation> getOperationsByCompte(String numeroCompte) {
        return operationRepo.findByCompteNumeroCompteOrderByDateOperationDesc(numeroCompte);
    }

    public void effectuerDepot(String numeroCompte, double montant, String description) {
        ComptesBancaires compte = getCompteByNumero(numeroCompte);
        Operation op = Operation.builder()
                .type(TypeOperation.DEPOT)
                .montant(montant)
                .description(description)
                .compte(compte)
                .build();
        operationRepo.save(op);
        compte.setSolde(compte.getSolde() + montant);
        compteRepo.save(compte);
    }

    public void effectuerRetrait(String numeroCompte, double montant, String description) {
        ComptesBancaires compte = getCompteByNumero(numeroCompte);

        double limiteRetrait = compte.getSolde();
        if (compte instanceof CompteCourant cc) {
            limiteRetrait += cc.getDecouvertAutorise();
        }
        if (montant > limiteRetrait) {
            throw new RuntimeException("Solde insuffisant. Solde disponible : " +
                    String.format("%.2f", limiteRetrait) + " MAD");
        }

        Operation op = Operation.builder()
                .type(TypeOperation.RETRAIT)
                .montant(montant)
                .description(description)
                .compte(compte)
                .build();
        operationRepo.save(op);
        compte.setSolde(compte.getSolde() - montant);
        compteRepo.save(compte);
    }

    // ======== STATISTIQUES ========
    public double getTotalDepots(String numeroCompte) {
        return getOperationsByCompte(numeroCompte).stream()
                .filter(op -> op.getType() == TypeOperation.DEPOT)
                .mapToDouble(Operation::getMontant)
                .sum();
    }

    public double getTotalRetraits(String numeroCompte) {
        return getOperationsByCompte(numeroCompte).stream()
                .filter(op -> op.getType() == TypeOperation.RETRAIT)
                .mapToDouble(Operation::getMontant)
                .sum();
    }
}
