package dev.haas.rm.model.repository

import dev.haas.rm.model.AnalysedResults
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalysedResultsRepository : JpaRepository<AnalysedResults, Long>
