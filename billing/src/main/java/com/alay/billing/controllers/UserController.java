package com.alay.billing.controllers;

import com.alay.billing.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    final UserRepository userRepository;


    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String showTransactions(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }
}
