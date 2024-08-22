package org.prueba.calificacionesh2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prueba.calificacionesh2.entity.Alumno;
import org.prueba.calificacionesh2.entity.Asignatura;
import org.prueba.calificacionesh2.entity.Calificacion;
import org.prueba.calificacionesh2.entity.Profesor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback
public class CalificacionesRepositoryTest {

    @Autowired
    private CalificacionesRepository calificacionesRepository;
    @Autowired
    private ProfesorRepository profesorRepository;
    @Autowired
    private AsignaturasRepository asignaturasRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;

    @Test
    void saveTest(){
        //Arrange
        var cal = new Calificacion();
        //Act
        var response = calificacionesRepository.save(cal);
        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(cal.getId(), response.getId());
    }
    @Test
    void findById(){
        //Arrange
        var cal = calificacionesRepository.save(new Calificacion());
        //Act
        var response = calificacionesRepository.findById(cal.getId());
        //Assert
        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(cal.getId(), response.get().getId());
    }
    @Test
    void updateTest(){
        //Arrange
        var calificacion = calificacionesRepository.save(new Calificacion());
        calificacion.setMark(7.0f);
        var responseSave = calificacionesRepository.save(calificacion);
        //Act
        var updateCalificacion = calificacionesRepository.findById(calificacion.getId());
        updateCalificacion.get().setMark(10.0f);
        var response = calificacionesRepository.save(updateCalificacion.get());
        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(responseSave.getId(), response.getId());

    }
    @Test
    void deleteTest(){
        //Arrange
        var responseSave = calificacionesRepository.save(new Calificacion());
        //Act
        calificacionesRepository.delete(responseSave);
        var response = calificacionesRepository.findById(responseSave.getId());
        //Assert
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void findCalificacionesByProfesorId(){
        //Arrange
        var profesor = profesorRepository.save(new Profesor());
        var alumno = alumnoRepository.save(new Alumno());
        var asignatura = new Asignatura();
        asignatura.setIdProfesor(profesor);
        asignatura = asignaturasRepository.save(asignatura);


        var calificiones = new Calificacion();
        calificiones.setMark(7.0f);
        calificiones.setIdAlumno(alumno);
        calificiones.setIdAsignatura(asignatura);
        var saveCalificaciones = calificacionesRepository.save(calificiones);

        //Act
        var response = calificacionesRepository.findCalificacionesByProfesorId(profesor.getId());

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1,response.size()); //Response contiene un List de un solo valor
    }

    @Test
    void findCalificacionesByAlumnoId(){
        //Arrange
        var profesor = profesorRepository.save(new Profesor());
        var alumno = alumnoRepository.save(new Alumno());
        var asignatura = asignaturasRepository.save(new Asignatura());
        var calificiones = new Calificacion();
        calificiones.setMark(7.0f);
        calificiones.setIdAlumno(alumno);
        calificiones.setIdAsignatura(asignatura);
        var saveCalificaciones = calificacionesRepository.save(calificiones);
        //Act
        var response = calificacionesRepository.findCalificacionByAlumnoId(alumno.getId());
        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1,response.size());
    }

}