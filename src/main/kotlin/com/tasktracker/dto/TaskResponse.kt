package com.tasktracker.dto

import com.tasktracker.domain.Task
import com.tasktracker.domain.TaskStatus
import java.time.Instant
import java.util.UUID

data class TaskResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        // Extension function to convert a Task entity to a TaskResponse DTO
        fun Task.toResponse() = TaskResponse(
            id = this.id,
            title = this.title,
            description = this.description,
            status = this.status,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
