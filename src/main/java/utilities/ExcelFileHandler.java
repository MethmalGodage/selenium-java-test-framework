package utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelFileHandler {

    XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private XSSFSheet connectToDataSheet(String path, String fileName, int sheetNo) {
        try {
            File excelFile = new File(System.getProperty("user.dir") + path + fileName);

            FileInputStream file = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheetAt(sheetNo);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sheet;
    }

    public List<String> readExcelSheetColumn(String path, String fileName, int sheetNo, int columnNo) {
        List<String> dataListFromExcelFile = new ArrayList<>();

        try {
            sheet = connectToDataSheet(path, fileName, sheetNo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Traversing over each row of XLSX file
        for (Row row : sheet) {
            DataFormatter formatter = new DataFormatter();
            Cell cell = row.getCell(columnNo);
            String validatingRow = formatter.formatCellValue(cell);

            if (row.getRowNum() == 0 || validatingRow.isEmpty()) {
                continue;
            }
            dataListFromExcelFile.add(validatingRow);

        }
        return dataListFromExcelFile;
    }

    public ArrayList<String> readExcelSheetRow(String path, String fileName, int sheetNo, int rowNumber) {

        ArrayList<String> dataList = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        Cell cell;
        String validateRow = "";
        int i = 0;

        sheet = connectToDataSheet(path, fileName, sheetNo);
        Row row = sheet.getRow(rowNumber);

        while (!validateRow.isEmpty() || i == 0) {
            cell = row.getCell(i);
            validateRow = formatter.formatCellValue(cell);
            if (!validateRow.equals("")) {
                dataList.add(validateRow);
            }
            i++;
        }

        return dataList;
    }

    public String readExcelSheetCell(String path, String fileName, int sheetNo, String cellAddress) {

        Cell cell = getCellObject(path, fileName, sheetNo, cellAddress);
        return cell.getStringCellValue();

    }

    public boolean writeExcelSheetCell(String path, String fileName, int sheetNo, String cellAddress, String value) throws IOException {

        Cell cell = getCellObject(path, fileName, sheetNo, cellAddress);
        cell.setCellValue(value);

        FileOutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + path + "/" + fileName);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

        return readExcelSheetCell(path, fileName, sheetNo, cellAddress).equals(value);

    }

    private Cell getCellObject(String path, String fileName, int sheetNo, String cellAddress) {

        sheet = connectToDataSheet(path, fileName, sheetNo);

        ArrayList<String> rowAndColumnNumbers = getColumnAndRowNumbers(cellAddress);
        int rowNumber = Integer.parseInt(rowAndColumnNumbers.get(0));
        int columnNumber = Integer.parseInt(rowAndColumnNumbers.get(1));

        return sheet.getRow(rowNumber).getCell(columnNumber);

    }

    private ArrayList<String> getColumnAndRowNumbers(String cellAddress) {

        String rowNumber = cellAddress.substring(1);
        String columnLetter = String.valueOf(cellAddress.charAt(0));
        String columnNumber = String.valueOf(convertColumnLetterToNumber(columnLetter));

        ArrayList<String> numbers = new ArrayList<>();
        numbers.add(rowNumber);
        numbers.add(columnNumber);

        return numbers;
    }

    private int convertColumnLetterToNumber(String letter) {
        return CellReference.convertColStringToIndex(letter);
    }

}
