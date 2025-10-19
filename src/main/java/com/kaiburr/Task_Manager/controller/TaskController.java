package com.kaiburr.Task_Manager.controller;

import com.kaiburr.Task_Manager.model.Task;
import com.kaiburr.Task_Manager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // GET all tasks or single task by ID
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        if (id != null) {
            Optional<Task> task = taskService.getTaskById(id);
            if (task.isPresent()) {
                return ResponseEntity.ok(task.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Task not found with id: " + id);
            }
        }
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // Search tasks by name
    @GetMapping("/search")
    public ResponseEntity<?> searchTasks(@RequestParam String name) {
        List<Task> tasks = taskService.searchTasksByName(name);
        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No tasks found containing: " + name);
        }
        return ResponseEntity.ok(tasks);
    }

    // Create or update a task
    @PutMapping
    public ResponseEntity<?> createOrUpdateTask(@RequestBody Task task) {
        try {
            Task savedTask = taskService.createTask(task);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task not found with id: " + id);
        }
    }

    // Execute a task
    @PutMapping("/{id}/execute")
    public ResponseEntity<?> executeTask(@PathVariable String id) {
        try {
            Task task = taskService.executeTask(id);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}