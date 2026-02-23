package com.tasktracker.dto

import com.tasktracker.domain.TaskStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateTaskRequest(
    @field:Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    val title: String? = null,
    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,
    val status: TaskStatus? = null // Status update is optional
)
