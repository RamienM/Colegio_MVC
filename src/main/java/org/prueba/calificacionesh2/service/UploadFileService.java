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
                //Si no existe el profesor lo añade
                List<Row> datosProfesor = findDatosByColunm("Profesores","Nombre",nombreProfesor,file);
                if (datosProfesor.size() == 2){
                    Map<String, Integer> indicesP = new HashMap<>();
                    datosProfesor.get(0).forEach(cell -> indicesP.put(cell.getStringCellValue(),cell.getColumnIndex()));

                    var prof = new Profesor();
                    prof.setNombre(getCellValue(datosProfesor.get(1),indicesP,"Nombre"));
                    prof.setApellido(getCellValue(datosProfesor.get(1),indicesP,"Apellido"));
                    prof.setCorreo(getCellValue(datosProfesor.get(1),indicesP,"Correo"));
                    prof.setTelefono(getCellValue(datosProfesor.get(1),indicesP,"Telefono"));

                    prof = profesorRepository.save(prof);
                    asignatura.setIdProfesor(prof);
                }else{
                    throw new RuntimeException("No se ha encontrado el profesor");
                }
            }
            asignaturasRepository.save(asignatura);
        });
    }


    public void uploadCalificaciones(MultipartFile file){
        obtainAllDataFromASheet(file, "Calificaciones", (row, indices) -> {
            Calificacion calificacion = new Calificacion();
            calificacion.setMark(Float.parseFloat(getCellValue(row,indices,"Nota")));

            var asig = asignaturasRepository.findByName(getCellValue(row,indices,"NombreAsignatura"));
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
        });
    }

    /**
     * Permite buscar, si existe, un dato en una columna especifica de una hoja concreta en un excel.
     * @param hoja              Hoja donde de debe buscar la columna
     * @param nombreColumna     Columna donde se espera encontrar el dato
     * @param datoAComparar     Dato que deseamos buscar
     * @param file              Fichero Excel
     * @return  Devuelve un ArrayList que contiene tanto la fila de las columnas como la fila donde se ha encontrado el atributo
     */
    private List<Row> findDatosByColunm(String hoja, String nombreColumna, String datoAComparar, MultipartFile file){
        ArrayList<Row> datos = new ArrayList<>();
        try(InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
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
        }catch (Exception e){
            System.err.println("Error en la lectura del fichero");
        }
        return datos;
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
