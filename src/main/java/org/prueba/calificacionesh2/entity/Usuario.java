package org.prueba.calificacionesh2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "usuario")
public class Usuario extends Persona {
    private String username;
    private String password;
    private String rol;
}
