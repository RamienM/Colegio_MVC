package org.prueba.calificacionesh2.excelManagement;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prueba.calificacionesh2.entity.Alumno;
import org.prueba.calificacionesh2.entity.Asignatura;
import org.prueba.calificacionesh2.entity.Calificacion;
import org.prueba.calificacionesh2.entity.Profesor;
import org.prueba.calificacionesh2.repository.AlumnoRepository;
import org.prueba.calificacionesh2.repository.AsignaturasRepository;
import org.prueba.calificacionesh2.repository.CalificacionesRepository;
import org.prueba.calificacionesh2.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;

@Service
public class UploadFileService {

    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private ProfesorRepository profesorRepository;
    @Autowired
    private CalificacionesRepository calificacionesRepository;
    @Autowired
    private AsignaturasRepository asignaturasRepository;


    /**
     * Permite añadir a la base de datos Alumnos a patir de un archivo Excel
     * @param file  Archivo exel que debe contener la hoja "Alumnos"
     */
    public void uploadAlumno(MultipartFile file){
        obtainAllDataFromASheet(file, "Alumnos",(row,indices)->{
            if (!alumnoRepository.existsByNombreAndCorreo(ObtainCellData.getStringData(row,indices.get("Nombre")),
                    ObtainCellData.getStringData(row,indices.get("Correo")))){
                Alumno alumno = new Alumno();
                alumno.setNombre(ObtainCellData.getStringData(row,indices.get("Nombre")));
                alumno.setApellido(ObtainCellData.getStringData(row,indices.get("Apellido")));
                alumno.setCorreo(ObtainCellData.getStringData(row,indices.get("Correo")));
                alumno.setTelefono(ObtainCellData.getStringData(row,indices.get("Telefono")));
                alumnoRepository.save(alumno);
            }
        });
    }

    /**
     * Permite añadir a la base de datos Profesores a patir de un archivo Excel
     * @param file  Archivo exel que debe contener la hoja "Profesores"
     */
    public void uploadProfesor(MultipartFile file){
        obtainAllDataFromASheet(file, "Profesores", (row, indices) -> {
            if (!profesorRepository.existsByNombreAndCorreo(ObtainCellData.getStringData(row,indices.get("Nombre")),
                    ObtainCellData.getStringData(row,indices.get("Correo")))) {
                Profesor profesor = new Profesor();
                profesor.setNombre(ObtainCellData.getStringData(row, indices.get("Nombre")));
                profesor.setApellido(ObtainCellData.getStringData(row, indices.get("Apellido")));
                profesor.setCorreo(ObtainCellData.getStringData(row, indices.get("Correo")));
                profesor.setTelefono(ObtainCellData.getStringData(row, indices.get("Telefono")));

                profesorRepository.save(profesor);
            }
        });
    }

    /**
     * Permite añadir a la base de datos Asignaturas a patir de un archivo Excel, también añade los profesores
     * que la imparten
     * @param file  Archivo exel que debe contener la hoja "Asignaturas" y en caso de ser necesario "Profesores"
     */
    public void uploadAsignatura(MultipartFile file){
        obtainAllDataFromASheet(file, "Asignaturas", (row, indices) -> {
            if (!asignaturasRepository.existsByName(ObtainCellData.getStringData(row,indices.get("NombreAsignatura")))){
                Asignatura asignatura = new Asignatura();
                asignatura.setName(ObtainCellData.getStringData(row,indices.get("NombreAsignatura")));

                var nombreProfesor = ObtainCellData.getStringData(row,indices.get("NombreProfesor"));
                var profesor = profesorRepository.findByNombre(nombreProfesor);

                if (profesor.isPresent()){
                    asignatura.setIdProfesor(profesor.get());
                }else {
                    obtainAllDataFromASheet(file,"Profesores", (rowP,indicesP) -> {
                        if (nombreProfesor.equals(ObtainCellData.getStringData(rowP,indicesP.get("Nombre")))) {
                            Profesor prof = new Profesor();
                            prof.setNombre(ObtainCellData.getStringData(rowP,indicesP.get("Nombre")));
                            prof.setApellido(ObtainCellData.getStringData(rowP,indicesP.get("Apellido")));
                            prof.setCorreo(ObtainCellData.getStringData(rowP,indicesP.get("Correo")));
                            prof.setTelefono(ObtainCellData.getStringData(rowP,indicesP.get("Telefono")));

                            profesorRepository.save(prof);
                        }
                    });

                    profesor = profesorRepository.findByNombre(nombreProfesor);
                    if (profesor.isPresent()) {
                        asignatura.setIdProfesor(profesor.get());
                    }else {
                        throw new RuntimeException("No existe un profesor con ese nombre");
                    }
                }
                asignaturasRepository.save(asignatura);
            }
        });
    }

