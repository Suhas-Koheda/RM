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
    val suggestion1: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion2: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion3: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion4: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion5: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion6: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion7: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion8: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion9: String = "",
    
    @Column(columnDefinition = "TEXT")
    val suggestion10: String = "",
    
    val modelUsed: String
)
