package org.prueba.calificacionesh2.view;

import org.prueba.calificacionesh2.dto.AsignaturaDTO;
import org.prueba.calificacionesh2.exception.AsignaturaNotFoundException;
import org.prueba.calificacionesh2.exception.ProfesorNotFoundException;
import org.prueba.calificacionesh2.service.AsignaturasService;
import org.prueba.calificacionesh2.service.ProfesorService;
import org.prueba.calificacionesh2.excelManagement.UploadFileService;
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
    public String saveAsignatua(Model model, @ModelAttribute("AsignaturaDTO") AsignaturaDTO asignatura){
        try {
            var res = asignaturasService.addAsignatura(asignatura);
            model.addAttribute("msg", res.toString());
        }catch (ProfesorNotFoundException e){
            model.addAttribute("msg", "El profesor con ID: " + asignatura.getIdProfesor() + " no se ha podido encontrar");
        }

        return "redirect:/asignatura/asignaturaMain";
    }
    @GetMapping("/asignatura/updateAsignatura/{id}")
    public String updateAsignaturaForm(Model model, @PathVariable Integer id){
        try {
            model.addAttribute("AsignaturaDTO", asignaturasService.getAsignaturaById(id));
            model.addAttribute("profesores", profesorService.getAllProfesores());
        }catch (AsignaturaNotFoundException e){

        }

        return "/asignatura/updateAsignatura";
    }

    @PostMapping("/asignatura/updateAsignatura/{id}")
    public String updateAsignatura(Model model, @ModelAttribute("AsignaturaDTO") AsignaturaDTO asignatura,@PathVariable Integer id){
        try {
            var res = asignaturasService.updateAsignatura(id,asignatura);
            model.addAttribute("msg", res.toString());
        }catch (AsignaturaNotFoundException e){
            model.addAttribute("msg", "No se ha encontrado la asignatura con id: " + id);
        }catch (ProfesorNotFoundException e){
            model.addAttribute("msg", "El profesor con ID: " + asignatura.getIdProfesor() + " no se ha podido encontrar");
        }

        return "redirect:/asignatura/asignaturaMain";
    }

    @GetMapping("/asignatura/deleteAsignatura/{id}")
    public String deleteAsignatura(Model model,@PathVariable Integer id){
        try {
            asignaturasService.deleteAsignatura(id);
            model.addAttribute("msg","Se ha borrado la asignatura con exito, id de la asignatura borrada: " +id);
        }catch (AsignaturaNotFoundException e){
            model.addAttribute("msg", "La asignatura no se ha podido borrar, no se ha podido encontrar la asignatura con id: " + id);
        }
        return "redirect:/asignatura/asignaturaMain";
    }

    @PostMapping("/asignatura/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                uploadFileService.uploadAsignatura(file);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/asignatura/asignaturaMain";
    }

}
