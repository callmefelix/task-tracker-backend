package com.tasktracker.controller

import com.tasktracker.dto.LoginRequest
import com.tasktracker.dto.RegisterRequest
import com.tasktracker.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val jwtEncoder: JwtEncoder,
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService
    ) {

        @PostMapping("/register")
        fun register(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<Map<String, String>> {
            return try {
                userService.createUser(registerRequest.username, registerRequest.password)
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapOf("message" to "User registered successfully"))
            } catch (e: IllegalArgumentException) {
                ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to (e.message ?: "Registration failed")))
            } catch (e: Exception) {
                println("Registration error: ${e.message}")
                ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapOf("error" to "An error occurred during registration"))
            }
        }

        @PostMapping("/token")
        fun token(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
            try {
                val authentication: Authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
                )

                val now = Instant.now()
                val scope: String = authentication.authorities.stream()
                    .map { obj: GrantedAuthority -> obj.authority }
                    .collect(Collectors.joining(" "))

                val claims: JwtClaimsSet = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plus(1, ChronoUnit.HOURS)) // Token valid for 1 hour
                    .subject(authentication.name)
                    .claim("scope", scope) // Add roles as 'scope' claim
                    .build()

                val jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
                return ResponseEntity.ok(mapOf("access_token" to jwtValue))

            } catch (e: Exception) {
                // Log the exception for debugging
                println("Authentication failed: ${e.message}")
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Invalid username or password"))
            }
        }
    }