package org.prueba.calificacionesh2.view;

import jakarta.mail.MessagingException;
import org.prueba.calificacionesh2.dto.AlumnoDTO;
import org.prueba.calificacionesh2.exception.AlumnoNotFoundException;
import org.prueba.calificacionesh2.service.AlumnoService;
import org.prueba.calificacionesh2.service.CalificacionesService;
import org.prueba.calificacionesh2.service.EmailService;
import org.prueba.calificacionesh2.excelManagement.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
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
        alumnoService.addAlumno(alumno);
        return "redirect:/alumno/alumnoMain";
    }

    @GetMapping("/alumno/updateAlumno/{id}")
    public String updateAlumnoForm(Model model, @PathVariable Integer id) {
        try {
            model.addAttribute("AlumnoDTO", alumnoService.getAlumnoById(id));
        } catch (AlumnoNotFoundException e) {
            System.err.println("No se ha encontrado el alumno");
        }

        return "/alumno/updateAlumno";
    }

    @PostMapping("/alumno/updateAlumno/{id}")
    public String updateAlumno(@ModelAttribute("AlumnoDTO") AlumnoDTO alumno, @PathVariable Integer id) {
        try {
            alumnoService.updateAlumno(id, alumno);
        } catch (AlumnoNotFoundException e) {
            System.err.println("No se ha encontrado el alumno");
        }

        return "redirect:/alumno/alumnoMain";
    }

    @GetMapping("/alumno/deleteAlumno/{id}")
    public String deleteAlumno(@PathVariable("id") Integer id) {
        try {
            alumnoService.deleteAlumno(id);
        } catch (AlumnoNotFoundException e) {
            System.err.println("No se ha encontrado el alumno");
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
            }catch (MessagingException m){
                System.err.println("No se ha podido mandar el email");
            }catch (MailAuthenticationException m){
                System.err.println("El usuario o contraseña para el envio de mails no esta configurado o son erroneos");
            } catch (Exception e) {
                System.err.println("No se ha podido subir el archivo");
            }
        }
        return "redirect:/alumno/alumnoMain";
    }

}
