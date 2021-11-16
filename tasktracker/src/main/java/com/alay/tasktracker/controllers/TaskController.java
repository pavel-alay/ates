package com.alay.tasktracker.controllers;

import com.alay.tasktracker.entities.Task;
import com.alay.tasktracker.entities.TaskStatus;
import com.alay.tasktracker.events.EventProducer;
import com.alay.tasktracker.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.UUID;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final EventProducer producer;

    @Autowired
    public TaskController(TaskRepository taskRepository, EventProducer producer) {
        this.taskRepository = taskRepository;
        this.producer = producer;
    }

    @GetMapping("/tasks")
    public String showTasks(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "tasks";
    }

    @GetMapping("/addtask")
    public String showAddTaskForm(Task task) {
        return "add-task";
    }

    @PostMapping("/addtask")
    public String addTask(@Valid Task task, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-task";
        }
        task.setPublicId(UUID.randomUUID().toString());
        task.setStatus(TaskStatus.Open);

        taskRepository.save(task);
        producer.taskCreated(task);
        return "redirect:/tasks";
    }
}
