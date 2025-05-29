package dev.haas.rm.config.security

import dev.haas.rm.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.stereotype.Component

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter(){
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substringAfter("Bearer ").trim()
            if (jwtService.validateAccessToken(token)) {
                val userId = jwtService.getUserId(token)
                val auth = UsernamePasswordAuthenticationToken(userId, null,listOf(SimpleGrantedAuthority("USER")))
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }
}