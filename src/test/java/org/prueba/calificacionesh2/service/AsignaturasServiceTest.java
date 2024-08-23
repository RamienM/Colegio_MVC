package org.prueba.calificacionesh2.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prueba.calificacionesh2.business.dto.AsignaturaDTO;
import org.prueba.calificacionesh2.business.service.AsignaturasService;
import org.prueba.calificacionesh2.persistence.entity.Asignatura;
import org.prueba.calificacionesh2.persistence.entity.Profesor;
import org.prueba.calificacionesh2.business.exception.AsignaturaNotFoundException;
import org.prueba.calificacionesh2.business.exception.ProfesorNotFoundException;
import org.prueba.calificacionesh2.persistence.repository.AsignaturasRepository;
import org.prueba.calificacionesh2.persistence.repository.ProfesorRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)  // Desactiva los filtros de seguridad
public class AsignaturasServiceTest {
    @Mock
    private AsignaturasRepository asignaturasRepository;
    @Mock
    private ProfesorRepository profesorRepository;
    @InjectMocks
    private AsignaturasService asignaturasService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addAsignaturaTest() throws ProfesorNotFoundException {
        // Arrange
        int profesorId = 1;
        Profesor profesor = new Profesor();
        profesor.setId(profesorId);

        AsignaturaDTO asignaturaDTO = new AsignaturaDTO();
        asignaturaDTO.setNombreAsignatura("Mock");
        asignaturaDTO.setIdProfesor(profesorId);

        Asignatura asignatura = new Asignatura();
        asignatura.setName(asignaturaDTO.getNombreAsignatura());
        asignatura.setIdProfesor(profesor);

        when(profesorRepository.findById(profesorId)).thenReturn(Optional.of(profesor));
        when(asignaturasRepository.save(any(Asignatura.class))).thenReturn(asignatura);

        // Act
        AsignaturaDTO result = asignaturasService.addAsignatura(asignaturaDTO);

        // Assert
        assertThat(result.getNombreAsignatura()).isEqualTo(asignaturaDTO.getNombreAsignatura());
        verify(profesorRepository,times(1)).findById(profesorId);
        verify(asignaturasRepository,times(1)).save(any(Asignatura.class));
    }

    @Test
    void addAsignaturaProfesorNotFoundTest(){
        //Arrange
        int id = 1;
        AsignaturaDTO asignaturaDTO = new AsignaturaDTO();
        asignaturaDTO.setIdProfesor(id);

        when(profesorRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(ProfesorNotFoundException.class, ()->{asignaturasService.addAsignatura(asignaturaDTO);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(profesorRepository,times(1)).findById(id);
    }

    @Test
    void getAllAsignaturasTest() {
        //Arrange
        var asignaturas = new ArrayList<Asignatura>();
        asignaturas.add(new Asignatura());

        var asignaturasDTO = new ArrayList<AsignaturaDTO>();
        asignaturasDTO.add(new AsignaturaDTO(asignaturas.get(0)));

        when(asignaturasRepository.findAll()).thenReturn(asignaturas);

        //Act
        var response = asignaturasService.getAllAsignaturas();

        //Assert
        assertThat(asignaturasDTO).isEqualTo(response);
        verify(asignaturasRepository,times(1)).findAll();
    }

    @Test
    void whenGetAllAsignaturasReturnArrayListVacio(){
        //Arrange
        when(asignaturasRepository.findAll()).thenReturn(new ArrayList<>());

        //Act
        var response = asignaturasService.getAllAsignaturas();

        //Assert
        assertThat(response).isEmpty();
        verify(asignaturasRepository,times(1)).findAll();
    }

    @Test
    void getAsignaturaByIdTest() throws AsignaturaNotFoundException {
        //Arrange
        int id = 1;

        var asignatura = new Asignatura();
        asignatura.setName("Mock");
        asignatura.setId(id);

        var asignaturaDTO = new AsignaturaDTO(asignatura);

        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(asignatura));

        //Act
        var response = asignaturasService.getAsignaturaById(id);

        //Assert
        assertThat(asignaturaDTO).isEqualTo(response);
        verify(asignaturasRepository,times(1)).findById(id);
    }

    @Test
    void getAsignaturaByIdNotFoundException(){
        //Arrange
        int id = 1;

        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(AsignaturaNotFoundException.class, ()->{asignaturasService.getAsignaturaById(id);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(asignaturasRepository,times(1)).findById(id);
    }

    @Test
    void updateAsignaturaTest() throws AsignaturaNotFoundException, ProfesorNotFoundException {
        //Arrange
        int id = 1;

        var profesor = new Profesor();
        profesor.setId(id);

        var asignatura = new Asignatura();
        asignatura.setName("Hola");
        asignatura.setId(id);
        asignatura.setIdProfesor(profesor);

        var asignaturaDTO = new AsignaturaDTO(asignatura);

        when(profesorRepository.findById(id)).thenReturn(Optional.of(profesor));
        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(asignatura));
        when(asignaturasRepository.save(any(Asignatura.class))).thenReturn(asignatura);

        //Act
        var response = asignaturasService.updateAsignatura(id,asignaturaDTO);

        //Assert
        assertThat(asignaturaDTO).isEqualTo(response);
        verify(asignaturasRepository,times(1)).save(asignatura);
        verify(asignaturasRepository,times(1)).findById(id);
        verify(profesorRepository,times(1)).findById(profesor.getId());
    }

    @Test
    void updateAsignaturaNotFoundTest() {
        //Arrange
        int id = 1;

        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());

        //Act

        var exp = Assertions.assertThrows(AsignaturaNotFoundException.class, () -> {asignaturasService.updateAsignatura(id, new AsignaturaDTO());});

        //Assert
        Assertions.assertNotNull(exp);
        verify(asignaturasRepository,times(1)).findById(id);
    }

    @Test
    void updateAsignaturaProfesorNotFoundTest() {
        //Arrange
        int id = 1;
        var asignatura = new AsignaturaDTO();
        asignatura.setIdProfesor(id);

        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(new Asignatura()));
        when(profesorRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(ProfesorNotFoundException.class, () ->{asignaturasService.updateAsignatura(id, asignatura);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(asignaturasRepository,times(1)).findById(id);
        verify(profesorRepository,times(1)).findById(id);
    }

    @Test
    void deleteAsignaturaTest() throws AsignaturaNotFoundException{
        // Arrange
        int id = 1;

        Asignatura asignatura = new Asignatura();
        asignatura.setId(id);

        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(asignatura));

        // Act
        asignaturasService.deleteAsignatura(id);

        // Assert
        verify(asignaturasRepository, times(1)).findById(id);
        verify(asignaturasRepository, times(1)).delete(asignatura);
    }

    @Test
    void deleteAsignaturaNotFoundTest(){
        //Arrange
        int id = 1;

        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(AsignaturaNotFoundException.class,() -> {asignaturasService.deleteAsignatura(id);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(asignaturasRepository,times(1)).findById(id);
    }

}
