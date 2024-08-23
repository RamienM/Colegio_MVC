package org.prueba.calificacionesh2.view;

import org.prueba.calificacionesh2.dto.AlumnoDTO;
import org.prueba.calificacionesh2.exception.AlumnoNotFoundException;
import org.prueba.calificacionesh2.service.AlumnoService;
import org.prueba.calificacionesh2.service.CalificacionesService;
import org.prueba.calificacionesh2.service.EmailService;
import org.prueba.calificacionesh2.excelManagement.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AlumnoControllerAdmin {

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private CalificacionesService calificacionesService;

    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/alumno/alumnoMain")
    public String viewAdminView(Model model) {
        model.addAttribute("alumnos", alumnoService.getAllAlumnos());
        return "/alumno/alumnoMain";
    }

    @GetMapping("/alumno/saveAlumno")
    public String saveAlumno(Model model) {
        model.addAttribute("AlumnoDTO", new AlumnoDTO());
        return "/alumno/saveAlumno";
    }

    @PostMapping("/alumno/saveAlumno")
    public String saveAlumno(Model model, @ModelAttribute("AlumnoDTO") AlumnoDTO alumno) {
        model.addAttribute("AlumnoDTO", new AlumnoDTO());
        var res = alumnoService.addAlumno(alumno);
        model.addAttribute("msg", res.toString());
        return "redirect:/alumno/alumnoMain";
    }

    @GetMapping("/alumno/updateAlumno/{id}")
    public String updateAlumnoForm(Model model, @PathVariable Integer id) {
        try {
            model.addAttribute("AlumnoDTO", alumnoService.getAlumnoById(id));
        } catch (AlumnoNotFoundException e) {

        }

        return "/alumno/updateAlumno";
    }

    @PostMapping("/alumno/updateAlumno/{id}")
    public String updateAlumno(Model model, @ModelAttribute("AlumnoDTO") AlumnoDTO alumno, @PathVariable Integer id) {
        try {
            var res = alumnoService.updateAlumno(id, alumno);
            model.addAttribute("msg", res.toString());
        } catch (AlumnoNotFoundException e) {
            model.addAttribute("msg", "El alumno con id " + id + " no existe");
        }

        return "redirect:/alumno/alumnoMain";
    }

    @GetMapping("/alumno/deleteAlumno/{id}")
    public String deleteAlumno(@PathVariable("id") Integer id) {
        try {
            alumnoService.deleteAlumno(id);
        } catch (AlumnoNotFoundException e) {

        }
        return "redirect:/alumno/alumnoMain";
    }

    @GetMapping("/alumno/calificaciones/{id}")
    public String getCalificacionesByAlumno(Model model, @PathVariable Integer id) {
        model.addAttribute("calificaciones", calificacionesService.getCalificacionesAndAsignaturasByIdAlumno(id));
        return "/alumno/alumnoCalification";
    }

    @PostMapping("/alumno/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                uploadFileService.uploadAlumno(file);
                emailService.sendEmail("ruben.ramis@patterson.agency","Alumnos Datos","Ruben","Se han subido los datos correctamente","Ruben");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/alumno/alumnoMain";
    }

}
