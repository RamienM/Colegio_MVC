package org.prueba.calificacionesh2.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "usuario")
public class Usuario extends Persona {
    private String username;
    private String password;
    private String rol;
}