    /**
     * Permite añadir a la base de datos Calificaciones a patir de un archivo Excel, también añade las asignaturas y
     * alumnos
     * @param file  Archivo exel que debe contener la hoja "Asignaturas" y en caso de ser necesario "Profesores"
     */
    public void uploadCalificaciones(MultipartFile file){
        obtainAllDataFromASheet(file, "Calificaciones", (row, indices) -> {
            Calificacion calificacion = new Calificacion();
            calificacion.setMark(ObtainCellData.getFloatData(row,indices.get("Nota")));
            String nombreAsignatura = ObtainCellData.getStringData(row,indices.get("NombreAsignatura"));
            var asig = asignaturasRepository.findByName(nombreAsignatura);
            if (asig.isPresent()){
                calificacion.setIdAsignatura(asig.get());
            }else{
                obtainAllDataFromASheet(file, "Asignaturas", (rowA, indicesA) -> {
                    if (nombreAsignatura.equals(ObtainCellData.getStringData(rowA,indicesA.get("NombreAsignatura")))) {
                        Asignatura asignatura = new Asignatura();
                        asignatura.setName(ObtainCellData.getStringData(rowA,indicesA.get("NombreAsignatura")));

                        var nombreProfesor = ObtainCellData.getStringData(rowA,indicesA.get("NombreProfesor"));
                        var profesor = profesorRepository.findByNombre(nombreProfesor);

                        if (profesor.isPresent()){
                            asignatura.setIdProfesor(profesor.get());
                        }else {
                            obtainAllDataFromASheet(file,"Profesores", (rowP,indicesP) -> {
                                if (nombreProfesor.equals(ObtainCellData.getStringData(rowP,indicesP.get("Nombre")))) {
                                    Profesor prof = new Profesor();
                                    prof.setNombre(ObtainCellData.getStringData(rowP,indicesP.get("Nombre")));
                                    prof.setApellido(ObtainCellData.getStringData(rowP,indicesP.get("Apellido")));
                                    prof.setCorreo(ObtainCellData.getStringData(rowP,indicesP.get("Correo")));
                                    prof.setTelefono(ObtainCellData.getStringData(rowP,indicesP.get("Telefono")));

                                    profesorRepository.save(prof);
                                }
                            });

                            profesor = profesorRepository.findByNombre(nombreProfesor);
                            if (profesor.isPresent()) {
                                asignatura.setIdProfesor(profesor.get());
                            }else {
                                throw new RuntimeException("No existe un profesor con ese nombre");
                            }
                        }
                        asignaturasRepository.save(asignatura);
                    }
                });
                asig = asignaturasRepository.findByName(nombreAsignatura);
                if (asig.isPresent()){
                    calificacion.setIdAsignatura(asig.get());
                }else{
                    throw new RuntimeException("No existe un asignatura con ese nombre");
                }
            }
            String nombreAlumno = ObtainCellData.getStringData(row,indices.get("NombreAlumno"));
            var alum = alumnoRepository.findByNombre(nombreAlumno);
            if (alum.isPresent()){
                calificacion.setIdAlumno(alum.get());
            }else {
                obtainAllDataFromASheet(file,"Alumnos", (rowA,indicesA) -> {
                    if (nombreAlumno.equals(ObtainCellData.getStringData(rowA,indicesA.get("Nombre")))) {
                        Alumno alumno = new Alumno();
                        alumno.setNombre(ObtainCellData.getStringData(rowA,indicesA.get("Nombre")));
                        alumno.setApellido(ObtainCellData.getStringData(rowA,indicesA.get("Apellido")));
                        alumno.setCorreo(ObtainCellData.getStringData(rowA,indicesA.get("Correo")));
                        alumno.setTelefono(ObtainCellData.getStringData(rowA,indicesA.get("Telefono")));

                        alumnoRepository.save(alumno);
                    }
                });
                alum = alumnoRepository.findByNombre(nombreAlumno);
                if (alum.isPresent()){
                    calificacion.setIdAlumno(alum.get());
                }else {
                    throw new RuntimeException("No existe un alumno con ese nombre");
                }
            }

            if (! calificacionesRepository.existsByIdAlumnoAndIdAsignatura(calificacion.getIdAlumno(),
                    calificacion.getIdAsignatura())){
                calificacionesRepository.save(calificacion);
            }
        });
    }


    /**
     * Obtiene todos lo datos de una hoja de Excel
     * @param file          Archivo excel
     * @param sheetName     Nombre de la hoja que hay que leer
     * @param entityMapper  Para el uso de expresiones lambda, se pide fila (Row) y indices (Map<Strig,Intger>)
     */
    private void obtainAllDataFromASheet(MultipartFile file, String sheetName, BiConsumer<Row, Map<String, Integer>> entityMapper) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("La hoja '" + sheetName + "' no existe en el fichero");
            }

            Map<String, Integer> indices = new HashMap<>();
            Row header = sheet.getRow(0);
            header.forEach(cell -> indices.put(cell.getStringCellValue(), cell.getColumnIndex()));

            Row row;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                if (row == null) continue;

                entityMapper.accept(row, indices);
            }

        } catch (Exception e) {
            System.err.println("Error en la lectura del archivo");
        }
    }

}
