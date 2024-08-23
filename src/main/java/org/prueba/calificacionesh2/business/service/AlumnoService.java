package org.prueba.calificacionesh2.business.service;

import org.prueba.calificacionesh2.business.dto.AlumnoDTO;
import org.prueba.calificacionesh2.persistence.entity.Alumno;
import org.prueba.calificacionesh2.business.exception.AlumnoNotFoundException;
import org.prueba.calificacionesh2.persistence.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlumnoService {
    @Autowired
    private AlumnoRepository alumnoRepository;


    public List<AlumnoDTO> getAllAlumnos() {
        var alumnos = new ArrayList<AlumnoDTO>();
        for (var alumno : alumnoRepository.findAll()) {
            alumnos.add(new AlumnoDTO(alumno));
        }
        return alumnos;
    }

    public AlumnoDTO getAlumnoById(Integer id) throws AlumnoNotFoundException {
        var optional = alumnoRepository.findById(id);

        if (optional.isPresent()) { //Controlar con exception
            return new AlumnoDTO(optional.get());
        }else{
            throw new AlumnoNotFoundException("No se ha podido encontrar el alumno con id: " + id);
        }
    }
    public AlumnoDTO addAlumno(AlumnoDTO alumnoDTO) {
        var alumno = new Alumno();
        alumno.setNombre(alumnoDTO.getNombre());
        alumno.setApellido(alumnoDTO.getApellido());
        alumno.setTelefono(alumnoDTO.getTelefono());
        alumno.setCorreo(alumnoDTO.getCorreo());

        return new AlumnoDTO(alumnoRepository.save(alumno));
    }
    public AlumnoDTO updateAlumno(Integer id, AlumnoDTO alumnoDTO) throws AlumnoNotFoundException {
        var exitsAlumno = alumnoRepository.findById(id);
        if (exitsAlumno.isPresent()) { //Realizar con una exception
            Alumno updatedAlumno = exitsAlumno.get();
            updatedAlumno.setNombre(alumnoDTO.getNombre() == null ? updatedAlumno.getNombre() : alumnoDTO.getNombre());
            updatedAlumno.setApellido(alumnoDTO.getApellido()== null ? updatedAlumno.getApellido() : alumnoDTO.getApellido());
            updatedAlumno.setCorreo(alumnoDTO.getCorreo() == null ? updatedAlumno.getCorreo() : alumnoDTO.getCorreo());
            updatedAlumno.setTelefono(alumnoDTO.getTelefono() == null ? updatedAlumno.getTelefono() : alumnoDTO.getTelefono());
            return new AlumnoDTO(alumnoRepository.save(updatedAlumno));
        }else{
            throw new AlumnoNotFoundException("No se ha podido encontrar el alumno con id: " + id);
        }
    }
    public void deleteAlumno(Integer id) throws AlumnoNotFoundException {
        var alumno = alumnoRepository.findById(id);
        if (alumno.isPresent()) {
            alumnoRepository.delete(alumno.get());
        }else{
            throw new AlumnoNotFoundException("No se ha podido encontrar el alumno con id: " + id);
        }
    }
}
