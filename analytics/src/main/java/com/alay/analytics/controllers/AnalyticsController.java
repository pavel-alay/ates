package com.alay.analytics.controllers;

import com.alay.analytics.entities.Task;
import com.alay.analytics.entities.User;
import com.alay.analytics.repositories.TaskRepository;
import com.alay.analytics.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AnalyticsController {

    final TaskRepository taskRepository;
    final UserRepository userRepository;

    public AnalyticsController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/stats";
    }

    @GetMapping("/stats")
    public String showStats(Model model) {
        List<User> users = userRepository.findAll();
        long debtors = users.stream()
            .filter(u -> u.getBalance() < 0).count();
        long profit = users.stream()
            .mapToLong(User::getBalance).sum();
        model.addAttribute("debtors", debtors);
        model.addAttribute("profit", profit);

        Task mostExpensive = taskRepository.findCompletedForTodayOrderByRewardDesc().stream()
            .findFirst().orElse(null);
        model.addAttribute("mostExpensive", mostExpensive);
        return "stats";
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userRepository.findAll(Sort.by(Sort.Direction.ASC, "balance")));
        return "users";
    }

    @GetMapping("/tasks")
    public String showTasks(Model model) {
        List<Task> completed = taskRepository.findCompletedForTodayOrderByRewardDesc();
        model.addAttribute("tasks", completed);
        return "tasks";
    }
}
