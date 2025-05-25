package dev.haas.rm.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.view.RedirectView

@Controller
class WebController {

    @GetMapping("/")
    fun home(): RedirectView {
        return RedirectView("/frontend/index.html")
    }
}
