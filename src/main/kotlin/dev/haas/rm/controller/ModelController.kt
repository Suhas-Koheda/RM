package dev.haas.rm.controller

import dev.haas.rm.model.services.ModelService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/models")
class ModelController(private val modelService: ModelService) {
    
    @GetMapping
    fun getAvailableModels() = ResponseEntity.ok(modelService.getAvailableModels())
}
