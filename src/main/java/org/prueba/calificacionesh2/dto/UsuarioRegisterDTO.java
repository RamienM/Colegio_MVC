package org.prueba.calificacionesh2.dto;

import lombok.Data;

@Data
public class UsuarioRegisterDTO {
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String rol;
}
