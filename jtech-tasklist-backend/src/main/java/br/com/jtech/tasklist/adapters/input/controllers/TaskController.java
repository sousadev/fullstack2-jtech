package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.input.TaskInputGateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskInputGateway gateway;

    public TaskController(TaskInputGateway gateway) {
        this.gateway = gateway;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll() {
        List<TaskResponse> dtos = TaskResponse.of(gateway.findAll());
        return ResponseEntity.ok(dtos);
    };

    @PostMapping
    public ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request) {

        Task task = Task.of(request);
        Task createdTask = gateway.create(task);

        TaskResponse response = TaskResponse.of(createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
