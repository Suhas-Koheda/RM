package dev.haas.rm.model.entity

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
    
    val match: Double=0.0,
    
    @Column(columnDefinition = "TEXT")
    val suggestions: String = "",

    @Column(columnDefinition = "TEXT")
    val jD:String="",

    val modelUsed: String="",

    @Column(columnDefinition = "TEXT")
    val resume:String="",
)
