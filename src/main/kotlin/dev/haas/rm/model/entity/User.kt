package dev.haas.rm.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "UserData")
data class User (
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val userId: Long?,
    val email: String,
    @Column(columnDefinition = "TEXT", nullable = false)
    val hashedPwd: String
)
