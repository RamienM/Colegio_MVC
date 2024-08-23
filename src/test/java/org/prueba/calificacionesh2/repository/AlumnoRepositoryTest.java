package org.prueba.calificacionesh2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prueba.calificacionesh2.persistence.entity.Alumno;
import org.prueba.calificacionesh2.persistence.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback
public class AlumnoRepositoryTest {

    @Autowired
    private AlumnoRepository alumnoRepository;


    @Test
    void saveTest() {
        // Arrange
        var alumno = new Alumno();

        // Act
        var response = alumnoRepository.save(alumno);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getApellido(), alumno.getApellido());
    }


    @Test
    void findByIdTest() {
        // Arrange
        var savedAlumno = alumnoRepository.save(new Alumno());

        //Act
        var response = alumnoRepository.findById(savedAlumno.getId());

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(savedAlumno.getId(),response.get().getId());

    }

    @Test
    void updateTest() {
        // Arrange
        var alumno = new Alumno();
        alumno.setNombre("Hola");
        var savedAlumno = alumnoRepository.save(alumno);

        //Act
        var alumnoUpdated = alumnoRepository.findById(savedAlumno.getId());
        alumnoUpdated.get().setNombre("Qinxiu");
        var response = alumnoRepository.save(alumnoUpdated.get());

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(alumnoUpdated.get().getNombre(),response.getNombre());

    }

    @Test
    void deleteTest() {
        //Arrange
        var savedAlumno = alumnoRepository.save(new Alumno());
        //Act
        alumnoRepository.delete(savedAlumno);
        var response = alumnoRepository.findById(savedAlumno.getId());
        //Assert
        Assertions.assertTrue(response.isEmpty());
    }

}
