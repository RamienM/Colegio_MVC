package org.prueba.calificacionesh2.business.excelManagement;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;


public class ObtainCellData {

    public static String getStringData(Row row, Integer column) throws RuntimeException {
        if (column == null) throw new RuntimeException("La columna no se ha podido encontrar");
        Cell cell = row.getCell(column);
        if (cell == null)  throw new RuntimeException("No se puede obtener el valor de la celda");

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
            default:
                throw new RuntimeException("No se puede obtener el valor de la celda");
        }
    }

    public static Float getFloatData(Row row, Integer column) throws RuntimeException {
        if (column == null) throw new RuntimeException("La columna no se ha podido encontrar");
        Cell cell = row.getCell(column);
        if (cell == null)  throw new RuntimeException("No se puede obtener el valor de la celda");

        switch (cell.getCellType()) {
            case STRING:
                return Float.valueOf(cell.getStringCellValue());
            case NUMERIC:
                // Verificamos si es un número entero (teléfono) o una fecha
                if (DateUtil.isCellDateFormatted(cell)) {
                    return Float.valueOf(cell.getDateCellValue().toString());
                } else {
                    return convertDoubleToFloat(cell.getNumericCellValue());
                }
            default:
                throw new RuntimeException("No se puede obtener el valor de la celda");
        }
    }

    private static Float convertDoubleToFloat(Double value) {
        return value == null ? null : value.floatValue();
    }
}
