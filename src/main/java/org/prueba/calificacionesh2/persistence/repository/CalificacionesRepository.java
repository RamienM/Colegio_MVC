package org.prueba.calificacionesh2.persistence.repository;

import org.prueba.calificacionesh2.persistence.entity.Alumno;
import org.prueba.calificacionesh2.persistence.entity.Asignatura;
import org.prueba.calificacionesh2.persistence.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionesRepository extends JpaRepository<Calificacion,Integer> {

    @Query(nativeQuery = true, value =
            "SELECT calificacion.id, calificacion.mark, calificacion.id_alumno, calificacion.id_asignatura FROM " +
                    "profesor JOIN asignatura ON asignatura.id_profesor = profesor.id " +
                    "JOIN calificacion ON calificacion.id_asignatura = asignatura.id " +
                    "JOIN alumno ON calificacion.id_alumno = alumno.id " +
                    "WHERE profesor.id = ?1 " +
                    "ORDER BY alumno.id")
    List<Calificacion> findCalificacionesByProfesorId(Integer id_profesor);

    @Query(value = "select u from Calificacion u where u.idAlumno.id = ?1")
    List<Calificacion> findCalificacionByAlumnoId(Integer id_alumno);

    Boolean existsByIdAlumnoAndIdAsignatura(Alumno alumno, Asignatura asignatura);
}
