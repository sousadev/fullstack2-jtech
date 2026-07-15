package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.application.ports.input.TaskGroupInputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task-groups")
@RequiredArgsConstructor
public class TaskGroupController {

    private final TaskGroupInputGateway gateway;

    @PostMapping
    public ResponseEntity<TaskGroupResponse> create(@RequestBody TaskGroupRequest request, @RequestHeader String authorization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gateway.create(authorization.replace("Bearer ", ""), request));
    }

    @GetMapping
    public ResponseEntity<List<TaskGroupResponse>> findAll(@RequestHeader String authorization) {
        return ResponseEntity.ok(gateway.findAllByUser(authorization.replace("Bearer ", "")));
    }
}
