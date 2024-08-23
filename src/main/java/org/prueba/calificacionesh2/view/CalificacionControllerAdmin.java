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
    public String saveCalificacion(Model model, @ModelAttribute("CalificacionDTO") CalificacionDTO calificacion){
        try {
            var res = calificacionService.addCalificaciones(calificacion);
            model.addAttribute("msg", res.toString());
        }catch (AsignaturaNotFoundException e){
            model.addAttribute("msg","No se ha encontrado la asignatura con id "+ calificacion.getIdAsignatura());
        }catch (AlumnoNotFoundException e){
            model.addAttribute("msg","No se ha podido encontrar el alumno con id " + calificacion.getIdAlumno());
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

        }
        return "/calificacion/updateCalificacion";
    }

    @PostMapping("/calificacion/updateCalificacion/{id}")
    public String updateCalificacion(Model model, @ModelAttribute("CalificacionDTO") CalificacionDTO calificacion, @PathVariable Integer id){
        try {
            var res = calificacionService.updateCalificaciones(id,calificacion);
            model.addAttribute("msg", res.toString());
        }catch (CalificacionNotFoundException e){
            model.addAttribute("msg", "La calificacion con id "+id+ " no existe");
        }catch (AsignaturaNotFoundException e){
            model.addAttribute("msg","No se ha encontrado la asignatura con id "+ calificacion.getIdAsignatura());
        }catch (AlumnoNotFoundException e){
            model.addAttribute("msg","No se ha podido encontrar el alumno con id " + calificacion.getIdAlumno());
        }
        return "redirect:/calificacion/calificacionMain";
    }

    @GetMapping("/calificacion/deleteCalificacion/{id}")
    public String deleteCalificacion(Model model, @PathVariable Integer id){
        try {
            calificacionService.deleteCalificaciones(id);
            model.addAttribute("msg", "Calificacion eliminada con id "+id);
        }catch (CalificacionNotFoundException e){
            model.addAttribute("msg", "La calificacion con id "+id+ " no existe");
        }
        return "redirect:/calificacion/calificacionMain";
    }

    @PostMapping("/calificacion/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                uploadFileService.uploadCalificaciones(file);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/calificacion/calificacionMain";
    }
}
