package dev.haas.rm.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService{

    private val secretKey="sefygiwsefwsekghseasfialsgaeufgalkeflaieufgalie"
    private val secretKeyBytes = Keys.hmacShaKeyFor(secretKey.toByteArray())

     val accessTokenValidationTime=15L*60L*1000L
    val refreshTokenValidationTime=30L*24*60*60*1000L

    private fun generateToken(
        userId: Long?,
        type: String,
        expiry: Long
    ):String{
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("type", type)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiry))
            .signWith(secretKeyBytes, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateAccessToken(userId: Long?): String {
        return generateToken(userId, "access", accessTokenValidationTime)
    }

    fun generateRefreshToken(userId: Long?): String {
        return generateToken(userId, "refresh", refreshTokenValidationTime)
    }

    fun validateAccessToken(token: String): Boolean {
            val claims=parseClaims(token)
            val tokenType= claims?.get("type") as? String?:return false
        return tokenType=="access"
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims=parseClaims(token)
        val tokenType= claims?.get("type") as? String?:return false
        return tokenType=="refresh"
    }

    fun getUserId(token:String):Long{
        val rawToken=if(token.startsWith("Bearer")){
            token.substring(7)
        }else{
            token
        }
        val claims=parseClaims(rawToken)?:throw IllegalArgumentException("Invalid JWT token")
        return claims.subject.toLong()
    }

    fun parseClaims(token: String): Claims? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKeyBytes)
                .build()
                .parseClaimsJws(token)
                .body
            claims
        } catch (_: Exception)  {
            throw IllegalArgumentException("Invalid JWT token")
        }
    }
}

