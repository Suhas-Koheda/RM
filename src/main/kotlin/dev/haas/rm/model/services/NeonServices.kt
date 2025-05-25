package dev.haas.rm.model.services

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import dev.haas.rm.model.repository.NeonRepository

@Service
class NeonServices @Autowired constructor(
    private val neonRepository: NeonRepository
) {
    fun saveNeonModel(neonModel: dev.haas.rm.model.NeonModel): dev.haas.rm.model.NeonModel {
        return neonRepository.save(neonModel)
    }

    fun getNeonModelById(id: Long): dev.haas.rm.model.NeonModel? {
        return neonRepository.findById(id).orElse(null)
    }

    fun getAllNeonModels(): List<dev.haas.rm.model.NeonModel> {
        return neonRepository.findAll()
    }

    fun deleteNeonModel(id: Long) {
        neonRepository.deleteById(id)
    }

}
