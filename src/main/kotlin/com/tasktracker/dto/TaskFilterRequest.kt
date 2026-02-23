package com.tasktracker.dto

import com.tasktracker.domain.TaskStatus

data class TaskFilterRequest(
    val status: TaskStatus? = null,
    val search: String? = null // For searching by title/description
)
