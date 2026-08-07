package com.projeto.projeto.service;

import com.projeto.projeto.domain.Task;
import com.projeto.projeto.domain.User;
import com.projeto.projeto.dto.TaskRequestDTO;
import com.projeto.projeto.dto.TaskResponseDTO;
import com.projeto.projeto.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponseDTO create(TaskRequestDTO dto, User currentUser) {
        Task task = new Task(dto.title(), dto.description(), currentUser);
        Task saved = taskRepository.save(task);
        return mapToDTO(saved);
    }

    public List<TaskResponseDTO> findAll(User currentUser) {
        return taskRepository.findByUser(currentUser)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public TaskResponseDTO findById(Long id, User currentUser) {
        Task task = taskRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado ou acesso não permitido"));
        return mapToDTO(task);
    }

    public TaskResponseDTO update(Long id, TaskRequestDTO dto, User currentUser) {
        Task task = taskRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado ou acesso não permitido"));

        if (dto.title() != null) task.setTitle(dto.title());
        if (dto.description() != null) task.setDescription(dto.description());
        if (dto.completed() != null) task.setCompleted(dto.completed());

        Task updated = taskRepository.save(task);
        return mapToDTO(updated);
    }

    public void delete(Long id, User currentUser) {
        Task task = taskRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado ou acesso não permitido"));
        taskRepository.delete(task);
    }

    private TaskResponseDTO mapToDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt()
        );
    }
}