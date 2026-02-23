package com.tasktracker.service

import com.tasktracker.domain.User
import com.tasktracker.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceTest {

    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        passwordEncoder = mockk()
        userService = UserService(userRepository, passwordEncoder)

        // Setup test user
        testUser = User(
            id = UUID.randomUUID(),
            username = "testuser",
            password = "encoded_password",
            role = "USER"
        )
    }

    @Test
    fun `createUser should create user with encoded password`() {
        // Given
        val username = "newuser"
        val rawPassword = "password123"
        val encodedPassword = "encoded_password123"
        val userSlot = slot<User>()

        every { userRepository.existsByUsername(username) } returns false
        every { passwordEncoder.encode(rawPassword) } returns encodedPassword
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        // When
        val result = userService.createUser(username, rawPassword)

        // Then
        assertNotNull(result)
        assertEquals(username, result.username)
        assertEquals(encodedPassword, result.password)
        assertEquals("USER", result.role)
        verify(exactly = 1) { userRepository.existsByUsername(username) }
        verify(exactly = 1) { passwordEncoder.encode(rawPassword) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `createUser should create user with custom role`() {
        // Given
        val username = "adminuser"
        val rawPassword = "adminpass"
        val encodedPassword = "encoded_adminpass"
        val role = "ADMIN"
        val userSlot = slot<User>()

        every { userRepository.existsByUsername(username) } returns false
        every { passwordEncoder.encode(rawPassword) } returns encodedPassword
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        // When
        val result = userService.createUser(username, rawPassword, role)

        // Then
        assertNotNull(result)
        assertEquals(username, result.username)
        assertEquals(encodedPassword, result.password)
        assertEquals(role, result.role)
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `createUser should throw exception when username already exists`() {
        // Given
        val username = "existinguser"
        val password = "password123"

        every { userRepository.existsByUsername(username) } returns true

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            userService.createUser(username, password)
        }
        assertTrue(exception.message!!.contains("Username already exists"))
        assertTrue(exception.message!!.contains(username))
        verify(exactly = 1) { userRepository.existsByUsername(username) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `createUser should use default USER role when role not specified`() {
        // Given
        val username = "newuser"
        val password = "password123"
        val userSlot = slot<User>()

        every { userRepository.existsByUsername(username) } returns false
        every { passwordEncoder.encode(password) } returns "encoded_password"
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        // When
        val result = userService.createUser(username, password)

        // Then
        assertEquals("USER", result.role)
    }

    @Test
    fun `getUserByUsername should return user when found`() {
        // Given
        val username = "testuser"

        every { userRepository.findByUsername(username) } returns testUser

        // When
        val result = userService.getUserByUsername(username)

        // Then
        assertNotNull(result)
        assertEquals(testUser.id, result?.id)
        assertEquals(testUser.username, result?.username)
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    fun `getUserByUsername should return null when user not found`() {
        // Given
        val username = "nonexistent"

        every { userRepository.findByUsername(username) } returns null

        // When
        val result = userService.getUserByUsername(username)

        // Then
        assertNull(result)
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    fun `getUserById should return user when found`() {
        // Given
        val userId = testUser.id

        every { userRepository.findById(userId) } returns Optional.of(testUser)

        // When
        val result = userService.getUserById(userId)

        // Then
        assertNotNull(result)
        assertEquals(testUser.id, result?.id)
        assertEquals(testUser.username, result?.username)
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `getUserById should return null when user not found`() {
        // Given
        val userId = UUID.randomUUID()

        every { userRepository.findById(userId) } returns Optional.empty()

        // When
        val result = userService.getUserById(userId)

        // Then
        assertNull(result)
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `createUser should not encode password twice`() {
        // Given
        val username = "newuser"
        val rawPassword = "password123"
        val encodedPassword = "encoded_password123"

        every { userRepository.existsByUsername(username) } returns false
        every { passwordEncoder.encode(rawPassword) } returns encodedPassword
        every { userRepository.save(any()) } answers { firstArg() }

        // When
        userService.createUser(username, rawPassword)

        // Then - verify encode is called exactly once
        verify(exactly = 1) { passwordEncoder.encode(rawPassword) }
    }

    @Test
    fun `createUser should handle empty password`() {
        // Given
        val username = "newuser"
        val emptyPassword = ""
        val encodedEmptyPassword = "encoded_empty"

        every { userRepository.existsByUsername(username) } returns false
        every { passwordEncoder.encode(emptyPassword) } returns encodedEmptyPassword
        every { userRepository.save(any()) } answers { firstArg() }

        // When
        val result = userService.createUser(username, emptyPassword)

        // Then
        assertEquals(encodedEmptyPassword, result.password)
        verify(exactly = 1) { passwordEncoder.encode(emptyPassword) }
    }

    @Test
    fun `createUser should generate unique UUID for each user`() {
        // Given
        val username1 = "user1"
        val username2 = "user2"
        val password = "password"
        val userSlot1 = slot<User>()
        val userSlot2 = slot<User>()

        every { userRepository.existsByUsername(any()) } returns false
        every { passwordEncoder.encode(password) } returns "encoded"
        every { userRepository.save(capture(userSlot1)) } answers { userSlot1.captured }

        // When
        val result1 = userService.createUser(username1, password)

        every { userRepository.save(capture(userSlot2)) } answers { userSlot2.captured }
        val result2 = userService.createUser(username2, password)

        // Then
        assertNotEquals(result1.id, result2.id)
    }
}
