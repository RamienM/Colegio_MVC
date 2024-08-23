package org.prueba.calificacionesh2.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prueba.calificacionesh2.business.dto.CalificacionDTO;
import org.prueba.calificacionesh2.business.dto.NotasEstudianteProfesorODT;
import org.prueba.calificacionesh2.business.service.CalificacionesService;
import org.prueba.calificacionesh2.persistence.entity.Alumno;
import org.prueba.calificacionesh2.persistence.entity.Asignatura;
import org.prueba.calificacionesh2.persistence.entity.Calificacion;
import org.prueba.calificacionesh2.business.exception.AlumnoNotFoundException;
import org.prueba.calificacionesh2.business.exception.AsignaturaNotFoundException;
import org.prueba.calificacionesh2.business.exception.CalificacionNotFoundException;
import org.prueba.calificacionesh2.persistence.repository.AlumnoRepository;
import org.prueba.calificacionesh2.persistence.repository.AsignaturasRepository;
import org.prueba.calificacionesh2.persistence.repository.CalificacionesRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)  // Desactiva los filtros de seguridad
public class CalificacionesServiceTest {

    @Mock
    private CalificacionesRepository calificacionesRepository;

    @Mock
    private AsignaturasRepository asignaturasRepository;

    @Mock
    private AlumnoRepository alumnoRepository;

