package com.isga.pr2.controller;

import com.isga.pr2.model.*;
import com.isga.pr2.service.BanqueService;
import com.isga.pr2.service.PdfService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/operations")
public class OperationController {

    private final BanqueService banqueService;
    private final PdfService pdfService;

    public OperationController(BanqueService banqueService, PdfService pdfService) {
        this.banqueService = banqueService;
        this.pdfService    = pdfService;
    }

    @GetMapping("/compte/{numero}")
    public String listeOperations(@PathVariable String numero, Model model) {
        ComptesBancaires compte = banqueService.getCompteByNumero(numero);
        List<Operation> operations = banqueService.getOperationsByCompte(numero);
        double totalDepots   = banqueService.getTotalDepots(numero);
        double totalRetraits = banqueService.getTotalRetraits(numero);

        model.addAttribute("compte", compte);
        model.addAttribute("client", compte.getClient());
        model.addAttribute("operations", operations);
        model.addAttribute("totalDepots", totalDepots);
        model.addAttribute("totalRetraits", totalRetraits);
        model.addAttribute("activeNav", "operations");
        return "operations/liste";
    }

    @PostMapping("/depot")
    public String depot(@RequestParam String numeroCompte,
                        @RequestParam double montant,
                        @RequestParam(required = false) String description,
                        RedirectAttributes attrs) {
        try {
            banqueService.effectuerDepot(numeroCompte, montant, description);
            attrs.addFlashAttribute("successMsg",
                    String.format("Dépôt de %.2f MAD effectué avec succès.", montant));
        } catch (Exception e) {
            attrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/operations/compte/" + numeroCompte;
    }

    @PostMapping("/retrait")
    public String retrait(@RequestParam String numeroCompte,
                          @RequestParam double montant,
                          @RequestParam(required = false) String description,
                          RedirectAttributes attrs) {
        try {
            banqueService.effectuerRetrait(numeroCompte, montant, description);
            attrs.addFlashAttribute("successMsg",
                    String.format("Retrait de %.2f MAD effectué avec succès.", montant));
        } catch (Exception e) {
            attrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/operations/compte/" + numeroCompte;
    }

    @GetMapping("/releve/{numero}")
    public ResponseEntity<byte[]> genererReleve(@PathVariable String numero) {
        try {
            ComptesBancaires compte = banqueService.getCompteByNumero(numero);
            Client client = compte.getClient();
            List<Operation> operations = banqueService.getOperationsByCompte(numero);

            byte[] pdf = pdfService.genererReleve(client, compte, operations);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("releve_" + numero + ".pdf").build());
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
