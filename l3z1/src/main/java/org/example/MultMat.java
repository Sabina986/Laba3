package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public final class MultMat extends ArrayPI {
    public void multi (Scanner sc) {
        int[][] matrixM = new int[7][7]; int[][] sumMatrix = new int[7][7]; int[][] minMatrix = new int[7][7];
        int[][] stMatrix1 = new int[7][7]; int[][] stMatrix2 = new int[7][7];
        String[] mat1 = new String[7]; String[] mat2 = new String[7];

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
                while (rs.next()) {
                    String []Mat1 = rs.getString("Matrix1").split("\n");
                    String []Mat2 = rs.getString("Matrix2").split("\n");
                    for (int i = 0; i<7; i++) {
                        mat1 = Mat1[i].split(" "); mat2 = Mat2[i].split(" ");
                        for (int j = 0; j<7; j++) {
                            mas1[i][j] = Integer.parseInt(mat1[j]); mas2[i][j] = Integer.parseInt(mat2[j]);
                        }
                    }
                }
            } catch (SQLException e) { System.out.println("Ошибка при экспорте данных: " + e); }
            int N;
            while (true) {
                System.out.print("Введите показатель степени для возведения матриц (целые положительные числа): ");
                if (sc.hasNextInt()) {
                    N = sc.nextInt();
                    if (N >= 0) {
                        sc.nextLine(); break; // Корректный ввод
                    } else {
                        System.out.println("Ошибка: число должно быть больше 0.");
                    }
                } else {
                    System.out.println("Ошибка: введите целое число.");
                    sc.next(); // Очистка неверного ввода
                }
            }
            if (N != 0){
                for (int y = 1; y<N; y++){
                    for (int i = 0; i<7; i++){
                        for (int j = 0; j<7; j++){
                            for (int k = 0; k<7; k++){
                                stMatrix1[i][j] += mas1[i][k] * mas1[k][j]; System.out.print("'" + stMatrix1[i][j] + "'");
                                stMatrix2[i][j] += mas2[i][k] * mas2[k][j];
                            }
                        }
                    }
                }
            } else {
                for (int j = 0; j<7; j++){
                    for (int k = 0; k<7; k++){
                        stMatrix1[j][k] = 0; stMatrix2[j][k] = 0;
                    }
                }
                stMatrix1[0][0] = 1; stMatrix1[1][1] = 1; stMatrix1[2][2] = 1; stMatrix1[3][3] = 1; stMatrix1[4][4] = 1;
                stMatrix1[5][5] = 1; stMatrix1[6][6] = 1; stMatrix2[0][0] = 1; stMatrix2[1][1] = 1; stMatrix2[2][2] = 1;
                stMatrix2[3][3] = 1; stMatrix2[4][4] = 1; stMatrix2[5][5] = 1; stMatrix2[6][6] = 1;
            }

            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    sumMatrix[i][j] = mas1[i][j] + mas2[i][j];
                    minMatrix[i][j] = mas1[i][j] - mas2[i][j];
                    matrixM[i][j] = 0;
                    for (int k = 0; k < 7; k++) {
                        matrixM[i][j] += mas1[i][k] * mas2[k][j];
                    }
                }
            }

            String printSQL = "SELECT * FROM " + tbName;
            try (PreparedStatement psInsert = connection.prepareStatement(printSQL)) {
                ResultSet rs = psInsert.executeQuery();
                while (rs.next()) {
                    String updateSQL = "UPDATE " + tbName + " SET SUMMatrix = ?, SumMat = ?, Minus = ?, Step1 = ?, Step2 = ?, i = ? WHERE Matrix1 = ?";
                    try (PreparedStatement psUpdate = connection.prepareStatement(updateSQL)) {
                        String StrAll = "", StrSum = "", StrMin = "", StrSt1 = "", StrSt2 = "";
                        String num = rs.getString("Matrix1");
                        System.out.println("\nИсходные матрицы: \n" + num + "\n" + rs.getString("Matrix2"));
                        System.out.println("\nПеремноженные матрицы: \n");
                        for (int i=0; i<7; i++){ // проход по строкам
                            for (int j=0; j<7; j++){ // проход по столбцам
                                System.out.print(" | " +  matrixM[i][j]);
                                StrAll = StrAll +  matrixM[i][j] + " ";
                            }
                            System.out.print(" |\n"); StrAll = StrAll + "\n";
                        }
                        System.out.println("\nСумма матриц: \n");
                        for (int i=0; i<7; i++){
                            for (int j=0; j<7; j++){
                                System.out.print(" | " + sumMatrix[i][j]);
                                StrSum = StrSum + sumMatrix[i][j] + " ";
                            }
                            System.out.print(" |\n"); StrSum = StrSum + "\n";
                        }
                        System.out.println("\nРазница матриц: \n");
                        for (int i=0; i<7; i++){
                            for (int j=0; j<7; j++){
                                System.out.print(" | " + minMatrix[i][j]);
                                StrMin = StrMin + minMatrix[i][j] + " ";
                            }
                            System.out.print(" |\n"); StrMin = StrMin + "\n";
                        }
                        System.out.println("\nВозведённые в степень матрицы: \n");
                        for (int i=0; i<7; i++){
                            for (int j=0; j<7; j++){
                                System.out.print(" | " + stMatrix1[i][j]);
                                StrSt1 = StrSt1 + stMatrix1[i][j] + " ";
                            }
                            System.out.print(" |\n"); StrSt1 = StrSt1 + "\n";
                        }
                        System.out.println("\n");
                        for (int i=0; i<7; i++){
                            for (int j=0; j<7; j++){
                                System.out.print(" | " + stMatrix2[i][j]);
                                StrSt2 = StrSt2 + stMatrix2[i][j] + " ";
                            }
                            System.out.print(" |\n"); StrSt2 = StrSt2 + "\n";
                        }
                        psUpdate.setString(1, StrAll); psUpdate.setString(2, StrSum);
                        psUpdate.setString(3, StrMin); psUpdate.setString(4, StrSt1);
                        psUpdate.setString(5, StrSt2); psUpdate.setInt(6, N); psUpdate.setString(7, num);
                        int rowsAffected = psUpdate.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("\nМатрицы успешно добавлены в базу данных!");
                        }  else {
                            System.out.println("Матрицы не найдены.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Ошибка при добавлении матриц в базу данных: " + e);
                    }
                }
            }

        } catch (SQLException e) { System.out.println("\nОшибка при добавлении в базу данных: " + e); }
    }
}

