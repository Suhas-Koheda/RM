package dev.haas.rm.config.security

import dev.haas.rm.model.entity.RefreshToken
import dev.haas.rm.model.entity.User
import dev.haas.rm.repository.RefreshTokenRepository
import dev.haas.rm.repository.UserRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AuthService (
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val hashEncoder: Encoder
){

    fun register(email:String,password:String): User{
        return userRepository.save(
            User(
                userId = null,
                email = email,
                hashedPwd = hashEncoder.encode(password),
            )
        )
    }

    fun login(email:String,password: String):TokenPair{
        val user=userRepository.findByEmail(email)?:
        throw BadCredentialsException("Invalid Creds Man!")

        if(!hashEncoder.isMatch(password,user.hashedPwd)
            && user.userId!=null ){
            throw BadCredentialsException("Invalid Creds Man!")
        }

        val newAxsTkn=jwtService.generateAccessToken(user.userId)
        val newRfsTkn=jwtService.generateRefreshToken(user.userId)
        storeRefreshToken(user.userId,newRfsTkn)
        return TokenPair(
            accessToken = newAxsTkn,
            refreshToken = newRfsTkn
        )
    }

    private fun storeRefreshToken(userId: Long?, rawToken: String){
        val hashed=hashEncoder.encode(rawToken)
        val expriyms=jwtService.refreshTokenValidationTime
        val expiry= Instant.now().plusMillis(expriyms)
        refreshTokenRepository.save(
            RefreshToken(
                userId =userId,
                hashedToken = hashed,
                expiresAt = expiry
            )
        )
    }

    @Transactional
    fun refreshToken(refreshToken: String,): TokenPair{
        if(!jwtService.validateRefreshToken(refreshToken)){
            throw IllegalArgumentException("Invalid refresh token!")
        }
        val userId=jwtService.getUserId(refreshToken)
        val user=userRepository.findById(userId).get()
        refreshTokenRepository.findByUserIdAndHashedToken(
            user.userId,hashEncoder.encode(refreshToken))
            ?:throw IllegalArgumentException("Invalid refresh token")
        refreshTokenRepository.deleteRefreshTokenByUserIdAndHashedToken(
            user.userId,hashEncoder.encode(refreshToken)
        )
        val newAxs=jwtService.generateAccessToken(user.userId)
        val newRef=jwtService.generateRefreshToken(user.userId)
        storeRefreshToken(user.userId,newRef)
        return TokenPair(
            accessToken = newAxs,
            refreshToken = newRef
        )
    }

    fun isMailPresent(email:String):Boolean{
        return userRepository.findByEmail(email)!=null
    }

    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

}