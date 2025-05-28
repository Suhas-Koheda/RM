package dev.haas.rm.controller

import dev.haas.rm.config.security.AuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController (private val authService: AuthService){

    @PostMapping("/login")
    fun login(
        @RequestBody authReq: AuthReq
    ): AuthService.TokenPair{
        return authService.login(authReq.userId,authReq.pwd)
    }

    @PostMapping("/register")
    fun register(
        @RequestBody authReq: AuthReq
    ): String {
        authService.register(authReq.userId,authReq.pwd)
        return "Registration successful"
    }

    @GetMapping("/email")
    fun validateMail(@RequestBody email:String): Boolean {
        return authService.isMailPresent(email)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody refToken: String
    ): AuthService.TokenPair{
        return authService.refreshToken(refToken)
    }

    data class AuthReq(val userId:String,val pwd:String)

}
