package org.prueba.calificacionesh2.persistence.repository;

import org.prueba.calificacionesh2.persistence.entity.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Integer> {
    Optional<Profesor> findByNombre(String nombre);
    Boolean existsByNombreAndCorreo(String nombre, String correo);
}
