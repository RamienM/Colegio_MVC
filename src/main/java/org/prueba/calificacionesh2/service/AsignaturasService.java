package org.prueba.calificacionesh2.service;

import org.prueba.calificacionesh2.dto.AsignaturaDTO;
import org.prueba.calificacionesh2.entity.Asignatura;
import org.prueba.calificacionesh2.exception.AsignaturaNotFoundException;
import org.prueba.calificacionesh2.exception.ProfesorNotFoundException;
import org.prueba.calificacionesh2.repository.AsignaturasRepository;
import org.prueba.calificacionesh2.repository.CalificacionesRepository;
import org.prueba.calificacionesh2.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AsignaturasService {
    @Autowired
    private AsignaturasRepository asignaturasRepository;
    @Autowired
    private ProfesorRepository profesorRepository;
    @Autowired
    private CalificacionesRepository calificacionesRepository;

    public List<AsignaturaDTO> getAllAsignaturas() {
        var asignaturasDTO = new ArrayList<AsignaturaDTO>();
        for (Asignatura asignatura : asignaturasRepository.findAll()) {
            asignaturasDTO.add(new AsignaturaDTO(asignatura));
        }
        return asignaturasDTO;
    }
    public AsignaturaDTO getAsignaturaById(Integer id) throws AsignaturaNotFoundException {
        var optionalAsignatura = asignaturasRepository.findById(id);
        if (optionalAsignatura.isPresent()) { //En caso contrario devolver exception
            return new AsignaturaDTO(optionalAsignatura.get());
        }
        throw new AsignaturaNotFoundException("No se ha podido encontrar la asignatura con el id: " + id);
    }
    public AsignaturaDTO addAsignatura(AsignaturaDTO asignaturaDTO) throws ProfesorNotFoundException {
        var optinalProfesor = profesorRepository.findById(asignaturaDTO.getIdProfesor());
        if (optinalProfesor.isPresent()) { //En caso contrarios devolver exception
            var asignatura = new Asignatura();
            asignatura.setName(asignaturaDTO.getNombreAsignatura());
            asignatura.setIdProfesor(optinalProfesor.get());
            return new AsignaturaDTO(asignaturasRepository.save(asignatura));
        }
        throw new ProfesorNotFoundException("No se ha podido encontrar el profesor con id: "+ asignaturaDTO.getIdProfesor());
    }
    public AsignaturaDTO updateAsignatura(Integer id, AsignaturaDTO asignaturaDTO) throws AsignaturaNotFoundException, ProfesorNotFoundException {
        Optional<Asignatura> exitsAsignatura = asignaturasRepository.findById(id);
        if (exitsAsignatura.isPresent()) {
            Asignatura updatedAsignatura = exitsAsignatura.get();
            updatedAsignatura.setName(asignaturaDTO.getNombreAsignatura() == null ? updatedAsignatura.getName() : asignaturaDTO.getNombreAsignatura());
            if(asignaturaDTO.getIdProfesor() != null) {
                var profesor = profesorRepository.findById(asignaturaDTO.getIdProfesor());
                if (profesor.isPresent()) {
                    updatedAsignatura.setIdProfesor(profesor.get());
                }else {
                    throw new ProfesorNotFoundException("No se ha podido encontrar el profesor con id: "+ asignaturaDTO.getIdProfesor());
                }
            }

            return new AsignaturaDTO(asignaturasRepository.save(updatedAsignatura));
        }
        throw new AsignaturaNotFoundException("No se ha podido encontrar la asignatura con el id: " + id);
    }
    public void deleteAsignatura(Integer id) throws AsignaturaNotFoundException {
        var asignatura = asignaturasRepository.findById(id);
        if (asignatura.isPresent()){
            asignaturasRepository.delete(asignatura.get());
            return;
        }
        throw new AsignaturaNotFoundException("No se ha podido encontrar la asignatura con el id: " + id);
    }
}
