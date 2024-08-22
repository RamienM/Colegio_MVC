package org.prueba.calificacionesh2.dto;

import lombok.Data;
import org.prueba.calificacionesh2.entity.Profesor;

@Data
public class ProfesorDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;

    public ProfesorDTO() {}
    public ProfesorDTO(Profesor profesor) {
        this.id = profesor.getId();
        this.nombre = profesor.getNombre();
        this.apellido = profesor.getApellido();
        this.correo = profesor.getCorreo();
        this.telefono = profesor.getTelefono();
    }
}
