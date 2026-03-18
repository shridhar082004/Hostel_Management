package com.hostel.ui;

import com.hostel.service.AdminAuthService;
import com.hostel.service.AdminService;
import com.hostel.service.BookingService;
import com.hostel.service.ComplaintService;
import com.hostel.service.StudentService;
import java.util.Scanner;

public class MainMenu {

    private final StudentService studentService = new StudentService();
    private final ComplaintService complaintService = new ComplaintService();
    private final BookingService bookingService = new BookingService();
    private final AdminService adminService = new AdminService();
    private final AdminAuthService adminAuthService = new AdminAuthService();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        ConsoleInput input = new ConsoleInput(scanner);

        try {
            while (true) {
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Student Login");
                System.out.println("3. Admin Login");
                System.out.println("4. Exit");

                int choice = input.readInt("Select option: ", 1, 4);

                if (choice == 1) {
                    handleRegistration(input);
                } else if (choice == 2) {
                    handleStudentLogin(input);
                } else if (choice == 3) {
                    handleAdminLogin(input);
                } else {
                    break;
                }
            }
        } catch (InputClosedException ex) {
            System.out.println();
            System.out.println("Input closed. Exiting.");
        }
    }

    private void handleRegistration(ConsoleInput input) {
        int id = input.readPositiveInt("Enter ID: ");
        String name = input.readNonEmpty("Enter Name: ");
        String password = input.readPassword("Enter Password: ");
        String confirm = input.readPassword("Confirm Password: ");

        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match.");
            return;
        }

        studentService.register(id, name, password);
    }

    private void handleStudentLogin(ConsoleInput input) {
        int id = input.readPositiveInt("Enter ID: ");
        String password = input.readPassword("Enter Password: ");

        if (!studentService.login(id, password)) {
            return;
        }

        while (true) {
            System.out.println();
            System.out.println("1. Raise Complaint");
            System.out.println("2. View Complaints");
            System.out.println("3. View Available Slots");
            System.out.println("4. Book Resource");
            System.out.println("5. View Bookings");
            System.out.println("6. Cancel Booking");
            System.out.println("7. Logout");

            int choice = input.readInt("Select option: ", 1, 7);

            if (choice == 1) {
                String type = input.readNonEmpty("Type: ");
                String desc = input.readNonEmpty("Description: ");
                complaintService.addComplaint(id, type, desc);
            } else if (choice == 2) {
                complaintService.viewComplaints(id);
            } else if (choice == 3) {
                String resource = input.readNonEmpty("Resource: ");
                String date = input.readNonEmpty("Date (yyyy-MM-dd): ");
                bookingService.showAvailableSlots(resource, date);
            } else if (choice == 4) {
                String resource = input.readNonEmpty("Resource: ");
                String date = input.readNonEmpty("Date (yyyy-MM-dd): ");
                while (true) {
                    java.util.List<String> available;
                    try {
                        available = bookingService.listAvailableSlots(resource, date);
                    } catch (IllegalArgumentException ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }

                    if (available.isEmpty()) {
                        System.out.println("No available slots for " + resource + " on " + date + ".");
                        break;
                    }

                    System.out.println("Available slots for " + resource + " on " + date + ":");
                    for (int i = 0; i < available.size(); i++) {
                        System.out.println((i + 1) + ". " + available.get(i));
                    }

                    String slotInput = input.readNonEmpty("Select slot number (or 'back'): ");
                    if ("back".equalsIgnoreCase(slotInput)) {
                        break;
                    }

                    int selection;
                    try {
                        selection = Integer.parseInt(slotInput);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid selection. Enter a number from the list.");
                        continue;
                    }

                    if (selection < 1 || selection > available.size()) {
                        System.out.println("Invalid selection. Enter a number from the list.");
                        continue;
                    }

                    String slot = available.get(selection - 1);
                    if (bookingService.book(resource, id, slot, date)) {
                        break;
                    }
                    System.out.println("Try another slot.");
                }
            } else if (choice == 5) {
                bookingService.viewBookings(id);
            } else if (choice == 6) {
                String bookingId = input.readNonEmpty("Enter Booking ID: ");
                bookingService.cancelBooking(bookingId, id);
            } else {
                break;
            }
        }
    }

    private void handleAdminLogin(ConsoleInput input) {
        String username = input.readNonEmpty("Enter Username: ");
        String password = input.readPassword("Enter Password: ");

        if (!adminAuthService.login(username, password)) {
            return;
        }

        while (true) {
            System.out.println();
            System.out.println("1. View All Complaints");
            System.out.println("2. Update Complaint Status");
            System.out.println("3. Logout");

            int choice = input.readInt("Select option: ", 1, 3);

            if (choice == 1) {
                adminService.viewAllComplaints();
            } else if (choice == 2) {
                String complaintId = input.readNonEmpty("Enter Complaint ID: ");
                String status = input.readNonEmpty("New Status: ");
                adminService.updateStatus(complaintId, status);
            } else {
                break;
            }
        }
    }
}
