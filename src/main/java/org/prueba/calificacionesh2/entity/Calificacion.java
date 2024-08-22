package org.prueba.calificacionesh2.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "calificacion")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idAlumno", referencedColumnName = "id")
    private Alumno idAlumno;

    @ManyToOne
    @JoinColumn(name="idAsignatura", referencedColumnName = "id")
    private Asignatura idAsignatura;

    @Column(name = "mark")
    private Float mark;
}
