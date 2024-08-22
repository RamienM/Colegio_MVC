package org.prueba.calificacionesh2.repository;

import org.prueba.calificacionesh2.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    Optional<Alumno> findByNombre(String nombre);
}
