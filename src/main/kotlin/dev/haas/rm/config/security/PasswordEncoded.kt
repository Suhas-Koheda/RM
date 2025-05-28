package dev.haas.rm.config.security

import com.google.firebase.auth.hash.Bcrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class Encoder{
    private val pwdEncoder= BCryptPasswordEncoder()
}