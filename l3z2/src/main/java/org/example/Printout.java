package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Printout extends Student {
    @Override
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
                System.out.print("Введите ID студента: ");
                if (sc.hasNextInt()) {
                    N = sc.nextInt();
                    if (N > 0) {
                        break; // Корректный ввод
                    } else {
                        System.out.println("Ошибка: число должно быть больше 0.");
                    }
                } else {
                    System.out.println("Ошибка: введите целое число.");
                    sc.next(); // Очистка неверного ввода
                }
            }

            String printSQL = "SELECT * FROM " + tbName;
            try (PreparedStatement psInsert = connection.prepareStatement(printSQL)) {
                ResultSet rs = psInsert.executeQuery();
                System.out.printf("| %-6s | %-38s | %-25s | %-10s |\n", "ID", "Направ. подготовки", "ФИО", "Группа");
                while (rs.next()) {
                    if (rs.getInt("ID") == N) {
                        int id = rs.getInt("ID"); String Napr = rs.getString("Naprav");
                        String Name = rs.getString("Name1"); String Group = rs.getString("Group1");
                        System.out.printf("| %-6d | %-38s | %-25s | %-10s |\n", id, Napr, Name, Group);
                    }
                }
                sc.nextLine();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при выводе студентов из базы данных: " + e);

        }
    }
}
