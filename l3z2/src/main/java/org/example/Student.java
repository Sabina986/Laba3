package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Student {
    private String grp, narp, Imya;
    private int Idd;

    public void insertStudent(Scanner sc) {
        try (Connection connection = MysqlConfig.getConnection()) {
            String dbName = MysqlConfig.getDatabaseName();
            if (dbName == null || dbName.isEmpty()) {
                System.out.println("Ошибка! Сначала создайте базу данных!");
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

            int N;
            while (true) {
                System.out.print("Введите кол-во студентов: ");
                if (sc.hasNextInt()) {
                    N = sc.nextInt();
                    if (N > 4) {
                        break; // Корректный ввод
                    } else {
                        System.out.println("Ошибка: число должно быть больше 4.");
                    }
                } else {
                    System.out.println("Ошибка: введите целое число.");
                    sc.next(); // Очистка неверного ввода
                }
            }
            for (int i = 0; i < N; i++){
                while (true) {
                    System.out.print("Введите ID студента: ");
                    if (sc.hasNextInt()) {
                        Idd = sc.nextInt();
                        if (Idd > 0) {
                            break; // Корректный ввод
                        } else {
                            System.out.println("Ошибка: число должно быть больше 0.");
                        }
                    } else {
                        System.out.println("Ошибка: введите целое число.");
                        sc.next(); // Очистка неверного ввода
                    }
                }
                sc.nextLine();
                System.out.print("Введите имя студента: ");
                String firstName = sc.nextLine();
                System.out.print("Введите фамилию студента: ");
                String lasttName = sc.nextLine();
                System.out.print("Введите отчество студента: ");
                String lasName = sc.nextLine();
                Imya = lasttName + " " + firstName + " " + lasName;
                System.out.print("Введите напрвление подготовки студента: ");
                narp = sc.nextLine();
                System.out.print("Введите группу студента: ");
                grp = sc.nextLine();
                String insertSQL = "INSERT INTO " + tbName + " (ID, Naprav, Name1, Group1) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psInsert = connection.prepareStatement(insertSQL)) {
                    psInsert.setInt(1, Idd); psInsert.setString(2, narp);
                    psInsert.setString(3, Imya); psInsert.setString(4, grp);
                    psInsert.executeUpdate();
                    System.out.println("Студент '" + Imya + "' успешно добавлен в базу данных!");
                }
            }
            String printSQL = "SELECT * FROM " + tbName;
            try (PreparedStatement psInsert = connection.prepareStatement(printSQL)) {
                ResultSet rs = psInsert.executeQuery();
                System.out.printf("| %-6s | %-38s | %-25s | %-10s |\n", "ID", "Направ. подготовки", "ФИО", "Группа");
                while (rs.next()) {
                    int id = rs.getInt("ID"); String Napr = rs.getString("Naprav");
                    String Name = rs.getString("Name1"); String Group = rs.getString("Group1");
                    System.out.printf("| %-6d | %-38s | %-25s | %-10s |\n", id, Napr, Name, Group);
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении студента в базу данных: " + e);

        }
    }
}


