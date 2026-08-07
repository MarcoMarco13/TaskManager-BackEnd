package com.projeto.projeto.dto;

import java.time.LocalDateTime;

public record TaskResponseDTO(Long id, String title, String description, boolean completed, LocalDateTime createdAt) {}