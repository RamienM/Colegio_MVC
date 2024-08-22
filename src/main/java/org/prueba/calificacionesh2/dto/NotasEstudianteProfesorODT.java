package org.prueba.calificacionesh2.dto;

import lombok.Data;
import org.prueba.calificacionesh2.entity.Calificacion;

@Data
public class NotasEstudianteProfesorODT {
    private String nombreEstudiante;
    private String nombreAsignatura;
    private Float nota;

    public NotasEstudianteProfesorODT() {}
    public NotasEstudianteProfesorODT(Calificacion calificacion){
        this.nota = calificacion.getMark();
        this.nombreEstudiante = calificacion.getIdAlumno().getNombre();
        this.nombreAsignatura = calificacion.getIdAsignatura().getName();
    }
}
