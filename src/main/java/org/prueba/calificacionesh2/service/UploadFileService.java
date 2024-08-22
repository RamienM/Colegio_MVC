package org.prueba.calificacionesh2.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prueba.calificacionesh2.dto.AlumnoDTO;
import org.prueba.calificacionesh2.dto.AsignaturaDTO;
import org.prueba.calificacionesh2.dto.ProfesorDTO;
import org.prueba.calificacionesh2.entity.Calificacion;
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

@Service
public class UploadFileService {

    @Autowired
    private AlumnoService alumnoService;
    @Autowired
    private ProfesorService profesorService;
    @Autowired
    private ProfesorRepository profesorRepository;
    @Autowired
    private AsignaturasService asignaturasService;
    @Autowired
    private CalificacionesRepository calificacionesRepository;
    @Autowired
    private AsignaturasRepository asignaturasRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;

    public void uploadAlumno(MultipartFile file){

        try(InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet("Alumnos");
            if (sheet == null) {
                throw new RuntimeException("La hoja 'Alumnos' no existe en el fichero");
            }

            Map<String, Integer> indices = new HashMap<>();
            Row header = sheet.getRow(0);
            header.forEach(cell -> indices.put(cell.getStringCellValue(),cell.getColumnIndex()));

            AlumnoDTO alumno;
            Row row;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                if (row == null) continue;
                alumno = new AlumnoDTO();
                alumno.setNombre(getCellValue(row,indices,"Nombre"));
                alumno.setApellido(getCellValue(row,indices,"Apellido"));
                alumno.setCorreo(getCellValue(row,indices,"Correo"));
                alumno.setTelefono(getCellValue(row,indices,"Telefono"));

                alumnoService.addAlumno(alumno);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadProfesor(MultipartFile file){

        try(InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet("Profesores");
            if (sheet == null) {
                throw new RuntimeException("La hoja 'Profesores' no existe en el fichero");
            }

            Map<String, Integer> indices = new HashMap<>();
            Row header = sheet.getRow(0);
            header.forEach(cell -> indices.put(cell.getStringCellValue(),cell.getColumnIndex()));

            ProfesorDTO profesor;
            Row row;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                if (row == null) continue;
                profesor = new ProfesorDTO();
                profesor.setNombre(getCellValue(row,indices,"Nombre"));
                profesor.setApellido(getCellValue(row,indices,"Apellido"));
                profesor.setCorreo(getCellValue(row,indices,"Correo"));
                profesor.setTelefono(getCellValue(row,indices,"Telefono"));

                profesorService.addProfesor(profesor);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadAsignatura(MultipartFile file){

        try(InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet("Asignaturas");
            if (sheet == null) {
                throw new RuntimeException("La hoja 'Asignaturas' no existe en el fichero");
            }

            Map<String, Integer> indices = new HashMap<>();
            Row header = sheet.getRow(0);
            header.forEach(cell -> indices.put(cell.getStringCellValue(),cell.getColumnIndex()));

            AsignaturaDTO asignatura;
            Row row;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                if (row == null) continue;
                asignatura = new AsignaturaDTO();
                asignatura.setNombreAsignatura(getCellValue(row,indices,"NombreAsignatura"));
                var nombreProfesor = getCellValue(row,indices,"NombreProfesor");
                var profesor = profesorRepository.findByNombre(nombreProfesor);
                if (profesor.isPresent()){
                    asignatura.setIdProfesor(profesor.get().getId());
                }else {
                   List<Row> datosProfesor = findDatos("Profesores","Nombre",nombreProfesor,file);
                   if (datosProfesor.size() == 2){
                       Map<String, Integer> indicesP = new HashMap<>();
                       datosProfesor.get(0).forEach(cell -> indicesP.put(cell.getStringCellValue(),cell.getColumnIndex()));

                       var prof = new ProfesorDTO();
                       prof.setNombre(getCellValue(datosProfesor.get(1),indicesP,"Nombre"));
                       prof.setApellido(getCellValue(datosProfesor.get(1),indicesP,"Apellido"));
                       prof.setCorreo(getCellValue(datosProfesor.get(1),indicesP,"Correo"));
                       prof.setTelefono(getCellValue(datosProfesor.get(1),indicesP,"Telefono"));

                       prof = profesorService.addProfesor(prof);

                       asignatura.setIdProfesor(prof.getId());
                   }else{
                       throw new RuntimeException();
                   }

                }
                asignaturasService.addAsignatura(asignatura);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void uploadCalificaciones(MultipartFile file){

        try(InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet("Calificaciones");
            if (sheet == null) {
                throw new RuntimeException("La hoja 'Calificaciones' no existe en el fichero");
            }

            Map<String, Integer> indices = new HashMap<>();
            Row header = sheet.getRow(0);
            header.forEach(cell -> indices.put(cell.getStringCellValue(),cell.getColumnIndex()));

            Calificacion calificacion;
            Row row;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                if (row == null) continue;
                calificacion = new Calificacion();
                calificacion.setMark(Float.parseFloat(getCellValue(row,indices,"Nota")));
                var asig = asignaturasRepository.findByNombre(getCellValue(row,indices,"NombreAsignatura"));
                if (asig.isPresent()){
                    calificacion.setIdAsignatura(asig.get());
                }else{
                    throw new RuntimeException("La asignatura no existe en el fichero");
                }
                var alum = alumnoRepository.findByNombre(getCellValue(row,indices,"NombreAlumno"));
                if (alum.isPresent()){
                    calificacion.setIdAlumno(alum.get());
                }else {
                    throw new RuntimeException("El alumno no existe en el fichero");
                }

                calificacionesRepository.save(calificacion);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<Row> findDatos(String hoja, String nombreColumna, String datoAComparar, MultipartFile file){
        try(InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            List<Row> datos = new ArrayList<>();
            Sheet sheet = workbook.getSheet(hoja);
            if (sheet == null) {
                throw new RuntimeException("La hoja " + hoja + " no existe en el fichero");
            }

            Map<String, Integer> indices = new HashMap<>();
            Row header = sheet.getRow(0);
            datos.add(header);
            header.forEach(cell -> indices.put(cell.getStringCellValue(), cell.getColumnIndex()));

            for (Row row : sheet) {
                if (row == null) continue;
                if (row.getCell(indices.get(nombreColumna)).getStringCellValue().equals(datoAComparar)){
                    datos.add(row);
                    return datos;
                }
            }
        }catch (Exception e){}
        return null;
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

}
