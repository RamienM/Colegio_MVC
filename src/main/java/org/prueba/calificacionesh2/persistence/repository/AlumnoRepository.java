package org.prueba.calificacionesh2.persistence.repository;

import org.prueba.calificacionesh2.persistence.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    Optional<Alumno> findByNombre(String nombre);
    Boolean existsByNombreAndCorreo(String nombre, String correo);
}
