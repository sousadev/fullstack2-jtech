package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.application.ports.input.TaskGroupInputGateway;
import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/taskgroups")
@RequiredArgsConstructor
public class TaskGroupController {

    private final TaskGroupInputGateway gateway;

    @GetMapping
    public ResponseEntity<List<TaskGroupResponse>> findAll() {
        List<TaskGroupResponse> dtoList = TaskGroupResponse.of(gateway.findAll());
        return ResponseEntity.ok(dtoList);
    }

}