    @InjectMocks
    private CalificacionesService calificacionesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCalificacionTest() throws AlumnoNotFoundException, AsignaturaNotFoundException {
        //Arrange
        int id = 1;

        var asignatura = new Asignatura();
        asignatura.setId(id);

        var alumno = new Alumno();
        alumno.setId(id);

        var calificacion = new Calificacion();
        calificacion.setIdAlumno(alumno);
        calificacion.setIdAsignatura(asignatura);

        var calificacionDTO = new CalificacionDTO(calificacion);

        when(calificacionesRepository.save(any(Calificacion.class))).thenReturn(calificacion);
        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(asignatura));
        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));

        //Act
        var response = calificacionesService.addCalificaciones(calificacionDTO);

        //Assert
        assertThat(calificacionDTO).isEqualTo(response);
        verify(calificacionesRepository, times(1)).save(any(Calificacion.class));
        verify(asignaturasRepository, times(1)).findById(id);
        verify(alumnoRepository, times(1)).findById(id);
    }

    @Test
    void addCalificacionAlumnoNotFoundTest(){
        //Arrange
        int id = 1;
        var calificacion = new CalificacionDTO();
        calificacion.setIdAlumno(id);
        calificacion.setIdAsignatura(id);

        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());
        when(alumnoRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(AlumnoNotFoundException.class,()->{calificacionesService.addCalificaciones(calificacion);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(asignaturasRepository, times(1)).findById(id);
        verify(alumnoRepository, times(1)).findById(id);
    }

    @Test
    void addCalificacionAsignaturaNotFoundTest(){
        //Arrange
        int id = 1;
        var calificacion = new CalificacionDTO();
        calificacion.setIdAlumno(id);
        calificacion.setIdAsignatura(id);

        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());
        when(alumnoRepository.findById(id)).thenReturn(Optional.of(new Alumno()));

        //Act
        var exp = Assertions.assertThrows(AsignaturaNotFoundException.class,()->{calificacionesService.addCalificaciones(calificacion);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(asignaturasRepository, times(1)).findById(id);
        verify(alumnoRepository, times(1)).findById(id);
    }

    @Test
    void getAllCalificacionesTest() {
        //Arrange
        var calificaciones = new ArrayList<Calificacion>();
        calificaciones.add(new Calificacion());

        var calificacionDTOs = new ArrayList<CalificacionDTO>();
        calificacionDTOs.add(new CalificacionDTO(calificaciones.get(0)));

        when(calificacionesRepository.findAll()).thenReturn(calificaciones);

        //Act
        var response = calificacionesService.getAllCalificaciones();

        //Assert
        assertThat(calificacionDTOs).isEqualTo(response);
        verify(calificacionesRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllCalificacioensReturnArrayListVacio(){
        //Arrange
        when(calificacionesRepository.findAll()).thenReturn(new ArrayList<>());

        //Act
        var response = calificacionesService.getAllCalificaciones();

        //Assert
        assertThat(response).isEmpty();
        verify(calificacionesRepository,times(1)).findAll();
    }

    @Test
    void getCalificacionByIdTest() throws CalificacionNotFoundException {
        //Arrange
        int id = 1;

        var calificacion = new Calificacion();
        calificacion.setId(id);

        var calificacionDTO = new CalificacionDTO(calificacion);

        when(calificacionesRepository.findById(id)).thenReturn(Optional.of(calificacion));

        //Act
        var response = calificacionesService.getCalificacionesById(id);

        //Assert
        assertThat(calificacionDTO).isEqualTo(response);
        verify(calificacionesRepository, times(1)).findById(id);
    }

    @Test
    void getCalificacionByIdNotFoundTest(){
        //Arrange
        int id = 1;

        when(calificacionesRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(CalificacionNotFoundException.class,()->{calificacionesService.getCalificacionesById(id);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(calificacionesRepository, times(1)).findById(id);
    }

    @Test
    void updateCalificacionTest() throws CalificacionNotFoundException, AsignaturaNotFoundException, AlumnoNotFoundException{
        //Arrange
        int id = 1;

        var alumno = new Alumno();
        alumno.setId(id);

        var asignatura = new Asignatura();
        asignatura.setId(id);

        var calificacion = new Calificacion();
        calificacion.setId(id);
        calificacion.setIdAsignatura(asignatura);
        calificacion.setIdAlumno(alumno);
        calificacion.setMark(7.0f);

        var calificacionDTO = new CalificacionDTO(calificacion);

        when(calificacionesRepository.findById(id)).thenReturn(Optional.of(calificacion));
        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(asignatura));
        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));
        when(calificacionesRepository.save(any(Calificacion.class))).thenReturn(calificacion);

        //Act
        var response = calificacionesService.updateCalificaciones(id, calificacionDTO);

        //Assert
        assertThat(calificacionDTO).isEqualTo(response);
        verify(calificacionesRepository, times(1)).findById(id);
        verify(asignaturasRepository, times(1)).findById(id);
        verify(alumnoRepository, times(1)).findById(id);
        verify(calificacionesRepository, times(1)).save(any(Calificacion.class));
    }

    @Test
    void updateCalificacionNotFoundTest(){
        //Arrange
        int id = 1;

        when(calificacionesRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(CalificacionNotFoundException.class, ()->{calificacionesService.updateCalificaciones(id,new CalificacionDTO());});

        //Assert
        Assertions.assertNotNull(exp);
        verify(calificacionesRepository, times(1)).findById(id);
    }

    @Test
    void updateCalificacionAsignaturaNotFoundTest(){
        //Arrange
        int id = 1;

        var asignatura = new Asignatura();
        asignatura.setId(id);

        var calificacion = new Calificacion();
        calificacion.setId(id);
        calificacion.setIdAsignatura(asignatura);
        calificacion.setMark(7.0f);

        when(calificacionesRepository.findById(id)).thenReturn(Optional.of(calificacion));
        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(AsignaturaNotFoundException.class,()->{calificacionesService.updateCalificaciones(id, new CalificacionDTO(calificacion));});

        //Assert
        Assertions.assertNotNull(exp);
        verify(calificacionesRepository, times(1)).findById(id);
        verify(asignaturasRepository, times(1)).findById(id);
    }

    @Test
    void updateCalificacionAlumnoNotFoundTest(){
        //Arrange
        int id = 1;

        var alumno = new Alumno();
        alumno.setId(id);

        var calificacion = new Calificacion();
        calificacion.setId(id);
        calificacion.setIdAlumno(alumno);
        calificacion.setMark(7.0f);

        when(calificacionesRepository.findById(id)).thenReturn(Optional.of(calificacion));
        when(alumnoRepository.findById(id)).thenReturn(Optional.empty());
        //Act
        var exp = Assertions.assertThrows(AlumnoNotFoundException.class,()->{calificacionesService.updateCalificaciones(id, new CalificacionDTO(calificacion));});

        //Assert
        Assertions.assertNotNull(exp);
        verify(calificacionesRepository, times(1)).findById(id);
        verify(alumnoRepository, times(1)).findById(id);
    }

    @Test
    void deleteCalificacionTest() throws CalificacionNotFoundException{
        //Arrange
        int id = 1;

        var calificacion = new Calificacion();
        calificacion.setId(id);

        when(calificacionesRepository.findById(id)).thenReturn(Optional.of(calificacion));

        //Act
        calificacionesService.deleteCalificaciones(id);

        //Assert
        verify(calificacionesRepository, times(1)).findById(id);
    }

    @Test
    void deleteCalificacionNotFoundTest(){
        //Arrange
        int id = 1;

        when(calificacionesRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        var exp = Assertions.assertThrows(CalificacionNotFoundException.class, ()->{calificacionesService.deleteCalificaciones(id);});

        //Assert
        Assertions.assertNotNull(exp);
        verify(calificacionesRepository, times(1)).findById(id);
    }


    @Test
    void getCalificacionesEstudiantesAndAsignaturasByIdProfesorTest(){
        //Arrange
        int id = 1;

        var asignatura = new Asignatura();
        asignatura.setId(id);

        var alumno = new Alumno();
        alumno.setId(id);

        var calificacion = new Calificacion();
        calificacion.setId(id);
        calificacion.setIdAlumno(alumno);
        calificacion.setIdAsignatura(asignatura);

        var calificaciones = new ArrayList<Calificacion>();
        calificaciones.add(calificacion);

        var notasEstudiantesProfesorODT = new ArrayList<NotasEstudianteProfesorODT>();
        notasEstudiantesProfesorODT.add(new NotasEstudianteProfesorODT(calificacion));

        when(calificacionesRepository.findCalificacionesByProfesorId(id)).thenReturn(calificaciones);

        //Act
        var response = calificacionesService.getCalificacionesEstudiantesAndAsignaturasByIdProfesor(id);
        assertThat(notasEstudiantesProfesorODT).isEqualTo(response);
        verify(calificacionesRepository,times(1)).findCalificacionesByProfesorId(id);
    }

    @Test
    void getCalificacionesAsignaturasByIdAlumnoTest(){
        //Arrange
        int id = 1;

        var asignatura = new Asignatura();
        asignatura.setId(id);

        var alumno = new Alumno();
        alumno.setId(id);

        var calificacion = new Calificacion();
        calificacion.setId(id);
        calificacion.setIdAlumno(alumno);
        calificacion.setIdAsignatura(asignatura);

        var calificaciones = new ArrayList<Calificacion>();
        calificaciones.add(calificacion);

        var notasEstudiantesProfesorODT = new ArrayList<NotasEstudianteProfesorODT>();
        notasEstudiantesProfesorODT.add(new NotasEstudianteProfesorODT(calificacion));

        when(calificacionesRepository.findCalificacionByAlumnoId(id)).thenReturn(calificaciones);

        //Act
        var response = calificacionesService.getCalificacionesAndAsignaturasByIdAlumno(id);
        assertThat(notasEstudiantesProfesorODT).isEqualTo(response);
        verify(calificacionesRepository,times(1)).findCalificacionByAlumnoId(id);
    }
}
