package org.prueba.calificacionesh2.business.service;

import org.prueba.calificacionesh2.business.dto.ProfesorDTO;
import org.prueba.calificacionesh2.business.exception.ProfesorNotFoundException;
import org.prueba.calificacionesh2.persistence.entity.Profesor;
import org.prueba.calificacionesh2.persistence.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfesorService {
    @Autowired
    private ProfesorRepository profesorRepository;

    public List<ProfesorDTO> getAllProfesores() {
        var profesores = new ArrayList<ProfesorDTO>();
        for (Profesor profesor : profesorRepository.findAll()) {
            profesores.add(new ProfesorDTO(profesor));
        }
        return profesores;
    }
    public ProfesorDTO getProfesorById(Integer id) throws ProfesorNotFoundException {
        var profesor = profesorRepository.findById(id);
        if(profesor.isPresent()) {
            return new ProfesorDTO(profesor.get());
        }
        throw new ProfesorNotFoundException("No se ha podido encontrar el profesor con id: "+ id);
    }
    public ProfesorDTO addProfesor(ProfesorDTO profesorDTO) {
        Profesor profesor = new Profesor();
        profesor.setNombre(profesorDTO.getNombre());
        profesor.setApellido(profesorDTO.getApellido());
        profesor.setTelefono(profesorDTO.getTelefono());
        profesor.setCorreo(profesorDTO.getCorreo());
        return new ProfesorDTO(profesorRepository.save(profesor));
    }
    public ProfesorDTO updateProfesor(Integer id, ProfesorDTO profesorDTO) throws ProfesorNotFoundException {
        var profesor = profesorRepository.findById(id);
        if(profesor.isPresent()) {
            Profesor updatedProfesor = profesor.get();
            updatedProfesor.setNombre(profesorDTO.getNombre() == null ? updatedProfesor.getNombre() : profesorDTO.getNombre());
            updatedProfesor.setApellido(profesorDTO.getApellido() == null ? updatedProfesor.getApellido() : profesorDTO.getApellido());
            updatedProfesor.setCorreo(profesorDTO.getCorreo() == null ? updatedProfesor.getCorreo() : profesorDTO.getCorreo());
            updatedProfesor.setTelefono(profesorDTO.getTelefono() == null ? updatedProfesor.getTelefono() : profesorDTO.getTelefono());
            return new ProfesorDTO(profesorRepository.save(updatedProfesor));
        }
        throw new ProfesorNotFoundException("No se ha podido encontrar el profesor con id: "+ id);
    }
    public void deleteProfesor(Integer id) throws ProfesorNotFoundException {
        var profesor = profesorRepository.findById(id);
        if (profesor.isPresent()) {
            profesorRepository.delete(profesor.get());
            return;
        }
        throw new ProfesorNotFoundException("No se ha podido encontrar el profesor con id: "+ id);
    }

}
