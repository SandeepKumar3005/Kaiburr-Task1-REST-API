package com.kaiburr.Task_Manager.service;

import com.kaiburr.Task_Manager.model.Task;
import com.kaiburr.Task_Manager.model.TaskExecution;
import com.kaiburr.Task_Manager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Dangerous command patterns
    private static final Pattern[] DANGEROUS_PATTERNS = {
            Pattern.compile(".*rm\\s+-rf.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*del\\s+/f.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*format.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*shutdown.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*reboot.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*;.*", Pattern.CASE_INSENSITIVE),  // Command chaining
            Pattern.compile(".*&&.*", Pattern.CASE_INSENSITIVE),  // Command chaining
            Pattern.compile(".*\\|\\|.*", Pattern.CASE_INSENSITIVE),  // Command chaining
            Pattern.compile(".*>.*", Pattern.CASE_INSENSITIVE),  // Redirection
            Pattern.compile(".*<.*", Pattern.CASE_INSENSITIVE),  // Redirection
    };

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public List<Task> searchTasksByName(String name) {
        return taskRepository.findByNameContaining(name);
    }

    public Task createTask(Task task) {
        // Validate command
        validateCommand(task.getCommand());
        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public Task executeTask(String id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (!taskOptional.isPresent()) {
            throw new RuntimeException("Task not found with id: " + id);
        }

        Task task = taskOptional.get();
        validateCommand(task.getCommand());

        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());

        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;
            
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", task.getCommand());
            } else {
                processBuilder = new ProcessBuilder("sh", "-c", task.getCommand());
            }

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
            execution.setEndTime(new Date());
            execution.setOutput(output.toString());

        } catch (Exception e) {
            execution.setEndTime(new Date());
            execution.setOutput("Error: " + e.getMessage());
        }

        task.getTaskExecutions().add(execution);
        return taskRepository.save(task);
    }

    private void validateCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }

        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(command).matches()) {
                throw new IllegalArgumentException(
                        "Command contains unsafe/malicious code: " + command);
            }
        }
    }
}