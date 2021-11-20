package com.alay.billing.controllers;

import com.alay.billing.services.BillingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BillingController {

    final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
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


        return "redirect:/transactions";
    }

}
