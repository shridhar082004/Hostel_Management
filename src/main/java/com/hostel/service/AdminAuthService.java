package com.hostel.service;

import com.hostel.AppConfig;
import com.hostel.util.Validation;

public class AdminAuthService {

    public boolean login(String username, String password) {
        try {
            String safeUser = Validation.requireNonBlank(username, "Username");
            String safePass = Validation.requireNonBlank(password, "Password");

            if (!AppConfig.getAdminUser().equals(safeUser)) {
                System.out.println("Invalid admin username.");
                return false;
            }
            if (!AppConfig.getAdminPass().equals(safePass)) {
                System.out.println("Invalid admin password.");
                return false;
            }

            if (AppConfig.isDefaultAdminCredentials()) {
                System.out.println("Warning: default admin credentials are in use.");
            }

            System.out.println("Admin login successful.");
            return true;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
