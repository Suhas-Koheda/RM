package dev.haas.rm.model

import org.springframework.context.annotation.Bean

data class AnalysedResults(val match: Double,val suggestions:String,val modelUsed: String)