package dev.haas.rm.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import dev.haas.rm.repository.NeonRepository
import dev.haas.rm.model.entity.NeonModel

@Service
class NeonServices @Autowired constructor(
    private val neonRepository: NeonRepository
) {
    fun saveNeonModel(neonModel: NeonModel): NeonModel {
        return neonRepository.save(neonModel)
    }

    fun getNeonModelById(id: Long): NeonModel? {
        return neonRepository.findById(id).orElse(null)
    }

    fun getAllNeonModels(): List<NeonModel> {
        return neonRepository.findAll()
    }

    fun deleteNeonModel(id: Long) {
        neonRepository.deleteById(id)
    }

}
