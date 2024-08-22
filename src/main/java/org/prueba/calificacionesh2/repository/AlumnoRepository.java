package org.prueba.calificacionesh2.repository;

import org.prueba.calificacionesh2.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
}
