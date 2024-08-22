package org.prueba.calificacionesh2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prueba.calificacionesh2.entity.Profesor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback
public class ProfesorRepositoryTest {
    @Autowired
    private ProfesorRepository profesorRepository;

    @Test
    void saveTest(){
        //Arrange
        Profesor profesor = new Profesor();
        //Act
        var response = profesorRepository.save(profesor);
        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getId(),profesor.getId());
    }

    @Test
    void findTestById(){
        //Arrange
        var savedProfesor = profesorRepository.save(new Profesor());
        //Act
        var response = profesorRepository.findById(savedProfesor.getId());
        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(savedProfesor.getId(),response.get().getId());
    }

    @Test
    void updateTest(){
        //Arrange
        var profesor = new Profesor();
        profesor.setNombre("Hola");
        var savedProfesor = profesorRepository.save(profesor);
        //Act
        var updatedProfesor = profesorRepository.findById(savedProfesor.getId());
        updatedProfesor.get().setNombre("Qinxiu");
        var response = profesorRepository.save(updatedProfesor.get());
        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(updatedProfesor.get().getNombre(),response.getNombre());
    }

    @Test
    void deleteTest(){
        //Arrange
        var savedProfesor = profesorRepository.save(new Profesor());
        //Act
        profesorRepository.delete(savedProfesor);
        var response = profesorRepository.findById(savedProfesor.getId());
        //Assert
        Assertions.assertTrue(response.isEmpty());
    }
}
