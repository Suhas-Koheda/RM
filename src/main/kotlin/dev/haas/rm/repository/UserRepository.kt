package dev.haas.rm.repository

import dev.haas.rm.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(emailId:String): Long?
}