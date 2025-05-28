package dev.haas.rm.config.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService{

    private val secretKey="sefygiwsefwsekghse"
    private val secretKeyBytes = Keys.hmacShaKeyFor(secretKey.toByteArray())

    private val accessTokenValidationTime=15L*60L*1000L
    private val refreshTokenValidationTime=30L*24*60*60*1000L

    private fun generateToken(
        userId:Long,
        type:String,
        expiry:Long
    ):String{
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("type", type)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiry))
            .signWith(secretKeyBytes, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateAccessToken(userId: Long): String {
        return generateToken(userId, "access", accessTokenValidationTime)
    }

    fun generateRefreshToken(userId: Long): String {
        return generateToken(userId, "refresh", refreshTokenValidationTime)
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKeyBytes)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun extractUserId(token: String): Long? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKeyBytes)
                .build()
                .parseClaimsJws(token)
                .body

            claims.subject.toLong()
        } catch (e: Exception) {
            null
        }
    }
}

