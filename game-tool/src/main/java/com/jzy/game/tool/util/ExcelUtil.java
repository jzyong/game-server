package com.jzy.game.tool.util;

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
import org.apache.poi.ss.usermodel.*;
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
     * @return 属性名称列表、字段类型、描述说明，数据内容,排查信息
     */
    public static Args.Five<List<String>, List<String>, List<String>, List<List<Object>>, List<String>> readExcel(String filePath, String sheetName) throws Exception {
        Workbook workBook = getWorkBook(filePath);
        if (workBook == null) {
            LOGGER.warn("路径：{} excel不存在", filePath);
            return null;
        }
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            LOGGER.warn("路径：{}表单{}不存在", filePath, sheetName);
            return null;
        }

        List<String> fieldList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> descList = new ArrayList<>();
        List<String> columnSelectTypeList = new ArrayList<>();

        //前五行为元数据
        //1行：客户端元数据
        //2行：客户端|服务器标识行 common|server|client
        //3行：字段名称
        //4行：字段数据类型
        //5行：字段说明
        for (int i = 0; i < 5; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            //1行：客户端元数据,忽略
            if (i == 0) {
                continue;
            }
            int lastCellNum = row.getPhysicalNumberOfCells();
            for (int j = 0; j < lastCellNum; j++) {
                String value = "";
                try {
                    value = row.getCell(j).toString();
                } catch (NullPointerException e) {
                    throw new IllegalStateException(String.format("表单%s 表头 %d行%d列数据为空", sheetName, i, j));
                }

                switch (i) {
                    case 1:
                        columnSelectTypeList.add(value);
                        break;
                    case 2:
                        fieldList.add(value);
                        break;
                    case 3:
                        typeList.add(value);
                        break;
                    case 4:
                        descList.add(value);
                    default:
                        break;
                }
            }
        }
        //数据
        List<List<Object>> dataList = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 5; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            //int lastCellNum = row.getPhysicalNumberOfCells();
            int lastCellNum = fieldList.size();
            List<Object> datas = new ArrayList<>();
            Cell firstColumn = row.getCell(0);
            if (firstColumn == null) {
                continue;
            }
            //第一列#开头表示注释行
            firstColumn.setCellType(CellType.STRING); //强制设置为字符串
            if (firstColumn.getStringCellValue().startsWith("#")) {
                continue;
            }
            if ("".equalsIgnoreCase(firstColumn.getStringCellValue())) {
                LOGGER.warn("{} {}-{} 有空行或者id未设值，跳过读取", sheetName, i + 1, 1);
                continue;
            }

            for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                try {
                    Object object;
                    //跳过解析客户端数据
                    if ("client".equalsIgnoreCase(columnSelectTypeList.get(j))) {
                        object = cell == null ? "" : cell.toString();
                    } else {
                        object = getCellValue(cell, typeList.get(j));
                    }

                    datas.add(object);
                } catch (Exception e) {
                    LOGGER.error(String.format("%d-%d 数据读取错误", i + 1, j + 1), e);
                }
            }
            dataList.add(datas);
        }

        workBook.close();
        return Args.of(fieldList, typeList, descList, dataList, columnSelectTypeList);
    }

    /**
     * 获取表头元数据
     *
     * @param filePath
     * @return 属性名称列表、字段类型、描述说明、客户端服务器字段标识
     */
    public static Args.Four<List<String>, List<String>, List<String>, List<String>> getMetaData(String filePath, String sheetName) throws Exception {
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
        List<String> serverClientColumnList = new ArrayList<>();

        //前五行为元数据
        //1行：客户端元数据
        //2行：客户端|服务器标识行 common|server|client
        //3行：字段名称
        //4行：字段数据类型
        //5行：字段说明
        for (int i = 1; i < 5; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            int lastCellNum = row.getPhysicalNumberOfCells();
            for (int j = 0; j < lastCellNum; j++) {
                String value = row.getCell(j).toString();
                switch (i) {
                    case 1:
                        serverClientColumnList.add(value);
                        break;
                    case 2:
                        fieldList.add(value);
                        break;
                    case 3:
                        typeList.add(value);
                        break;
                    case 4:
                        descList.add(value);
                        break;
                    default:
                        break;
                }
            }
        }
        workBook.close();
        return Args.of(fieldList, typeList, descList, serverClientColumnList);
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
            LOGGER.error(String.format("获取 %s sheet名称", filePath), e);
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
                case "number": //lua中只有number
                    return 0.0;
                case "array":
                    return new ArrayList<Document>();
                case "object":
                case "json":
                    return new Document();
                case "boolean":
                case "bool":
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
        } else if ("boolean".equalsIgnoreCase(type) || "bool".equalsIgnoreCase(type)) {
            return Boolean.parseBoolean(cellValue);
        } else if ("float".equalsIgnoreCase(type)) {
            return Float.parseFloat(cellValue);
            //lua number默认为double类型
        } else if ("double".equalsIgnoreCase(type) || "number".equalsIgnoreCase(cellValue)) {
            return Double.parseDouble(cellValue);
        } else if ("array".equalsIgnoreCase(type)) {
            return MongoUtil.getDocuments(cellValue);
        } else if ("object".equalsIgnoreCase(type) || "json".equalsIgnoreCase(type)) {
            return MongoUtil.getDocument(cellValue);
            //指定类型数组需要自己组装中括号 1,2
        } else if ("string[]".equalsIgnoreCase(type) || "int[]".equalsIgnoreCase(type)
                || "bool[]".equalsIgnoreCase(type) || "double".equalsIgnoreCase(type)) {
            StringBuilder sb = new StringBuilder("[");
            sb.append(cellValue);
            sb.append("]");
            return MongoUtil.getDocuments(cellValue);
        } else if ("long[]".equalsIgnoreCase(type) || "float[]".equalsIgnoreCase(type)) {
            StringBuilder sb = new StringBuilder("[");
            sb.append(cellValue);
            sb.append("]");
            return MongoUtil.getDocuments(cellValue, type);
        }

        return cellValue;
    }
}
