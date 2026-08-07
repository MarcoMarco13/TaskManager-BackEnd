package com.projeto.projeto.controller;

import com.projeto.projeto.domain.User;
import com.projeto.projeto.dto.TaskRequestDTO;
import com.projeto.projeto.dto.TaskResponseDTO;
import com.projeto.projeto.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@RequestBody TaskRequestDTO dto,
                                                  @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(dto, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> findAll(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.findAll(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable Long id,
                                                    @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.findById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id,
                                                  @RequestBody TaskRequestDTO dto,
                                                  @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.update(id, dto, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal User currentUser) {
        taskService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}