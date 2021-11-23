package com.alay.tasktracker.controllers;

import com.alay.tasktracker.entities.Task;
import com.alay.tasktracker.services.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public String showTasks(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/tasks";
    }

    @GetMapping("/addtask")
    public String showAddTaskForm(Task task) {
        return "add-task";
    }

    @PostMapping("/addtask")
    public String addTask(@Valid Task task, BindingResult result) {
        if (result.hasErrors()) {
            return "add-task";
        }

        taskService.createTask(task);

        return "redirect:/tasks";
    }

    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable("id") long id) {
        taskService.completeTask(id);

        return "redirect:/tasks";
    }

    @GetMapping("/reassign")
    public String reassignTasks() {
        taskService.reassignTasks();

        return "redirect:/tasks";
    }
}
