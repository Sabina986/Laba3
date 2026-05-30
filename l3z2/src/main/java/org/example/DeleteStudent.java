package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DeleteStudent extends Student{
    @Override
    public void insertStudent(Scanner sc) {


        System.out.print("Введите ID студента для удаления: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат ID");
            return;
        }
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
            String deleteSQL = "DELETE FROM " + tbName + " WHERE id = ?";
            try (PreparedStatement psDelete = connection.prepareStatement(deleteSQL)) {
                psDelete.setInt(1, id);
                int rowsAffected = psDelete.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Студент с ID '" + id + "' успешно удален");
                }  else {
                    System.out.println("Студент с ID '" + id + "' не найден.");
                }
            } catch (SQLException e) {
                System.out.println("Ошибка при удалении студента: " + e);
            }


        } catch (SQLException e) {
            System.out.println("Ошибка при выводе студентов из базы данных: " + e);

        }
    }
}

