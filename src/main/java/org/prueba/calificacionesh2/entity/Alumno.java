package org.prueba.calificacionesh2.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "alumno")
public class Alumno extends Persona {
}
