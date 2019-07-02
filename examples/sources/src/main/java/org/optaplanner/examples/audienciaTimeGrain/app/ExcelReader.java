package org.optaplanner.examples.audienciaTimeGrain.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader<rowIt> {

    public void readExcelFile(){
        File excelFile = new File("src/main/java/org/optaplanner/examples/audienciaTimeGrain/app/test_1.xlsx");
        FileInputStream fis = null;

        {
            try {
                fis = new FileInputStream(excelFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        XSSFWorkbook workbook = null;

        {
            try {
                workbook = new XSSFWorkbook(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        XSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIt = sheet.iterator();

        while(rowIt.hasNext()){
            Row row = rowIt.next();

            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                System.out.println(cell.toString() + ';');
            }
        }
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
