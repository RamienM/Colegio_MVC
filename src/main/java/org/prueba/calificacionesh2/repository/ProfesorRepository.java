package org.prueba.calificacionesh2.repository;

import org.prueba.calificacionesh2.entity.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfesorRepository extends JpaRepository<Profesor, Integer> {
    Optional<Profesor> findByNombre(String nombre);
    Boolean existsByNombreAndCorreo(String nombre, String correo);
}
