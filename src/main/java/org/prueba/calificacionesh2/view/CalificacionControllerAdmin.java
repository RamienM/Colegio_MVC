package org.prueba.calificacionesh2.view;

import org.prueba.calificacionesh2.dto.CalificacionDTO;
import org.prueba.calificacionesh2.exception.AlumnoNotFoundException;
import org.prueba.calificacionesh2.exception.AsignaturaNotFoundException;
import org.prueba.calificacionesh2.exception.CalificacionNotFoundException;
import org.prueba.calificacionesh2.service.AlumnoService;
import org.prueba.calificacionesh2.service.AsignaturasService;
import org.prueba.calificacionesh2.service.CalificacionesService;
import org.prueba.calificacionesh2.excelManagement.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CalificacionControllerAdmin {

    @Autowired
    private CalificacionesService calificacionService;
    @Autowired
    private AsignaturasService asignaturasService;
    @Autowired
    private AlumnoService alumnoService;
    @Autowired
    private UploadFileService uploadFileService;

    @GetMapping("/calificacion/calificacionMain")
    public String viewAdminView(Model model){
        model.addAttribute("calificaciones", calificacionService.getAllCalificaciones());
        return "/calificacion/calificacionMain";
    }

    @GetMapping("/calificacion/saveCalificacion")
    public String saveCalificacion(Model model){
        model.addAttribute("CalificacionDTO", new CalificacionDTO());
        model.addAttribute("alumnos", alumnoService.getAllAlumnos());
        model.addAttribute("asignaturas", asignaturasService.getAllAsignaturas());
        return "/calificacion/saveCalificacion";
    }

    @PostMapping("/calificacion/saveCalificacion")
    public String saveCalificacion(@ModelAttribute("CalificacionDTO") CalificacionDTO calificacion){
        try {
            calificacionService.addCalificaciones(calificacion);
        }catch (AsignaturaNotFoundException e){
            System.err.println("No se ha encontrado la asignatura");
        }catch (AlumnoNotFoundException e){
            System.err.println("No se ha encontrado el alumno");
        }
        return "redirect:/calificacion/calificacionMain";
    }

    @GetMapping("/calificacion/updateCalificacion/{id}")
    public String updateCalificacion(Model model, @PathVariable Integer id){
        try {
            model.addAttribute("CalificacionDTO", calificacionService.getCalificacionesById(id));
            model.addAttribute("alumnos", alumnoService.getAllAlumnos());
            model.addAttribute("asignaturas", asignaturasService.getAllAsignaturas());
        }catch (CalificacionNotFoundException e){
            System.err.println("No se ha encontrado la calificacion");
        }
        return "/calificacion/updateCalificacion";
    }

    @PostMapping("/calificacion/updateCalificacion/{id}")
    public String updateCalificacion(@ModelAttribute("CalificacionDTO") CalificacionDTO calificacion, @PathVariable Integer id){
        try {
            calificacionService.updateCalificaciones(id,calificacion);
        }catch (CalificacionNotFoundException e){
            System.err.println("No se ha encontrado la calificacion");
        }catch (AsignaturaNotFoundException e){
            System.err.println("No se ha encontrado la asignatura");
        }catch (AlumnoNotFoundException e){
            System.err.println("No se ha encontrado el alumno");
        }
        return "redirect:/calificacion/calificacionMain";
    }

    @GetMapping("/calificacion/deleteCalificacion/{id}")
    public String deleteCalificacion(@PathVariable Integer id){
        try {
            calificacionService.deleteCalificaciones(id);
        }catch (CalificacionNotFoundException e){
            System.err.println("No se ha encontrado la calificacion");
        }
        return "redirect:/calificacion/calificacionMain";
    }

    @PostMapping("/calificacion/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                uploadFileService.uploadCalificaciones(file);
            }catch (Exception e) {
                System.err.println("No se ha podido subir las calificaciones");
            }
        }
        return "redirect:/calificacion/calificacionMain";
    }
}
