package dev.haas.rm.config.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class Encoder {

    private val pwdEncoder = BCryptPasswordEncoder()

    private fun preprocess(pwd: String): String {
        return if (pwd.toByteArray().size > 72) {
            val digest = MessageDigest.getInstance("SHA-256").digest(pwd.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } else {
            pwd
        }
    }

    fun encode(pwd: String): String = pwdEncoder.encode(preprocess(pwd))

    fun isMatch(pwd: String, hashed: String): Boolean = pwdEncoder.matches(preprocess(pwd), hashed)
}
