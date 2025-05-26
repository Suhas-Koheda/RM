package dev.haas.rm.repository

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import dev.haas.rm.model.entity.NeonModel

@Repository
interface NeonRepository : JpaRepository<NeonModel, Long> {

}
