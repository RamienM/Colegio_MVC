package org.prueba.calificacionesh2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prueba.calificacionesh2.persistence.entity.Asignatura;
import org.prueba.calificacionesh2.persistence.repository.AsignaturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback
public class AsignaturasRepositoryTest {
    @Autowired
    AsignaturasRepository asignaturasRepository;

    @Test
    void saveTest(){
        //Arrange
        var asignatura = new Asignatura();
        //Act
        var response = asignaturasRepository.save(asignatura);
        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(asignatura.getId(),response.getId());
    }

    @Test
    void findByIdTest(){
        //Arrange
        var asignatura = asignaturasRepository.save(new Asignatura());
        //Act
        var response = asignaturasRepository.findById(asignatura.getId());
        //Assets
        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(asignatura.getId(),response.get().getId());
    }

    @Test
    void updateTest(){
        //Arrange
        var asignatura = new Asignatura();
        asignatura.setName("Hola");
        var res = asignaturasRepository.save(asignatura);
        //Act
        var updateAsignatura = asignaturasRepository.findById(res.getId());
        updateAsignatura.get().setName("Spring Boot");
        var response = asignaturasRepository.save(updateAsignatura.get());
        //Assets
        Assertions.assertNotNull(response);
        Assertions.assertEquals(updateAsignatura.get().getName(),response.getName());
    }
    @Test
    void deleteTest(){
        //Arrange
        var asignatura = asignaturasRepository.save(new Asignatura());
        //Act
        asignaturasRepository.delete(asignatura);
        var response = asignaturasRepository.findById(asignatura.getId());
        //Assets
        Assertions.assertTrue(response.isEmpty());
    }
}
