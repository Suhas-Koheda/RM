package dev.haas.rm.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(private val jwtAuthFilter: JwtAuthFilter) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
            http
            .cors { cors ->
                cors.configurationSource {
                    val config = org.springframework.web.cors.CorsConfiguration()
                    config.allowedOrigins = listOf("*")
                    config.allowedMethods = listOf("*")
                    config.allowedHeaders = listOf("*")
                    config
                }
            }
                .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .authorizeHttpRequests {
                    it.requestMatchers("/auth/**","/").permitAll()
                    it.anyRequest().authenticated()
                }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

}