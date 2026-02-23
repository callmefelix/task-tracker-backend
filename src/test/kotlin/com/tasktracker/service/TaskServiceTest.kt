package com.tasktracker.service

import com.tasktracker.domain.Task
import com.tasktracker.domain.TaskStatus
import com.tasktracker.domain.User
import com.tasktracker.dto.CreateTaskRequest
import com.tasktracker.dto.TaskFilterRequest
import com.tasktracker.dto.UpdateTaskRequest
import com.tasktracker.exception.ResourceNotFoundException
import com.tasktracker.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.*

class TaskServiceTest {

    private lateinit var taskService: TaskService
    private lateinit var taskRepository: TaskRepository
    private lateinit var userService: UserService

    private lateinit var testUser: User
    private lateinit var testTask: Task

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        userService = mockk()
        taskService = TaskService(taskRepository, userService)

        // Setup test user
        testUser = User(
            id = UUID.randomUUID(),
            username = "testuser",
            password = "encoded_password",
            role = "USER"
        )

        // Setup test task
        testTask = Task(
            id = UUID.randomUUID(),
            title = "Test Task",
            description = "Test Description",
            status = TaskStatus.TODO,
            user = testUser
        )
    }

    @Test
    fun `createTask should create task for authenticated user`() {
        // Given
        val request = CreateTaskRequest(
            title = "New Task",
            description = "New Description"
        )
        val username = "testuser"

        every { userService.getUserByUsername(username) } returns testUser
        every { taskRepository.save(any()) } returns testTask

        // When
        val result = taskService.createTask(request, username)

        // Then
        assertNotNull(result)
        assertEquals("Test Task", result.title)
        verify(exactly = 1) { userService.getUserByUsername(username) }
        verify(exactly = 1) { taskRepository.save(any()) }
    }

    @Test
    fun `createTask should throw exception when user not found`() {
        // Given
        val request = CreateTaskRequest(
            title = "New Task",
            description = "New Description"
        )
        val username = "nonexistent"

        every { userService.getUserByUsername(username) } returns null

        // When & Then
        val exception = assertThrows<ResourceNotFoundException> {
            taskService.createTask(request, username)
        }
        assertTrue(exception.message!!.contains("User not found"))
    }

    @Test
    fun `getAllTasks should return tasks for authenticated user`() {
        // Given
        val username = "testuser"
        val filter = TaskFilterRequest(status = null, search = null)
        val tasks = listOf(testTask)

        every { userService.getUserByUsername(username) } returns testUser
        every { taskRepository.findByUserId(testUser.id) } returns tasks

        // When
        val result = taskService.getAllTasks(filter, username)

        // Then
        assertEquals(1, result.size)
        assertEquals("Test Task", result[0].title)
        verify(exactly = 1) { taskRepository.findByUserId(testUser.id) }
    }

    @Test
    fun `getAllTasks with status filter should call correct repository method`() {
        // Given
        val username = "testuser"
        val filter = TaskFilterRequest(status = TaskStatus.TODO, search = null)
        val tasks = listOf(testTask)

        every { userService.getUserByUsername(username) } returns testUser
        every { taskRepository.findByUserIdAndStatus(testUser.id, TaskStatus.TODO) } returns tasks

        // When
        val result = taskService.getAllTasks(filter, username)

        // Then
        assertEquals(1, result.size)
        verify(exactly = 1) { taskRepository.findByUserIdAndStatus(testUser.id, TaskStatus.TODO) }
    }

    @Test
    fun `getAllTasks with search filter should call correct repository method`() {
        // Given
        val username = "testuser"
        val filter = TaskFilterRequest(status = null, search = "test")
        val tasks = listOf(testTask)

        every { userService.getUserByUsername(username) } returns testUser
        every {
            taskRepository.findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndDescriptionContainingIgnoreCase(
                testUser.id, "test", testUser.id, "test"
            )
        } returns tasks

        // When
        val result = taskService.getAllTasks(filter, username)

        // Then
        assertEquals(1, result.size)
        verify(exactly = 1) {
            taskRepository.findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndDescriptionContainingIgnoreCase(
                testUser.id, "test", testUser.id, "test"
            )
        }
    }

    @Test
    fun `getTaskById should return task when user owns it`() {
        // Given
        val taskId = testTask.id
        val username = "testuser"

        every { taskRepository.findById(taskId) } returns Optional.of(testTask)

        // When
        val result = taskService.getTaskById(taskId, username)

        // Then
        assertNotNull(result)
        assertEquals(testTask.id, result.id)
        assertEquals(testTask.title, result.title)
    }

    @Test
    fun `getTaskById should throw exception when task not found`() {
        // Given
        val taskId = UUID.randomUUID()
        val username = "testuser"

        every { taskRepository.findById(taskId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            taskService.getTaskById(taskId, username)
        }
    }

    @Test
    fun `getTaskById should throw exception when user does not own task`() {
        // Given
        val taskId = testTask.id
        val username = "otheruser"

        every { taskRepository.findById(taskId) } returns Optional.of(testTask)

        // When & Then
        assertThrows<ResourceNotFoundException> {
            taskService.getTaskById(taskId, username)
        }
    }

    @Test
    fun `updateTask should update task successfully`() {
        // Given
        val taskId = testTask.id
        val username = "testuser"
        val updateRequest = UpdateTaskRequest(
            title = "Updated Title",
            description = "Updated Description",
            status = TaskStatus.IN_PROGRESS
        )

        every { taskRepository.findById(taskId) } returns Optional.of(testTask)
        every { taskRepository.save(any()) } returns testTask.apply {
            title = "Updated Title"
            description = "Updated Description"
            status = TaskStatus.IN_PROGRESS
        }

        // When
        val result = taskService.updateTask(taskId, updateRequest, username)

        // Then
        assertEquals("Updated Title", result.title)
        assertEquals("Updated Description", result.description)
        assertEquals(TaskStatus.IN_PROGRESS, result.status)
        verify(exactly = 1) { taskRepository.save(any()) }
    }

    @Test
    fun `updateTask should only update provided fields`() {
        // Given
        val taskId = testTask.id
        val username = "testuser"
        val updateRequest = UpdateTaskRequest(
            title = "Updated Title",
            description = null,
            status = null
        )

        every { taskRepository.findById(taskId) } returns Optional.of(testTask)
        every { taskRepository.save(any()) } returns testTask

        // When
        val result = taskService.updateTask(taskId, updateRequest, username)

        // Then
        verify(exactly = 1) { taskRepository.save(any()) }
    }

    @Test
    fun `deleteTask should delete task when user owns it`() {
        // Given
        val taskId = testTask.id
        val username = "testuser"

        every { taskRepository.findById(taskId) } returns Optional.of(testTask)
        every { taskRepository.deleteById(taskId) } returns Unit

        // When
        taskService.deteleTask(taskId, username)

        // Then
        verify(exactly = 1) { taskRepository.deleteById(taskId) }
    }

    @Test
    fun `deleteTask should throw exception when task not found`() {
        // Given
        val taskId = UUID.randomUUID()
        val username = "testuser"

        every { taskRepository.findById(taskId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            taskService.deteleTask(taskId, username)
        }
    }

    @Test
    fun `deleteTask should throw exception when user does not own task`() {
        // Given
        val taskId = testTask.id
        val username = "otheruser"

        every { taskRepository.findById(taskId) } returns Optional.of(testTask)

        // When & Then
        assertThrows<ResourceNotFoundException> {
            taskService.deteleTask(taskId, username)
        }
    }
}
