package org.prueba.calificacionesh2.view;

import org.prueba.calificacionesh2.dto.ProfesorDTO;
import org.prueba.calificacionesh2.exception.ProfesorNotFoundException;
import org.prueba.calificacionesh2.service.CalificacionesService;
import org.prueba.calificacionesh2.service.ProfesorService;
import org.prueba.calificacionesh2.excelManagement.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProfesorControllerAdmin {
    @Autowired
    private ProfesorService profesorService;
    @Autowired
    private CalificacionesService calificacionesService;
    @Autowired
    private UploadFileService uploadFileService;

    @GetMapping("/profesor/profesorMain")
    public String viewAdminView(Model model){
        model.addAttribute("profesores", profesorService.getAllProfesores());
        return "profesor/profesorMain";
    }

    @GetMapping("/profesor/saveProfesor")
    public String saveProfesor(Model model){
        model.addAttribute("ProfesorDTO", new ProfesorDTO());
        return "profesor/saveProfesor";
    }

    @PostMapping("/profesor/saveProfesor")
    public String saveProfesor(Model model,@ModelAttribute("ProfesorDTO") ProfesorDTO profesor){
        model.addAttribute("ProfesorDTO", new ProfesorDTO());
        profesorService.addProfesor(profesor);
        return "redirect:/profesor/profesorMain";
    }

    @GetMapping("/profesor/updateProfesor/{id}")
    public String updateProfesorForm(Model model, @PathVariable Integer id){
        try {
            model.addAttribute("ProfesorDTO", profesorService.getProfesorById(id));
        }catch (ProfesorNotFoundException e){
            System.err.println("No se ha encontrado el profesor");
        }
        return "/profesor/updateProfesor";
    }

    @PostMapping("/profesor/updateProfesor/{id}")
    public String updateProfesor(Model model,@ModelAttribute("ProfesorDTO") ProfesorDTO profesor, @PathVariable Integer id){
        try {
            var res = profesorService.updateProfesor(id,profesor);
            model.addAttribute("msg",res.toString());
        }catch (ProfesorNotFoundException e){
            System.err.println("No se ha encontrado el profesor");
        }
        return "redirect:/profesor/profesorMain";
    }

    @GetMapping("profesor/deleteProfesor/{id}")
    public String deleteProfesor(@PathVariable("id") Integer id){
        try {
            profesorService.deleteProfesor(id);
        }catch (ProfesorNotFoundException e){
            System.err.println("No se ha encontrado el profesor");
        }
        return "redirect:/profesor/profesorMain";
    }

    @GetMapping("profesor/calificaciones/{id}")
    public String getCalificacionByProfesor(Model model, @PathVariable Integer id){
        model.addAttribute("calificaciones", calificacionesService.getCalificacionesEstudiantesAndAsignaturasByIdProfesor(id));
        return "profesor/profesorCalifications";
    }

    @PostMapping("/profesor/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                uploadFileService.uploadProfesor(file);
            }catch (Exception e) {
                System.err.println("No se ha podido subir el profesor");
            }
        }
        return "redirect:/profesor/profesorMain";
    }
}
