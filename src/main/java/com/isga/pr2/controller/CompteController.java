package com.isga.pr2.controller;

import com.isga.pr2.model.*;
import com.isga.pr2.service.BanqueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/comptes")
public class CompteController {

    private final BanqueService banqueService;

    public CompteController(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    @GetMapping("/client/{clientId}")
    public String listeComptes(@PathVariable Long clientId, Model model) {
        Client client = banqueService.getClientById(clientId);
        List<ComptesBancaires> comptes = banqueService.getComptesByClient(clientId);
        double soldeTotal = comptes.stream().mapToDouble(ComptesBancaires::getSolde).sum();
        long totalOperations = comptes.stream().mapToLong(c -> c.getOperations().size()).sum();
        model.addAttribute("client", client);
        model.addAttribute("comptes", comptes);
        model.addAttribute("soldeTotal", soldeTotal);
        model.addAttribute("totalOperations", totalOperations);
        model.addAttribute("activeNav", "comptes");
        return "comptes/liste";
    }

    @GetMapping("/nouveau/{clientId}")
    public String formulaire(@PathVariable Long clientId, Model model) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("client", banqueService.getClientById(clientId));
        model.addAttribute("activeNav", "comptes");
        return "comptes/form";
    }

    @PostMapping("/sauvegarder/{clientId}")
    public String sauvegarder(@PathVariable Long clientId,
                              @RequestParam String typeCompte,
                              @RequestParam double soldeInitial,
                              @RequestParam(required = false) Double decouvert,
                              @RequestParam(required = false) Double taux,
                              RedirectAttributes attrs) {
        Client client = banqueService.getClientById(clientId);
        String numero = "CPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ComptesBancaires compte;
        if ("COURANT".equals(typeCompte)) {
            compte = new CompteCourant(numero, soldeInitial, LocalDate.now(), client,
                    decouvert != null ? decouvert : 0);
        } else {
            compte = new CompteEpargne(numero, soldeInitial, LocalDate.now(), client,
                    taux != null ? taux : 0);
        }
        banqueService.saveCompte(compte);
        attrs.addFlashAttribute("successMsg", "Compte créé : " + numero);
        return "redirect:/comptes/client/" + clientId;
    }
}
