package com.alay.billing.controllers;

import com.alay.billing.repositories.UserRepository;
import com.alay.billing.services.BillingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BillingController {

    final BillingService billingService;
    final UserRepository userRepository;

    public BillingController(BillingService billingService, UserRepository userRepository) {
        this.billingService = billingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/transactions")
    public String showTransactions(Model model) {
        model.addAttribute("transactions", billingService.findAll());
        return "transactions";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/transactions";
    }

    @GetMapping("/payment")
    public String payment() {
        billingService.createPayments();
        return "redirect:/transactions";
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }
}
