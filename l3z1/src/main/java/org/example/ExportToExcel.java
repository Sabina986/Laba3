package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public final class ExportToExcel extends ArrayPI{
    public static void exportTableToExcel(String filepath) {

        try (Connection connection = MysqlConfig.getConnection()) {
            String dbName = MysqlConfig.getDatabaseName();
            if (dbName == null || dbName.isEmpty()) {
                System.out.println("Ошибка! Сначала создайте/подключитесь к базе данных!");
                return;
            }
            try (PreparedStatement psUse = connection.prepareStatement("USE " + dbName)) {
                psUse.executeUpdate();
            }
            String tbName = MysqlConfig.getTable();
            if (tbName == null || tbName.isEmpty()) {
                System.out.println("Ошибка! Сначала создайте таблицу в базе данных!");
                return;
            }
            String checkTableSQL = "SHOW TABLES LIKE ?";
            try (PreparedStatement psCheck = connection.prepareStatement(checkTableSQL)) {
                psCheck.setString(1, tbName);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Таблицы '" + tbName + "' не существует. Сначала создайте таблицу");
                        return;
                    }
                }
            }
            String printAll = "SELECT * FROM " + tbName;
            try (PreparedStatement psPrint = connection.prepareStatement(printAll); ResultSet rs = psPrint.executeQuery()) {
                Workbook wb = new XSSFWorkbook();
                Sheet sheet = wb.createSheet("Matrix");
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("Первая матрица");
                row.createCell(1).setCellValue("Вторая матрица");
                row.createCell(2).setCellValue("Произведение матриц");
                row.createCell(3).setCellValue("Сумма матриц");
                row.createCell(4).setCellValue("Вычитание матриц");
                row.createCell(5).setCellValue("Матрица 1 в степени i");
                row.createCell(6).setCellValue("Матрица 2 в степени i");
                row.createCell(7).setCellValue("i");

                int rowUbdex = 1;
                System.out.printf("\n| %-38s | %-38s | %-38s | %-38s | %-38s | %-38s | %-38s | %-5s |\n", "Первая матрица", "Вторая матрица", "Произведение матриц",
                        "Сумма матриц", "Вычитание матриц", "Матрица 1 в степени i", "Матрица 2 в степени i", "i");
                while (rs.next()) {
                    System.out.print("----------------------------------------------------------------------------------------------------------------------------\n");
                    String []Mat1 = rs.getString("Matrix1").split("\n");
                    String []Mat2 = rs.getString("Matrix2").split("\n");
                    if(rs.getString("SUMMatrix")==null){
                        for (int i=0; i<7; i++){
                            System.out.printf("| %-38s | %-38s | %-38s | %-38s | %-38s | %-38s | %-38s | %-5s |\n",
                                    Mat1[i], Mat2[i], " ", " ", " ", " ", " ", " ");
                        }
                    } else {
                        String []SumMat = rs.getString("SUMMatrix").split("\n");
                        String []SumM = rs.getString("SumMat").split("\n");
                        String []MinusM = rs.getString("Minus").split("\n");
                        String []StepM1 = rs.getString("Step1").split("\n");
                        String []StepM2 = rs.getString("Step2").split("\n");
                        int iM = rs.getInt("i");
                        for (int i=0; i<7; i++){
                            if (i==0){
                            System.out.printf("| %-38s | %-38s | %-38s | %-38s | %-38s | %-38s | %-38s | %-5d |\n",
                                    Mat1[i], Mat2[i], SumMat[i], SumM[i], MinusM[i], StepM1[i], StepM2[i], iM);
                            } else {
                                System.out.printf("| %-38s | %-38s | %-38s | %-38s | %-38s | %-38s | %-38s | %-5s |\n",
                                        Mat1[i], Mat2[i], SumMat[i], SumM[i], MinusM[i], StepM1[i], StepM2[i], " ");
                            }
                        }
                    }
                    Row row1 = sheet.createRow(rowUbdex++);
                    row1.createCell(0).setCellValue(rs.getString("Matrix1"));
                    row1.createCell(1).setCellValue(rs.getString("Matrix2"));
                    row1.createCell(2).setCellValue(rs.getString("SUMMatrix"));
                    row1.createCell(3).setCellValue(rs.getString("SumMat"));
                    row1.createCell(4).setCellValue(rs.getString("Minus"));
                    row1.createCell(5).setCellValue(rs.getString("Step1"));
                    row1.createCell(6).setCellValue(rs.getString("Step2"));
                    row1.createCell(7).setCellValue(rs.getInt("i"));
                }
                System.out.print("\n");
                int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
                for (int i = 0; i < columnCount; i++) {
                    sheet.autoSizeColumn(i);
                }
                try (FileOutputStream fos = new FileOutputStream(filepath)) {
                    wb.write(fos);
                } catch (IOException e) {
                    System.out.println("Ошибка при записи Excel-файла: " + e);
                } finally {
                    wb.close();
                    System.out.println("Данные успешно экспортированы в Excel-файл: " + filepath);
                }
            } catch (SQLException e) {
                System.out.println("Ошибка при экспорте данных: " + e);
            }
        } catch (IOException | SQLException e) {
            System.out.println("Ошибка при закрытии Excel-файла: " + e);
        }
    }
}

