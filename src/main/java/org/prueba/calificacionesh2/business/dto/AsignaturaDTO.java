package org.prueba.calificacionesh2.business.dto;

import lombok.Data;
import org.prueba.calificacionesh2.persistence.entity.Asignatura;

@Data
public class AsignaturaDTO {
    private Integer id;
    private String nombreAsignatura;
    private Integer idProfesor;
    private String nombreProfesor;

    public AsignaturaDTO() {}
    public AsignaturaDTO(Asignatura asignatura) {
        this.id = asignatura.getId();
        this.nombreAsignatura = asignatura.getName();
        if (asignatura.getIdProfesor() != null) {
            this.idProfesor = asignatura.getIdProfesor().getId();
            this.nombreProfesor = asignatura.getIdProfesor().getNombre();
        }
    }
}
