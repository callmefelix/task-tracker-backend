package com.tasktracker.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // Apply to all endpoints
            .allowedOrigins( "http://localhost",
                "http://localhost:80",
                "http://localhost:5173",
                "http://89.167.111.171") // Allow your Vite frontend's origin
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow necessary HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(true) // Important for cookies/authentication headers
            .maxAge(3600) // Cache CORS preflight request for 1 hour
    }
}