package org.prueba.calificacionesh2.repository;

import org.prueba.calificacionesh2.entity.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AsignaturasRepository extends JpaRepository<Asignatura, Integer> {
    Optional<Asignatura> findByName(String nombre);
    Boolean existsByName(String nombre);
}
