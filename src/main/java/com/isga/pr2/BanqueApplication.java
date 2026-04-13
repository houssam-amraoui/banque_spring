package com.isga.pr2;

import com.isga.pr2.model.*;
import com.isga.pr2.service.BanqueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class BanqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(BanqueApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(BanqueService service) {
        return args -> {
            if (!service.getAllClients().isEmpty()) {
                return;
            }
            // Clients
            Client c1 = service.saveClient(Client.builder()
                    .nom("ALAOUI").prenom("Mohammed")
                    .email("m.alaoui@email.ma")
                    .telephone("0612345678")
                    .adresse("12 Rue Hassan II, Casablanca")
                    .build());

            Client c2 = service.saveClient(Client.builder()
                    .nom("BENALI").prenom("Fatima")
                    .email("f.benali@email.ma")
                    .telephone("0698765432")
                    .adresse("45 Boulevard Zerktouni, Rabat")
                    .build());

            Client c3 = service.saveClient(Client.builder()
                    .nom("CHERKAOUI").prenom("Youssef")
                    .email("y.cherkaoui@email.ma")
                    .telephone("0677889900")
                    .adresse("8 Avenue Al Massira, Marrakech")
                    .build());

            // Comptes
            CompteCourant cc1 = new CompteCourant("CPT-CC-001", 15000, LocalDate.now().minusMonths(6), c1, 5000);
            CompteEpargne ce1 = new CompteEpargne("CPT-CE-001", 30000, LocalDate.now().minusYears(1), c1, 3.5);
            CompteCourant cc2 = new CompteCourant("CPT-CC-002", 8000, LocalDate.now().minusMonths(3), c2, 2000);
            CompteEpargne ce2 = new CompteEpargne("CPT-CE-002", 50000, LocalDate.now().minusYears(2), c3, 4.0);

            service.saveCompte(cc1);
            service.saveCompte(ce1);
            service.saveCompte(cc2);
            service.saveCompte(ce2);

            // Opérations
            service.effectuerDepot("CPT-CC-001", 5000, "Salaire janvier");
            service.effectuerDepot("CPT-CC-001", 2000, "Virement reçu");
            service.effectuerRetrait("CPT-CC-001", 1500, "Loyer");
            service.effectuerRetrait("CPT-CC-001", 300, "Facture électricité");
            service.effectuerDepot("CPT-CC-001", 800, "Remboursement");

            service.effectuerDepot("CPT-CE-001", 10000, "Épargne mensuelle");
            service.effectuerDepot("CPT-CE-001", 5000, "Bonus annuel");

            service.effectuerDepot("CPT-CC-002", 3000, "Salaire");
            service.effectuerRetrait("CPT-CC-002", 500, "Courses");
            service.effectuerRetrait("CPT-CC-002", 200, "Transport");

            System.out.println("✅ Données de démonstration chargées avec succès !");
        };
    }
}
