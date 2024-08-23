package org.prueba.calificacionesh2.controller;

import org.prueba.calificacionesh2.business.dto.AsignaturaDTO;
import org.prueba.calificacionesh2.business.exception.AsignaturaNotFoundException;
import org.prueba.calificacionesh2.business.exception.ProfesorNotFoundException;
import org.prueba.calificacionesh2.business.service.AsignaturasService;
import org.prueba.calificacionesh2.business.service.ProfesorService;
import org.prueba.calificacionesh2.business.excelManagement.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AsignatuaControllerAdmin {

    @Autowired
    private AsignaturasService asignaturasService;
    @Autowired
    private ProfesorService profesorService;
    @Autowired
    private UploadFileService uploadFileService;

    @GetMapping("/asignatura/asignaturaMain")
    public String viewAdminView(Model model){
        model.addAttribute("asignaturas", asignaturasService.getAllAsignaturas());
        return "/asignatura/asignaturaMain";
    }

    @GetMapping("/asignatura/saveAsignatura")
    public String saveAsignatura(Model model){
        model.addAttribute("AsignaturaDTO", new AsignaturaDTO());
        model.addAttribute("profesores", profesorService.getAllProfesores());
        return "/asignatura/saveAsignatura";
    }

    @PostMapping("/asignatura/saveAsignatura")
    public String saveAsignatua(@ModelAttribute("AsignaturaDTO") AsignaturaDTO asignatura){
        try {
            asignaturasService.addAsignatura(asignatura);
        }catch (ProfesorNotFoundException e){
            System.err.println("No se ha encontrado el profesor");
        }

        return "redirect:/asignatura/asignaturaMain";
    }
    @GetMapping("/asignatura/updateAsignatura/{id}")
    public String updateAsignaturaForm(Model model, @PathVariable Integer id){
        try {
            model.addAttribute("AsignaturaDTO", asignaturasService.getAsignaturaById(id));
            model.addAttribute("profesores", profesorService.getAllProfesores());
        }catch (AsignaturaNotFoundException e){
            System.err.println("No se ha encontrado la asignatura");
        }

        return "/asignatura/updateAsignatura";
    }

    @PostMapping("/asignatura/updateAsignatura/{id}")
    public String updateAsignatura(@ModelAttribute("AsignaturaDTO") AsignaturaDTO asignatura,@PathVariable Integer id){
        try {
            asignaturasService.updateAsignatura(id,asignatura);
        }catch (AsignaturaNotFoundException e){
            System.err.println("No se ha encontrado la asignatura");
        }catch (ProfesorNotFoundException e){
            System.err.println("No se ha encontrado el profesor");
        }

        return "redirect:/asignatura/asignaturaMain";
    }

    @GetMapping("/asignatura/deleteAsignatura/{id}")
    public String deleteAsignatura(@PathVariable Integer id){
        try {
            asignaturasService.deleteAsignatura(id);
        }catch (AsignaturaNotFoundException e){
            System.err.println("No se ha encontrado la asignatura");
        }
        return "redirect:/asignatura/asignaturaMain";
    }

    @PostMapping("/asignatura/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                uploadFileService.uploadAsignatura(file);
            }catch (Exception e) {
                System.err.println("No se ha encontrado subir las asignaturas");
            }
        }
        return "redirect:/asignatura/asignaturaMain";
    }

}
