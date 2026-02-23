package com.tasktracker.config

import com.tasktracker.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitializer {

    @Bean
    fun initializeData(userService: UserService): CommandLineRunner {
        return CommandLineRunner {
            // Create default users if they don't exist
            try {
                if (userService.getUserByUsername("user") == null) {
                    userService.createUser("user", "password", "USER")
                    println("Created default user: user/password")
                }
            } catch (e: Exception) {
                println("User 'user' already exists or error creating: ${e.message}")
            }

            try {
                if (userService.getUserByUsername("admin") == null) {
                    userService.createUser("admin", "admin", "ADMIN")
                    println("Created default admin: admin/admin")
                }
            } catch (e: Exception) {
                println("User 'admin' already exists or error creating: ${e.message}")
            }
        }
    }
}
