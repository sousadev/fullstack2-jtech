package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.application.ports.input.TaskGroupInputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/task-groups")
@RequiredArgsConstructor
public class TaskGroupController {

    private final TaskGroupInputGateway gateway;

    @PostMapping
    public ResponseEntity<TaskGroupResponse> create(@RequestBody TaskGroupRequest request,
            @RequestHeader String authorization) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gateway.create(authorization.replace("Bearer ", ""), request));
    }

    @GetMapping
    public ResponseEntity<List<TaskGroupResponse>> findAll(@RequestHeader String authorization) {
        return ResponseEntity.ok(gateway.findAllByUser(authorization.replace("Bearer ", "")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskGroupResponse> findById(@RequestHeader String authorization, @PathVariable String id) {
        System.out.println("ID: " + id);
        return ResponseEntity.ok().body(gateway.findById(id, authorization.replace("Bearer ", "")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskGroupResponse> updateTaskGroup(@PathVariable String id,
            @RequestBody TaskGroupRequest request, @RequestHeader String authorization) {
        return ResponseEntity.ok().body(gateway.update(id, request, authorization.replace("Bearer ", "")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskGroup(@PathVariable String id, @RequestHeader String authorization) {
        gateway.delete(id, authorization.replace("Bearer ", ""));
        return ResponseEntity.noContent().build();
    }
}
