package com.tasktracker.repository

import com.tasktracker.domain.Task
import com.tasktracker.domain.TaskStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskRepository : JpaRepository<Task, UUID> {
    // Find all tasks for a specific user
    fun findByUserId(userId: UUID): List<Task>

    // Find tasks by user and status
    fun findByUserIdAndStatus(userId: UUID, status: TaskStatus): List<Task>

    // Find tasks by user and search in title/description
    fun findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndDescriptionContainingIgnoreCase(
        userId1: UUID,
        title: String,
        userId2: UUID,
        description: String
    ): List<Task>
}