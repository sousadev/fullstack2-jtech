package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.application.ports.input.TaskInputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskInputGateway gateway;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request, @RequestHeader String authorization) {
        TaskResponse response = gateway.create(authorization.replace("Bearer ", ""), request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable String id, @RequestBody TaskRequest request, @RequestHeader String authorization) {
        TaskResponse response = gateway.update(authorization.replace("Bearer ", ""), id, request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @RequestHeader String authorization) {
        gateway.delete(authorization.replace("Bearer ", ""), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable String id, @RequestHeader String authorization) {
        TaskResponse response = gateway.findById(authorization.replace("Bearer ", ""), id);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<TaskResponse>> findAllByGroup(@PathVariable String groupId, @RequestHeader String authorization) {
        List<TaskResponse> response = gateway.findAllByGroup(authorization.replace("Bearer ", ""), groupId);
        return ResponseEntity.ok(response);
    }
}
