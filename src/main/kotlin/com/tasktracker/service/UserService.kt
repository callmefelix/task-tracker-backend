package com.tasktracker.service

import com.tasktracker.domain.User
import com.tasktracker.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(username: String, password: String, role: String = "USER"): User {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists: $username")
        }

        val user = User(
            username = username,
            password = passwordEncoder.encode(password),
            role = role
        )
        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: UUID): User? {
        return userRepository.findById(id).orElse(null)
    }
}
