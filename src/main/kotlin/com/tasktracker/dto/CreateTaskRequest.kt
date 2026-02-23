package com.tasktracker.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTaskRequest(
    @field:NotBlank(message = "Title cannot be blank")
    @field:Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    val title: String,
    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null // Description is optional
)
