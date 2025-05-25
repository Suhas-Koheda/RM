package dev.haas.rm.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column

@Entity
data class AnalysedResults(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    val match: Double,
    
    @Column(columnDefinition = "TEXT")
    val suggestions: String = "",
    
    val modelUsed: String
)
