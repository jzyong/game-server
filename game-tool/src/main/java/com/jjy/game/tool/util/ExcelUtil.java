package com.jjy.game.tool.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.DBObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * excel读写工具类
 *
 * @author JiangZhiYong
 */
public class ExcelUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);
    public final static String xls = "xls";
    public final static String xlsx = "xlsx";

    /**
     * 读取Excel
     *
     * @param filePath
     * @param sheetName
     * @return 属性名称列表、字段类型、描述说明，数据内容
     */
    public static Args.Four<List<String>, List<String>, List<String>, List<List<Object>>> readExcel(String filePath, String sheetName) throws Exception {
        Workbook workBook = getWorkBook(filePath);
        if (workBook == null) {
            return null;
        }
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            return null;
        }

        List<String> fieldList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> descList = new ArrayList<>();

        //前三行为元数据
        for (int i = 0; i < 3; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            int lastCellNum = row.getPhysicalNumberOfCells();
            for (int j = 0; j < lastCellNum; j++) {
                String value = row.getCell(j).toString();
                switch (i) {
                    case 0:
                        fieldList.add(value);
                        break;
                    case 1:
                        typeList.add(value);
                        break;
                    default:
                        descList.add(value);
                        break;
                }
            }
        }
        //数据
        List<List<Object>> dataList = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 3; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                return null;
            }
            //int lastCellNum = row.getPhysicalNumberOfCells();
            int lastCellNum = fieldList.size();
            List<Object> datas = new ArrayList<>();
            for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                Object object = getCellValue(cell, typeList.get(j));
                datas.add(object);
            }
            dataList.add(datas);
        }

        workBook.close();
        return Args.of(fieldList, typeList, descList, dataList);
    }

    /**
     * 获取表头元数据
     *
     * @param filePath
     * @return 属性名称列表、字段类型、描述说明
     */
    public static Args.Three<List<String>, List<String>, List<String>> getMetaData(String filePath, String sheetName) throws Exception {
        Workbook workBook = getWorkBook(filePath);
        if (workBook == null) {
            return null;
        }
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            return null;
        }

        List<String> fieldList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> descList = new ArrayList<>();

        //前三行为元数据
        for (int i = 0; i < 3; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            int lastCellNum = row.getPhysicalNumberOfCells();
            for (int j = 0; j < lastCellNum; j++) {
                String value = row.getCell(j).toString();
                switch (i) {
                    case 0:
                        fieldList.add(value);
                        break;
                    case 1:
                        typeList.add(value);
                        break;
                    default:
                        descList.add(value);
                        break;
                }
            }
        }
        workBook.close();
        return Args.of(fieldList, typeList, descList);
    }

    /**
     * 获取表单名称
     *
     * @param filePath Excel完整路径
     * @return
     */
    public static List<String> getSheetNames(String filePath) throws Exception {
        List<String> sheetNames = new ArrayList<>();
        try {

            Workbook workBook = getWorkBook(filePath);
            if (workBook == null) {
                return null;
            }
            //String[] sheetNames = new String[workBook.getNumberOfSheets()];

            for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
                sheetNames.add(workBook.getSheetAt(i).getSheetName());
            }
            workBook.close();
        } catch (Exception e) {
            LOGGER.error("获取sheet名称", e);
        }
        return sheetNames;
    }

    /**
     * 获取Excel对象
     *
     * @param filePath Excel完整路径
     * @return
     * @throws Exception
     */
    public static Workbook getWorkBook(String filePath) throws Exception {
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = new FileInputStream(filePath);
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (filePath.endsWith(xls)) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (filePath.endsWith(xlsx)) {
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return workbook;
    }

    /**
     * 获取属性值
     *
     * @param cell
     * @param type
     * @return
     */
    public static Object getCellValue(Cell cell, String type) {
        String cellValue = "";
        type = type.toLowerCase();
        if (cell == null) {
            //表格未填数据设置默认值
            switch (type) {
                case "int":
                case "short":
                case "byte":
                case "long":
                    return 0;
                case "float":
                case "double":
                    return 0.0;
                case "array":
                    return new ArrayList<Document>();
                case "object":
                    return new Document();
                case "boolean":
                    return false;
                default:
                    return cellValue;
            }
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值 
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        if ("int".equalsIgnoreCase(type)) {
            return Integer.parseInt(cellValue);
        } else if ("long".equalsIgnoreCase(type)) {
            return Long.parseLong(cellValue);
        } else if ("byte".equalsIgnoreCase(type)) {
            return Byte.parseByte(cellValue);
        } else if ("short".equalsIgnoreCase(type)) {
            return Short.parseShort(cellValue);
        } else if ("Date".equalsIgnoreCase(type)) {
            return new Date(cellValue);
        } else if ("boolean".equalsIgnoreCase(type)) {
            return Boolean.parseBoolean(cellValue);
        } else if ("float".equalsIgnoreCase(type)) {
            return Float.parseFloat(cellValue);
        } else if ("double".equalsIgnoreCase(type)) {
            return Double.parseDouble(cellValue);
        } else if ("array".equalsIgnoreCase(type)) {
            return MongoUtil.getDocuments(cellValue);
        } else if ("object".equalsIgnoreCase(type)) {
            return MongoUtil.getDocument(cellValue);
        }

        return cellValue;
    }
}
