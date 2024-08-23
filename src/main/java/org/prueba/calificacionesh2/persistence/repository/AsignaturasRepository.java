package org.prueba.calificacionesh2.persistence.repository;

import org.prueba.calificacionesh2.persistence.entity.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsignaturasRepository extends JpaRepository<Asignatura, Integer> {
    Optional<Asignatura> findByName(String nombre);
    Boolean existsByName(String nombre);
}
