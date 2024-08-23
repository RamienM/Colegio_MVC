package org.prueba.calificacionesh2.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prueba.calificacionesh2.business.dto.ProfesorDTO;
import org.prueba.calificacionesh2.business.service.ProfesorService;
import org.prueba.calificacionesh2.persistence.entity.Profesor;
import org.prueba.calificacionesh2.business.exception.ProfesorNotFoundException;
import org.prueba.calificacionesh2.persistence.repository.ProfesorRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)  // Desactiva los filtros de seguridad
public class ProfesorServiceTest {
    @Mock
    private ProfesorRepository profesorRepository;

    @InjectMocks
    private ProfesorService profesorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProfesorTest() {
        //Arrange
        var profesor = new Profesor();
        var profesorDTO = new ProfesorDTO(profesor);

        when(profesorRepository.save(any(Profesor.class))).thenReturn(profesor);

        //Act
        var response = profesorService.addProfesor(profesorDTO);

        //Assert
        assertThat(profesorDTO).isEqualTo(response);
        verify(profesorRepository, times(1)).save(any(Profesor.class));
    }

    @Test
    void getAllProfesoresTest() {
        //Arrange
        var profesores = new ArrayList<Profesor>();
        profesores.add(new Profesor());

        var profesoresODT = new ArrayList<ProfesorDTO>();
        profesoresODT.add(new ProfesorDTO(profesores.get(0)));

        when(profesorRepository.findAll()).thenReturn(profesores);

        //Act
        var response = profesorService.getAllProfesores();

        //Assert
        assertThat(profesoresODT).isEqualTo(response);
        verify(profesorRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllProfesoresReturnArrayListVacio(){
        //Arrange
        when(profesorRepository.findAll()).thenReturn(new ArrayList<>());

        //Act
        var response = profesorService.getAllProfesores();

        //Assert
        assertThat(response).isEmpty();
        verify(profesorRepository,times(1)).findAll();
    }

    @Test
    void getProfesorByIdTest() throws ProfesorNotFoundException {
        //Arrange
        int id = 1;

        var profesor = new Profesor();
        profesor.setId(id);

        var profesorODT = new ProfesorDTO(profesor);

        when(profesorRepository.findById(id)).thenReturn(Optional.of(profesor));

        //Act
        var response = profesorService.getProfesorById(id);

        //Assert
        assertThat(profesorODT).isEqualTo(response);
        verify(profesorRepository, times(1)).findById(id);
    }

    @Test
    void getProfesorByIdNotFoundTest(){
        //Arrange
        int id = 1;

        when(profesorRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(ProfesorNotFoundException.class,() ->{profesorService.getProfesorById(id);});

        //Assertion
        Assertions.assertNotNull(exp);
        verify(profesorRepository, times(1)).findById(id);
    }

    @Test
    void updateProfesorTest() throws ProfesorNotFoundException{
        //Arrange
        int id = 1;
        var profesor = new Profesor();
        profesor.setId(id);
        profesor.setNombre("Nombre");

        var profesorDTO = new ProfesorDTO(profesor);

        when(profesorRepository.findById(id)).thenReturn(Optional.of(profesor));
        when(profesorRepository.save(any(Profesor.class))).thenReturn(profesor);

        //Act
        var response = profesorService.updateProfesor(id,profesorDTO);

        //Assert
        assertThat(profesorDTO).isEqualTo(response);
        verify(profesorRepository, times(1)).findById(id);
        verify(profesorRepository, times(1)).save(any(Profesor.class));
    }

    @Test
    void updateProfesorNotFoundTest(){
        //Arrange
        int id = 1;

        when(profesorRepository.findById(id)).thenReturn(Optional.empty());

        //Act

        var exp = Assertions.assertThrows(ProfesorNotFoundException.class, () -> {profesorService.updateProfesor(id, new ProfesorDTO());});

        //Assert
        Assertions.assertNotNull(exp);
        verify(profesorRepository, times(1)).findById(id);
    }

    @Test
    void deleteCalificacionTest() throws ProfesorNotFoundException {
        //Arrange
        int id = 1;

        var profesor = new Profesor();
        profesor.setId(id);

        when(profesorRepository.findById(id)).thenReturn(Optional.of(profesor));

        //Act
        profesorService.deleteProfesor(id);

        //Assert
        verify(profesorRepository, times(1)).findById(id);
    }

    @Test
    void deleteProfesorNotFoundExceptionTest() {
        //Arrange
        int id = 1;

        when(profesorRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(ProfesorNotFoundException.class, () -> {profesorService.deleteProfesor(id);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(profesorRepository, times(1)).findById(id);
    }
}
