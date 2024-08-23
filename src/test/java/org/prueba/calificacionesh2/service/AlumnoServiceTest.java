package org.prueba.calificacionesh2.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prueba.calificacionesh2.business.dto.AlumnoDTO;
import org.prueba.calificacionesh2.business.service.AlumnoService;
import org.prueba.calificacionesh2.persistence.entity.Alumno;
import org.prueba.calificacionesh2.business.exception.AlumnoNotFoundException;
import org.prueba.calificacionesh2.persistence.repository.AlumnoRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)  // Desactiva los filtros de seguridad
public class AlumnoServiceTest {
    @Mock
    private AlumnoRepository alumnoRepository;
    @InjectMocks
    private AlumnoService alumnoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Añadir test para casos donde el ID no se encuentra
    @Test
    void addAlumnoTest() {
        //Arrange
        var alumno = new Alumno();
        var alumnoDTO = new AlumnoDTO(alumno);

        when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumno);

        //Act
        var response = alumnoService.addAlumno(alumnoDTO);

        //Assert
        assertThat(alumnoDTO).isEqualTo(response);
        verify(alumnoRepository, times(1)).save(any(Alumno.class));
    }

    @Test
    void getAllAlumnosTest(){
        //Arrange
        var alumnos = new ArrayList<Alumno>();
        alumnos.add(new Alumno());

        var alumnosDTO = new ArrayList<AlumnoDTO>();
        alumnosDTO.add(new AlumnoDTO(alumnos.get(0)));

        when(alumnoRepository.findAll()).thenReturn(alumnos);

        //Act
        var response = alumnoService.getAllAlumnos();

        //Assert
        assertThat(alumnosDTO).isEqualTo(response);
        verify(alumnoRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllAlumnosReturnArrayListVacio(){
        //Arrange
        when(alumnoRepository.findAll()).thenReturn(new ArrayList<>());

        //Act
        var response = alumnoService.getAllAlumnos();

        //Assert
        assertThat(response).isEmpty();
        verify(alumnoRepository,times(1)).findAll();
    }

    @Test
    void getAlumnoByIdTest() throws AlumnoNotFoundException {
        //Arrange
        int id = 1;

        var alumno = new Alumno();
        alumno.setId(id);

        var alumnoDTO = new AlumnoDTO(alumno);
        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));

        //Act
        var response = alumnoService.getAlumnoById(id);

        //Assert
        assertThat(alumnoDTO).isEqualTo(response);
        verify(alumnoRepository, times(1)).findById(id);
    }

    @Test
    void getAlumnoByIdNotFoundExceptionTest() {
        //Arrange
        int id = 1;

        when(alumnoRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(AlumnoNotFoundException.class, () -> {alumnoService.getAlumnoById(id);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(alumnoRepository, times(1)).findById(id);
    }

    @Test
    void updateAlumnoTest() throws AlumnoNotFoundException{
        //Arrange
        int id = 1;

        var alumno = new Alumno();
        alumno.setId(id);
        alumno.setNombre("Ruben");

        var alumnoDTO = new AlumnoDTO(alumno);

        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));
        when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumno);

        //Act
        var response = alumnoService.updateAlumno(id,alumnoDTO);

        //Assert
        assertThat(alumnoDTO).isEqualTo(response);
        verify(alumnoRepository, times(1)).findById(id);
        verify(alumnoRepository, times(1)).save(any(Alumno.class));
    }

    @Test
    void updateAlumnoNotFoundExceptionTest(){
        //Arrange
        int id = 1;

        when(alumnoRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(AlumnoNotFoundException.class, () -> {alumnoService.updateAlumno(id,new AlumnoDTO());});

        //Assetions
        Assertions.assertNotNull(exp);
        verify(alumnoRepository, times(1)).findById(id);
    }

    @Test
    void deleteAlumnoTest() throws AlumnoNotFoundException{
        //Arrange
        int id = 1;

        Alumno alumno = new Alumno();
        alumno.setId(id);

        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));

        //Act
        alumnoService.deleteAlumno(id);

        //Assert
        verify(alumnoRepository, times(1)).findById(id);
        verify(alumnoRepository, times(1)).delete(alumno);
    }

    @Test
    void deleteAlumnoNotFoundExceptionTest(){
        //Arrange
        int id = 1;

        when(alumnoRepository.findById(id)).thenReturn(Optional.empty());

        //Act
       var exp = Assertions.assertThrows(AlumnoNotFoundException.class,()->{alumnoService.deleteAlumno(id);});

        //Assetion
        Assertions.assertNotNull(exp);
        verify(alumnoRepository, times(1)).findById(id);
    }
}
