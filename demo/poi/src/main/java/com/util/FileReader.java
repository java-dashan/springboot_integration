package com.util;

import com.entity.Field;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//@Slf4j
public class FileReader {
    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    public static Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    public static List<Field> readFile(String path) {
        Workbook workbook = null;
        FileInputStream inputStream = null;

        File file = new File(path);

        if (!file.exists()) {
            return null;
        }

        String fileType = path.substring(path.lastIndexOf(".") + 1, path.length());
        try {
            inputStream = new FileInputStream(file);
            // 获取Excel工作簿
            workbook = getWorkbook(inputStream,fileType);

            // 读取excel中的数据
            List<Field> resultDataList = parseExcel(workbook);

            return resultDataList;

        } catch (Exception e) {
            System.out.println("出错！错误信息：" + e.getMessage());
            return null;
        }finally {
            try {
                if (null != workbook) {
                    workbook.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                System.out.println("关闭数据流出错！错误信息：" + e.getMessage());
                return null;
            }
        }

    }

    private static List<Field> parseExcel(Workbook workbook) {
        List<Field> list = new ArrayList<>();
        int numberOfSheets = workbook.getNumberOfSheets();
        System.out.println(numberOfSheets);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            int firstRowNum = sheet.getFirstRowNum();
//            sheet.getRow(firstRowNum)
            Row row = null;

            System.out.println(sheet.getLastRowNum());
            for (int j = 0; j < sheet.getLastRowNum()+1; j++) {
                row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                int cellNum = 0;
                Field field = new Field();
                Cell cell = row.getCell(cellNum++);
                field.setFieldName(convertCellValueToString(cell));
                cell = row.getCell(cellNum++);
                field.setFieldType(convertCellValueToString(cell));
                cell = row.getCell(cellNum++);
                field.setFieldLong(convertCellValueToString(cell));
                cell = row.getCell(cellNum++);
                field.setDefaultValue(convertCellValueToString(cell));
                cell = row.getCell(cellNum++);
                field.setFieldComment(convertCellValueToString(cell));
                list.add(field);
            }
        }
        return list;
    }

    private static String convertCellValueToString(Cell cell) {
        if(cell==null){
            return null;
        }
        String returnValue = null;
        switch (cell.getCellType()) {
            case NUMERIC:   //数字
                Double doubleValue = cell.getNumericCellValue();

                // 格式化科学计数法，取一位整数
                DecimalFormat df = new DecimalFormat("0");
                returnValue = df.format(doubleValue);
                break;
            case STRING:    //字符串
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN:   //布尔
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            case BLANK:     // 空值
                break;
            case FORMULA:   // 公式
                returnValue = cell.getCellFormula();
                break;
            case ERROR:     // 故障
                break;
            default:
                break;
        }
        return returnValue;
    }
}
