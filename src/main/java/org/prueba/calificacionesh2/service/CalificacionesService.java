package org.prueba.calificacionesh2.service;

import org.prueba.calificacionesh2.dto.CalificacionDTO;
import org.prueba.calificacionesh2.dto.NotasEstudianteProfesorODT;
import org.prueba.calificacionesh2.entity.Calificacion;
import org.prueba.calificacionesh2.exception.AlumnoNotFoundException;
import org.prueba.calificacionesh2.exception.AsignaturaNotFoundException;
import org.prueba.calificacionesh2.exception.CalificacionNotFoundException;
import org.prueba.calificacionesh2.repository.AlumnoRepository;
import org.prueba.calificacionesh2.repository.AsignaturasRepository;
import org.prueba.calificacionesh2.repository.CalificacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalificacionesService {
    @Autowired
    private CalificacionesRepository calificacionesRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private AsignaturasRepository asignaturasRepository;

    public List<CalificacionDTO> getAllCalificaciones(){
        var calificaciones = new ArrayList<CalificacionDTO>();
        for(var calificacion : calificacionesRepository.findAll()){
            calificaciones.add(new CalificacionDTO(calificacion));
        }
        return calificaciones;
    }

    public CalificacionDTO getCalificacionesById(Integer id) throws CalificacionNotFoundException {
        var calificacion = calificacionesRepository.findById(id);
        if(calificacion.isPresent()){
            return new CalificacionDTO(calificacion.get());
        }
        throw new CalificacionNotFoundException("No se ha podido encontrar la calificacion con el id: " + id);
    }

    public CalificacionDTO addCalificaciones(CalificacionDTO calificacionDTO) throws AlumnoNotFoundException, AsignaturaNotFoundException {
        var alumno = alumnoRepository.findById(calificacionDTO.getIdAlumno());
        var asignatura = asignaturasRepository.findById(calificacionDTO.getIdAsignatura());
        if(alumno.isPresent()){
            if(asignatura.isPresent()){
                var calificacion = new Calificacion();
                calificacion.setMark(calificacionDTO.getMark());
                calificacion.setIdAlumno(alumno.get());
                calificacion.setIdAsignatura(asignatura.get());
                return new CalificacionDTO(calificacionesRepository.save(calificacion));
            }
            throw new AsignaturaNotFoundException("No se ha podido encontrar la asignatura con el id: " + calificacionDTO.getIdAsignatura());
        }
        throw new AlumnoNotFoundException("No se ha encontrado el alumno con id: " + calificacionDTO.getIdAlumno());
    }

    public CalificacionDTO updateCalificaciones(Integer id, CalificacionDTO calificacionDTO) throws CalificacionNotFoundException, AsignaturaNotFoundException, AlumnoNotFoundException{
        var calificacion = calificacionesRepository.findById(id);
        if(calificacion.isPresent()){
            Calificacion updateCalificacion = calificacion.get();
            updateCalificacion.setMark(calificacionDTO.getMark() == null ? updateCalificacion.getMark() : calificacionDTO.getMark());

            if(calificacionDTO.getIdAsignatura() != null){
                var asignatura = asignaturasRepository.findById(calificacionDTO.getIdAsignatura());
                if(asignatura.isPresent()){
                    updateCalificacion.setIdAsignatura(asignatura.get());
                }else{
                    throw new AsignaturaNotFoundException("No se ha podido encontrar la asignatura con el id: " + calificacionDTO.getIdAsignatura());
                }
            }

            if(calificacionDTO.getIdAlumno() != null){
                var alumno = alumnoRepository.findById(calificacionDTO.getIdAlumno());
                if(alumno.isPresent()){
                    updateCalificacion.setIdAlumno(alumno.get());
                }else{
                    throw new AlumnoNotFoundException("No se ha encontrado el alumno con id: " + calificacionDTO.getIdAlumno());
                }
            }

            return new CalificacionDTO(calificacionesRepository.save(updateCalificacion));
        }
        throw new CalificacionNotFoundException("No se ha podido encontrar la calificacion con el id: " + id);
    }

    public void deleteCalificaciones(Integer id) throws CalificacionNotFoundException {
        var calificacion = calificacionesRepository.findById(id);
        if(calificacion.isPresent()){
            calificacionesRepository.delete(calificacion.get());
            return;
        }
        throw new CalificacionNotFoundException("No se ha podido encontrar la calificacion con el id: " + id);
    }

    public List<NotasEstudianteProfesorODT> getCalificacionesEstudiantesAndAsignaturasByIdProfesor(Integer idProfesor){
        var calificaciones = calificacionesRepository.findCalificacionesByProfesorId(idProfesor);
        if(calificaciones.isEmpty()) return new ArrayList<>();

        var notasEstudiantes = new ArrayList<NotasEstudianteProfesorODT>();
        NotasEstudianteProfesorODT notaEstudiante;

        for(var calificacion : calificaciones){
            notaEstudiante = new NotasEstudianteProfesorODT(calificacion);

            notasEstudiantes.add(notaEstudiante);
        }

        return notasEstudiantes;
    }

    public List<NotasEstudianteProfesorODT> getCalificacionesAndAsignaturasByIdAlumno(Integer idAlumno){
        var calificaciones = calificacionesRepository.findCalificacionByAlumnoId(idAlumno);
        if(calificaciones.isEmpty()) return new ArrayList<>();

        var notasEstudiantes = new ArrayList<NotasEstudianteProfesorODT>();
        NotasEstudianteProfesorODT notaEstudiante;

        for(var calificacion : calificaciones){
            notaEstudiante = new NotasEstudianteProfesorODT(calificacion);

            notasEstudiantes.add(notaEstudiante);
        }

        return notasEstudiantes;
    }
}
