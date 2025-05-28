package dev.haas.rm.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "UserData")
data class User (
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val userId: Long?,
    val email: String,
    val hashedPwd: String
)
