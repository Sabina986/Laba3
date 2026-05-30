package org.example;

import java.util.Scanner;

public class Run {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        Printout a = new Printout(); Student b = new Student(); DeleteStudent c = new DeleteStudent();

        while (!exit) {
            System.out.println("\n1. Вывести все таблицы из базы данных MySQL\n" +
                    "2. Создать таблицу в БД MySQL\n" +
                    "3. Ввести данные о всех студентах и сохранить их в MySQL\n" +
                    "4. Вывести данные о студенте по ID\n" +
                    "5. Удалить студента по ID\n" +
                    "6. Сохранить итоговые результаты из MySQL в Excel\n" +
                    "7. Выход");
            System.out.print("Выберите пункт меню: ");
            String input = sc.nextLine();
            switch (input) {
                case "1":
                    CreateDB.createDatabase(sc);
                    break;
                case "2":
                    CreateTB.createTable(sc);
                    break;
                case "3":
                    b.insertStudent(sc);
                    break;
                case "4":
                    a.insertStudent(sc);
                    break;
                case "5":
                    c.insertStudent(sc);
                    break;
                case "6":
                    System.out.print("Введите путь для сохранения Excel-файла: ");
                    String filepath = sc.nextLine();
                    ExportToExcel.exportTableToExcel(filepath);
                    break;
                case "7":
                    exit = true;
                    System.out.println("Выход из программы");
                    break;
                default:
                    System.out.println("Неверное значение пункта меню! Попробуйте снова.");
            }
        }
        MysqlConfig.shutdown();
        sc.close();
    }

}

