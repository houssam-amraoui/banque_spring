package com.isga.pr2.controller;

import com.isga.pr2.model.Client;
import com.isga.pr2.service.BanqueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final BanqueService banqueService;

    public ClientController(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    @GetMapping
    public String liste(Model model, @RequestParam(required = false) String search) {
        List<Client> clients = (search != null && !search.isBlank())
                ? banqueService.searchClients(search)
                : banqueService.getAllClients();
        long totalComptes = clients.stream().mapToLong(c -> c.getComptes().size()).sum();
        model.addAttribute("clients", clients);
        model.addAttribute("totalComptes", totalComptes);
        model.addAttribute("search", search);
        model.addAttribute("activeNav", "clients");
        return "clients/liste";
    }

    @GetMapping("/nouveau")
    public String formulaire(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("activeNav", "clients");
        return "clients/form";
    }

    @GetMapping("/modifier/{id}")
    public String modifier(@PathVariable Long id, Model model) {
        Client client = banqueService.getClientById(id);
        model.addAttribute("client", client);
        model.addAttribute("activeNav", "clients");
        return "clients/form";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarder(@Valid @ModelAttribute("client") Client client,
                              BindingResult result,
                              Model model,
                              RedirectAttributes attrs) {
        if (result.hasErrors()) {
            model.addAttribute("activeNav", "clients");
            return "clients/form";
        }
        banqueService.saveClient(client);
        String msg = (client.getId() != null)
                ? "Client mis à jour avec succès !"
                : "Client enregistré avec succès !";
        attrs.addFlashAttribute("successMsg", msg);
        return "redirect:/clients";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id, RedirectAttributes attrs) {
        banqueService.deleteClient(id);
        attrs.addFlashAttribute("successMsg", "Client supprimé.");
        return "redirect:/clients";
    }
}
