package com.tasktracker.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND) // Returns 404 Not Found when thrown
class ResourceNotFoundException(message: String) : RuntimeException(message)