package dev.haas.rm.model.entity

import dev.langchain4j.service.UserName
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "UserData")
data class User (
    @Id val userId: Long,
    val email: String,
    val hashedPwd: String
)
