package dev.haas.rm.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant

@Entity
data class RefreshToken (
    @Id
    val userId: Long?,
    @Column(columnDefinition = "TEXT", nullable = false)
    val hashedToken: String,
    val expiresAt: Instant,
    val createdAt: Instant =Instant.now()
)