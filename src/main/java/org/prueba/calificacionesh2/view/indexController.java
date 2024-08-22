package org.prueba.calificacionesh2.view;

import org.prueba.calificacionesh2.dto.UsuarioLoginDTO;
import org.prueba.calificacionesh2.dto.UsuarioRegisterDTO;
import org.prueba.calificacionesh2.entity.Roles;
import org.prueba.calificacionesh2.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class indexController {


    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/main")
    public String index() {
        return "index";
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("UsuarioLoginDTO", new UsuarioLoginDTO());
        return "/auth/login";
    }
    @GetMapping("/form-register")
    public String register(Model model) {
        model.addAttribute("UsuarioRegisterDTO", new UsuarioRegisterDTO());
        model.addAttribute("roles", Roles.values());
        return "/auth/register";
    }
    @PostMapping("/auth/register")
    public String registro(@ModelAttribute("UsuarioRegisterDTO") UsuarioRegisterDTO register){
        usuarioService.registrarUsuario(register);
        return "redirect:/";
    }
    @PostMapping("/auth/login")
    public String login(@ModelAttribute("UsuarioLoginDTO") UsuarioLoginDTO inicio) {
        var usuario = usuarioService.loadUserByUsername(inicio.getUsername());
        if (usuario == null) {
            return "redirect:/";
        }else {
            return "redirect:/main";
        }
    }
}
