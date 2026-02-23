package com.tasktracker.domain

import jakarta.persistence.*
import java.time.Instant
import java.util.*


@Entity
@Table(name = "tasks") // Explicit table name
data class Task(
    @Id
    val id: UUID = UUID.randomUUID(), // Automatically generate UUID
    var title: String,
    var description: String?, // Nullable description
    @Enumerated(EnumType.STRING) // Store enum as String in DB
    var status: TaskStatus = TaskStatus.TODO, // Default status
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(), // Automatically set creation timestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(), // Automatically set update timestamp

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null // Each task belongs to a user
) {
    // Override equals and hashCode to use 'id' for comparison,
    // which is important for JPA entities and collections
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
