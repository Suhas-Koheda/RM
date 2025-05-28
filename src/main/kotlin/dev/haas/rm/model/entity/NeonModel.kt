package dev.haas.rm.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.OneToOne
import jakarta.persistence.CascadeType

@Entity
data class NeonModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val resume: String,
    
    @Column(nullable = false)
    val userID: Long,
    
    @OneToOne(cascade = [CascadeType.ALL])
    val analysedResults: AnalysedResults
)
