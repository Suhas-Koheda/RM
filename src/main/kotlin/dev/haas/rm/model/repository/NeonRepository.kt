package dev.haas.rm.model.repository

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import dev.haas.rm.model.NeonModel

@Repository
interface NeonRepository : JpaRepository<NeonModel, Long> {

}
