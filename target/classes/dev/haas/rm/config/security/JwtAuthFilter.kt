package dev.haas.rm.config.security

import io.jsonwebtoken.Jwt
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import java.net.http.HttpHeaders

class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter(){
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader=request.getHeaders("Authorization")
        if(authHeader!=null && authHeader.nextElement().startsWith("Bearer")){
            if(jwtService.validateAccessToken(authHeader.nextElement())){
                val userId=jwtService.getUserId(authHeader.nextElement())
                val auth= UsernamePasswordAuthenticationToken(userId,null)
                SecurityContextHolder.getContext().authentication=auth
            }
        }
        filterChain.doFilter(request,response)
    }
}