package dev.haas.rm.repository

import dev.haas.rm.model.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long>{
    fun findByUserIdAndHashedToken(userId: Long?, hashedToken: String): RefreshToken?
    fun deleteRefreshTokenByUserIdAndHashedToken(userId: Long?, hashedToken: String)
}