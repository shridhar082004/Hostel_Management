package com.hostel.ui;

import java.io.Console;
import java.util.Scanner;

public class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    public int readPositiveInt(String prompt) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value <= 0) {
                    System.out.println("Please enter a positive number.");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    public String readNonEmpty(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (line.isBlank()) {
                System.out.println("Value cannot be empty.");
                continue;
            }
            return line;
        }
    }

    public String readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] value = console.readPassword(prompt);
            if (value == null) {
                throw new InputClosedException();
            }
            return new String(value).trim();
        }
        return readNonEmpty(prompt);
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine().trim();
    }
}
