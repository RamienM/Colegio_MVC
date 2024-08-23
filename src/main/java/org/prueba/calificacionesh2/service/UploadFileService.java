package org.prueba.calificacionesh2.service;

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
import java.math.BigDecimal;
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
            Alumno alumno = new Alumno();
            alumno.setNombre(getCellValue(row, indices, "Nombre"));
            alumno.setApellido(getCellValue(row, indices, "Apellido"));
            alumno.setCorreo(getCellValue(row, indices, "Correo"));
            alumno.setTelefono(getCellValue(row, indices, "Telefono"));

            alumnoRepository.save(alumno);
        });
    }

    /**
     * Permite añadir a la base de datos Profesores a patir de un archivo Excel
     * @param file  Archivo exel que debe contener la hoja "Profesores"
     */
    public void uploadProfesor(MultipartFile file){
        obtainAllDataFromASheet(file, "Profesores", (row, indices) -> {
            Profesor profesor = new Profesor();
            profesor.setNombre(getCellValue(row, indices, "Nombre"));
            profesor.setApellido(getCellValue(row, indices, "Apellido"));
            profesor.setCorreo(getCellValue(row, indices, "Correo"));
            profesor.setTelefono(getCellValue(row, indices, "Telefono"));

            profesorRepository.save(profesor);
        });
    }

    /**
     * Permite añadir a la base de datos Asignaturas a patir de un archivo Excel, también añade los profesores
     * que la imparten
     * @param file  Archivo exel que debe contener la hoja "Asignaturas" y en caso de ser necesario "Profesores"
     */
    public void uploadAsignatura(MultipartFile file){
        obtainAllDataFromASheet(file, "Asignaturas", (row, indices) -> {
            Asignatura asignatura = new Asignatura();
            asignatura.setName(getCellValue(row,indices,"NombreAsignatura"));

            var nombreProfesor = getCellValue(row,indices,"NombreProfesor");
            var profesor = profesorRepository.findByNombre(nombreProfesor);

            if (profesor.isPresent()){
                asignatura.setIdProfesor(profesor.get());
            }else {
                obtainAllDataFromASheet(file,"Profesores", (rowP,indicesP) -> {
                    if (nombreProfesor.equals(getCellValue(rowP,indicesP,"Nombre"))) {
                        Profesor prof = new Profesor();
                        prof.setNombre(getCellValue(rowP,indicesP,"Nombre"));
                        prof.setApellido(getCellValue(rowP,indicesP, "Apellido"));
                        prof.setCorreo(getCellValue(rowP,indicesP, "Correo"));
                        prof.setTelefono(getCellValue(rowP,indicesP, "Telefono"));

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
            calificacion.setMark(Float.parseFloat(getCellValue(row,indices,"Nota")));

            String nombreAsignatura = getCellValue(row,indices,"NombreAsignatura");
            var asig = asignaturasRepository.findByName(nombreAsignatura);
            if (asig.isPresent()){
                calificacion.setIdAsignatura(asig.get());
            }else{
                obtainAllDataFromASheet(file, "Asignaturas", (rowA, indicesA) -> {
                    if (nombreAsignatura.equals(getCellValue(rowA,indicesA,"NombreAsignatura"))) {
                        Asignatura asignatura = new Asignatura();
                        asignatura.setName(getCellValue(rowA,indicesA,"NombreAsignatura"));

                        var nombreProfesor = getCellValue(rowA,indicesA,"NombreProfesor");
                        var profesor = profesorRepository.findByNombre(nombreProfesor);

                        if (profesor.isPresent()){
                            asignatura.setIdProfesor(profesor.get());
                        }else {
                            obtainAllDataFromASheet(file,"Profesores", (rowP,indicesP) -> {
                                if (nombreProfesor.equals(getCellValue(rowP,indicesP,"Nombre"))) {
                                    Profesor prof = new Profesor();
                                    prof.setNombre(getCellValue(rowP,indicesP,"Nombre"));
                                    prof.setApellido(getCellValue(rowP,indicesP, "Apellido"));
                                    prof.setCorreo(getCellValue(rowP,indicesP, "Correo"));
                                    prof.setTelefono(getCellValue(rowP,indicesP, "Telefono"));

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
            String nombreAlumno = getCellValue(row,indices,"NombreAlumno");
            var alum = alumnoRepository.findByNombre(nombreAlumno);
            if (alum.isPresent()){
                calificacion.setIdAlumno(alum.get());
            }else {
                obtainAllDataFromASheet(file,"Alumnos", (rowA,indicesA) -> {
                    if (nombreAlumno.equals(getCellValue(rowA,indicesA,"Nombre"))) {
                        Alumno alumno = new Alumno();
                        alumno.setNombre(getCellValue(rowA,indicesA,"Nombre"));
                        alumno.setApellido(getCellValue(rowA,indicesA, "Apellido"));
                        alumno.setCorreo(getCellValue(rowA,indicesA, "Correo"));
                        alumno.setTelefono(getCellValue(rowA,indicesA, "Telefono"));

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

            calificacionesRepository.save(calificacion);
        });
    }

    private String getCellValue(Row row, Map<String, Integer> columnIndices, String columnName) {
        Integer columnIndex = columnIndices.get(columnName);
        if (columnIndex == null) {
            throw new RuntimeException("Columna '" + columnName + "' no encontrada.");
        }
        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Verificamos si es un número entero (teléfono) o una fecha
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convertir el valor numérico a un string sin notación científica
                    return BigDecimal.valueOf(cell.getNumericCellValue())
                            .toPlainString();
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
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
