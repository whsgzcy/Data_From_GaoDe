package com.iwant.excel;

/**
* @Description: 从excel读取数据/往excel中写入 excel有表头，表头每列的内容对应实体类的属性 
* @author nagsh 
* @author: zysu
* @date: 2016年12月18日 下午12:28:27  
*    
*/

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.lang.reflect.Field;  
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;  
import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.poi.hssf.usermodel.HSSFRow;  
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.ss.usermodel.Sheet;  

public class ExcelManage {
	private HSSFWorkbook workbook = null;  
    
    /** 
     * 判断文件是否存在. 
     * @param fileDir  文件路径 
     * @return 
     */  
    public boolean fileExist(String fileDir){  
         boolean flag = false;  
         File file = new File(fileDir);  
         flag = file.exists();  
         return flag;  
    }  
    /** 
     * 判断文件的sheet是否存在. 
     * @param fileDir   文件路径 
     * @param sheetName  表格索引名 
     * @return 
     */  
    public boolean sheetExist(String fileDir,String sheetName){  
         boolean flag = false;  
         File file = new File(fileDir);  
         if(file.exists()){    //文件存在  
            //创建workbook  
             try {  
                workbook = new HSSFWorkbook(new FileInputStream(file));  
                //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)  
                HSSFSheet sheet = workbook.getSheet(sheetName);    
                if(sheet!=null)  
                    flag = true;  
            } catch (Exception e) {  
                e.printStackTrace();  
            }   
              
         }else{    //文件不存在  
             flag = false;  
         }  
           
         return flag;  
    }  
    /** 
     * 创建新excel. 
     * @param fileDir  excel的路径 
     * @param sheetName 要创建的表格索引 
     * @param titleRow excel的第一行即表格头 
     */  
    public void createExcel(String fileDir,String sheetName,String titleRow[]){  
        //创建workbook  
        workbook = new HSSFWorkbook();  
        //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)  
        Sheet sheet1 = workbook.createSheet(sheetName);    
        //新建文件  
        FileOutputStream out = null;  
        try {  
            //添加表头  
            Row row = workbook.getSheet(sheetName).createRow(0);    //创建第一行    
            for(int i = 0;i < titleRow.length;i++){  
                Cell cell = row.createCell(i);  
                cell.setCellValue(titleRow[i]);  
            }  
            out = new FileOutputStream(fileDir);  
            workbook.write(out);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {    
            try {    
                out.close();    
            } catch (IOException e) {    
                e.printStackTrace();  
            }    
        }    
    }  
    /** 
     * 删除文件. 
     * @param fileDir  文件路径 
     */  
    public boolean deleteExcel(String fileDir){  
        boolean flag = false;  
        File file = new File(fileDir);  
        // 判断目录或文件是否存在    
        if (!file.exists()) {  // 不存在返回 false    
            return flag;    
        } else {    
            // 判断是否为文件    
            if (file.isFile()) {  // 为文件时调用删除文件方法    
                file.delete();  
                flag = true;  
            }   
        }  
        return flag;  
    }  
 
    /** 
     * 读取excel表中的数据. 
     *  
     * @param fileDir    文件路径    
     * @param sheetName 表格索引(EXCEL 是多表文档,所以需要输入表索引号，如sheet1) 
     * @param object   object 
     */  
    public List readFromExcel(String fileDir,String sheetName, Object object) {  
        //创建workbook  
        File file = new File(fileDir);  
        try {  
            workbook = new HSSFWorkbook(new FileInputStream(file));  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
        List result = new ArrayList();  
        // 获取该对象的class对象  
        Class class_ = object.getClass();  
        // 获得该类的所有属性  
        Field[] fields = class_.getDeclaredFields();  
  
        // 读取excel数据  
        // 获得指定的excel表  
        HSSFSheet sheet = workbook.getSheet(sheetName);  
        // 获取表格的总行数  
        int rowCount = sheet.getLastRowNum() + 1; // 需要加一  
        System.out.println("rowCount:"+rowCount);  
        if (rowCount < 1) {  
            return result;  
        }  
        // 获取表头的列数  
        int columnCount = sheet.getRow(0).getLastCellNum();  
        // 读取表头信息,确定需要用的方法名---set方法  
        // 用于存储方法名  
        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数  
        // 用于存储属性类型  
        String[] fieldTypes = new String[columnCount];  
        // 获得表头行对象  
        HSSFRow titleRow = sheet.getRow(0);  
        // 遍历  
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头列  
            String data = titleRow.getCell(columnIndex).toString(); // 某一列的内容  
            String Udata = Character.toUpperCase(data.charAt(0))  
                    + data.substring(1, data.length()); // 使其首字母大写  
            methodNames[columnIndex] = "set" + Udata;  
            for (int i = 0; i < fields.length; i++) { // 遍历属性数组  
                if (data.equals(fields[i].getName())) { // 属性与表头相等  
                    fieldTypes[columnIndex] = fields[i].getType().getName(); // 将属性类型放到数组中  
                }  
            }  
        }  
        // 逐行读取数据 从1开始 忽略表头  
        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {  
            // 获得行对象  
            HSSFRow row = sheet.getRow(rowIndex);  
            if (row != null) {  
                Object obj = null;  
                // 实例化该泛型类的对象一个对象  
                try {  
                    obj = class_.newInstance();  
                } catch (Exception e1) {  
                    e1.printStackTrace();  
                }  
  
                // 获得本行中各单元格中的数据  
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {  
                    String data = row.getCell(columnIndex).toString();  
                    // 获取要调用方法的方法名  
                    String methodName = methodNames[columnIndex];  
                    Method method = null;  
                    try {  
                        // 这部分可自己扩展  
                        if (fieldTypes[columnIndex].equals("java.lang.String")) {  
                            method = class_.getDeclaredMethod(methodName,  
                                    String.class); // 设置要执行的方法--set方法参数为String  
                            method.invoke(obj, data); // 执行该方法  
                        } else if (fieldTypes[columnIndex].equals("int")) {  
                            method = class_.getDeclaredMethod(methodName,  
                                    int.class); // 设置要执行的方法--set方法参数为int  
                            double data_double = Double.parseDouble(data);  
                            int data_int = (int) data_double;  
                            method.invoke(obj, data_int); // 执行该方法  
                        }  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
                result.add(obj);  
            }  
        }  
        return result;  
    }  
      
    /** 
     * 往excel中写入(已存在的数据无法写入). 
     * @param fileDir    文件路径 
     * @param sheetName  表格索引 
     * @param object 
     */  
    public void writeToExcel(String fileDir,String sheetName, Object object){  
        //创建workbook  
        File file = new File(fileDir);  
        try {  
            workbook = new HSSFWorkbook(new FileInputStream(file));  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        //流  
        FileOutputStream out = null;  
        HSSFSheet sheet = workbook.getSheet(sheetName);  
        // 获取表格的总行数  
        int rowCount = sheet.getLastRowNum() + 1; // 需要加一  
        // 获取表头的列数  
        int columnCount = sheet.getRow(0).getLastCellNum();  
        try {  
            Row row = sheet.createRow(rowCount);     //最新要添加的一行  
            //通过反射获得object的字段,对应表头插入  
            // 获取该对象的class对象  
//            Class class_ = object.getClass();  
            // 获得表头行对象  
            HSSFRow titleRow = sheet.getRow(0);  
            if(titleRow!=null){  
//                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {  //遍历表头  
//                    String title = titleRow.getCell(columnIndex).toString().trim().toString().trim();  
//                    String UTitle = Character.toUpperCase(title.charAt(0))+ title.substring(1, title.length()); // 使其首字母大写;  
//                    String methodName  = "get"+UTitle;  
                    Cell cell1 = row.createCell(0);
                    cell1.setCellValue("111");
                    Cell cell2 = row.createCell(1);
                    cell2.setCellValue("222");
                    Cell cell3 = row.createCell(2);
                    cell3.setCellValue("333");
//                }  
            }  
            out = new FileOutputStream(fileDir);  
            workbook.write(out);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {    
            try {    
                out.close();    
            } catch (IOException e) {    
                e.printStackTrace();  
            }    
        }    
    } 

    public static void main(String[] args) {  
        ExcelManage em = new ExcelManage();  
        //判断文件是否存在  
        System.out.println(em.fileExist("C:/Users/suzhe/Desktop/test2.xls"));  
        //创建文件  
        String title[] = {"id","name","password"};  
        em.createExcel("C:/Users/suzhe/Desktop/test2.xls","sheet1",title);  
        //判断sheet是否存在  
        System.out.println(em.sheetExist("C:/Users/suzhe/Desktop/test2.xls","sheet1"));  
        em.writeToExcel("C:/Users/suzhe/Desktop/test2.xls","sheet1",null);  

    }  
}
