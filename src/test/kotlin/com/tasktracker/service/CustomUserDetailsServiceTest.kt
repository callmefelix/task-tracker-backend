package com.tasktracker.service

import com.tasktracker.domain.User
import com.tasktracker.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

class CustomUserDetailsServiceTest {

    private lateinit var customUserDetailsService: CustomUserDetailsService
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: User
    private lateinit var adminUser: User

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        customUserDetailsService = CustomUserDetailsService(userRepository)

        // Setup test users
        testUser = User(
            id = UUID.randomUUID(),
            username = "testuser",
            password = "encoded_password",
            role = "USER"
        )

        adminUser = User(
            id = UUID.randomUUID(),
            username = "adminuser",
            password = "encoded_admin_password",
            role = "ADMIN"
        )
    }

    @Test
    fun `loadUserByUsername should return UserDetails when user exists`() {
        // Given
        val username = "testuser"

        every { userRepository.findByUsername(username) } returns testUser

        // When
        val result = customUserDetailsService.loadUserByUsername(username)

        // Then
        assertNotNull(result)
        assertEquals(testUser.username, result.username)
        assertEquals(testUser.password, result.password)
        assertTrue(result.authorities.contains(SimpleGrantedAuthority("ROLE_USER")))
        assertEquals(1, result.authorities.size)
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    fun `loadUserByUsername should throw exception when user not found`() {
        // Given
        val username = "nonexistent"

        every { userRepository.findByUsername(username) } returns null

        // When & Then
        val exception = assertThrows<UsernameNotFoundException> {
            customUserDetailsService.loadUserByUsername(username)
        }
        assertTrue(exception.message!!.contains("User not found"))
        assertTrue(exception.message!!.contains(username))
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    fun `loadUserByUsername should return UserDetails with ADMIN role`() {
        // Given
        val username = "adminuser"

        every { userRepository.findByUsername(username) } returns adminUser

        // When
        val result = customUserDetailsService.loadUserByUsername(username)

        // Then
        assertNotNull(result)
        assertEquals(adminUser.username, result.username)
        assertTrue(result.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")))
        assertEquals(1, result.authorities.size)
    }

    @Test
    fun `loadUserByUsername should prepend ROLE_ to authority`() {
        // Given
        val username = "testuser"

        every { userRepository.findByUsername(username) } returns testUser

        // When
        val result = customUserDetailsService.loadUserByUsername(username)

        // Then
        val authority = result.authorities.first()
        assertEquals("ROLE_USER", authority.authority)
        assertFalse(authority.authority == "USER") // Should have ROLE_ prefix
    }

    @Test
    fun `loadUserByUsername should handle user with custom role`() {
        // Given
        val customUser = User(
            id = UUID.randomUUID(),
            username = "customuser",
            password = "encoded_password",
            role = "MANAGER"
        )

        every { userRepository.findByUsername(customUser.username) } returns customUser

        // When
        val result = customUserDetailsService.loadUserByUsername(customUser.username)

        // Then
        assertTrue(result.authorities.contains(SimpleGrantedAuthority("ROLE_MANAGER")))
    }

    @Test
    fun `loadUserByUsername should return UserDetails with enabled account`() {
        // Given
        val username = "testuser"

        every { userRepository.findByUsername(username) } returns testUser

        // When
        val result = customUserDetailsService.loadUserByUsername(username)

        // Then
        assertTrue(result.isEnabled)
        assertTrue(result.isAccountNonExpired)
        assertTrue(result.isAccountNonLocked)
        assertTrue(result.isCredentialsNonExpired)
    }

    @Test
    fun `loadUserByUsername should preserve encoded password`() {
        // Given
        val username = "testuser"
        val encodedPassword = "bcrypt_encoded_very_long_password_hash_12345"
        val userWithEncodedPassword = testUser.apply { password = encodedPassword }

        every { userRepository.findByUsername(username) } returns userWithEncodedPassword

        // When
        val result = customUserDetailsService.loadUserByUsername(username)

        // Then
        assertEquals(encodedPassword, result.password)
    }

    @Test
    fun `loadUserByUsername should handle empty username gracefully`() {
        // Given
        val emptyUsername = ""

        every { userRepository.findByUsername(emptyUsername) } returns null

        // When & Then
        assertThrows<UsernameNotFoundException> {
            customUserDetailsService.loadUserByUsername(emptyUsername)
        }
    }
}
