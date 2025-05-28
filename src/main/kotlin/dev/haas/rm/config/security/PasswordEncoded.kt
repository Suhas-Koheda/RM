package dev.haas.rm.config.security

import com.google.firebase.auth.hash.Bcrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class Encoder {

    private val pwdEncoder = BCryptPasswordEncoder()

    fun encode(pwd:String):String=pwdEncoder.encode(pwd)

    fun isMatch(pwd:String,hashed:String): Boolean=pwdEncoder.matches(pwd,hashed)
}