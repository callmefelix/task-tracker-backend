package com.tasktracker.service

import com.tasktracker.domain.Task
import com.tasktracker.dto.CreateTaskRequest
import com.tasktracker.dto.TaskFilterRequest
import com.tasktracker.dto.UpdateTaskRequest
import com.tasktracker.exception.ResourceNotFoundException
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import com.tasktracker.repository.TaskRepository
import java.time.Instant
import java.util.*

@Service
class TaskService(
    private val taskRespository: TaskRepository,
    private val userService: UserService
) {

    @Transactional
    fun createTask(request: CreateTaskRequest, username: String): Task {
        val user = userService.getUserByUsername(username)
            ?: throw ResourceNotFoundException("User not found: $username")

        val task = Task(
            title = request.title,
            description = request.description,
            user = user
        )
        return taskRespository.save(task)
    }

    @Transactional(readOnly = true)
    fun getAllTasks(filter: TaskFilterRequest, username: String): List<Task> {
        val user = userService.getUserByUsername(username)
            ?: throw ResourceNotFoundException("User not found: $username")

        return when {
            filter.status != null -> taskRespository.findByUserIdAndStatus(user.id, filter.status)
            !filter.search.isNullOrBlank() -> {
                taskRespository.findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndDescriptionContainingIgnoreCase(
                    user.id, filter.search, user.id, filter.search
                )
            }
            else -> taskRespository.findByUserId(user.id)
        }
    }

    @Transactional(readOnly = true)
    fun getTaskById(id: UUID, username: String): Task {
        val task = taskRespository.findById(id)
            .orElseThrow { ResourceNotFoundException("Task with ID $id is not found") }

        // Verify that the task belongs to the current user
        if (task.user?.username != username) {
            throw ResourceNotFoundException("Task with ID $id not found") // Don't reveal it exists
        }

        return task
    }

    @Transactional
    fun updateTask(id: UUID, request: UpdateTaskRequest, username: String): Task {
        val existingTask = getTaskById(id, username)

        existingTask.apply {
            title = request.title ?: title
            description = request.description ?: description
            status = request.status ?: status
            updatedAt = Instant.now()
        }
        return taskRespository.save(existingTask)
    }

    @Transactional
    fun deteleTask(id: UUID, username: String) {
        val task = getTaskById(id, username) // This checks ownership
        taskRespository.deleteById(task.id)
    }

    @Transactional
    fun deleteAllTask(ids: List<UUID>, username: String) {
        ids.forEach { id -> deteleTask(id, username) }
    }
}