package com.tasktracker.controller

import com.tasktracker.dto.CreateTaskRequest
import com.tasktracker.dto.TaskFilterRequest
import com.tasktracker.dto.TaskResponse
import com.tasktracker.dto.TaskResponse.Companion.toResponse
import com.tasktracker.dto.UpdateTaskRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import com.tasktracker.service.TaskService
import java.util.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun createTask(
        @Valid @RequestBody request: CreateTaskRequest,
        principal: java.security.Principal
    ): ResponseEntity<TaskResponse> {
        val createdTask = taskService.createTask(request, principal.name)
        return ResponseEntity(createdTask.toResponse(), HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllTasks(
        filter: TaskFilterRequest,
        principal: java.security.Principal
    ): ResponseEntity<List<TaskResponse>> {
        val allTasks = taskService.getAllTasks(filter, principal.name).map { task -> task.toResponse() }
        return ResponseEntity(allTasks, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getTaskById(
        @PathVariable id: UUID,
        principal: java.security.Principal
    ): ResponseEntity<TaskResponse> {
        val task = taskService.getTaskById(id, principal.name)
        return ResponseEntity(task.toResponse(), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateTask(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTaskRequest,
        principal: java.security.Principal
    ): ResponseEntity<TaskResponse> {
        val updatedTask = taskService.updateTask(id, request, principal.name)
        return ResponseEntity(updatedTask.toResponse(), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deteleTask(
        @PathVariable id: UUID,
        principal: java.security.Principal
    ): ResponseEntity<Void> {
        taskService.deteleTask(id, principal.name)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}