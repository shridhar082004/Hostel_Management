package com.hostel.service;

import com.hostel.db.MongoDBConnection;
import com.hostel.security.PasswordHasher;
import com.hostel.util.Validation;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import java.util.Date;
import org.bson.Document;

public class StudentService {

    private final MongoCollection<Document> collection =
            MongoDBConnection.getDatabase().getCollection("students");

    public boolean register(int id, String name, String password) {
        try {
            Validation.requirePositive(id, "Student ID");
            String safeName = Validation.requireNonBlank(name, "Name");
            String safePassword = Validation.requireMinLength(password, "Password", 6);

            Document existing = collection.find(new Document("studentId", id)).first();
            if (existing != null) {
                String storedHash = existing.getString("passwordHash");
                if (storedHash == null || storedHash.isBlank()) {
                    collection.updateOne(
                            new Document("_id", existing.getObjectId("_id")),
                            new Document("$set",
                                    new Document("name", safeName)
                                            .append("passwordHash", PasswordHasher.hash(safePassword))
                                            .append("updatedAt", new Date()))
                    );
                    System.out.println("Registration updated existing account.");
                    return true;
                }
                System.out.println("Student already exists.");
                return false;
            }

            Document student = new Document("studentId", id)
                    .append("name", safeName)
                    .append("passwordHash", PasswordHasher.hash(safePassword))
                    .append("createdAt", new Date());

            collection.insertOne(student);
            System.out.println("Registration successful.");
            return true;
        } catch (MongoWriteException ex) {
            if (ex.getError() != null && ex.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                System.out.println("Student already exists.");
                return false;
            }
            System.out.println("Registration failed due to a database error.");
            return false;
        } catch (MongoException ex) {
            System.out.println("Registration failed due to a database error.");
            return false;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean login(int id, String password) {
        try {
            Validation.requirePositive(id, "Student ID");
            String safePassword = Validation.requireNonBlank(password, "Password");

            Document student = collection.find(new Document("studentId", id)).first();
            if (student == null) {
                System.out.println("Student not found.");
                return false;
            }

            String storedHash = student.getString("passwordHash");
            if (storedHash == null || storedHash.isBlank()) {
                System.out.println("Account requires password setup. Please register again.");
                return false;
            }
            if (!PasswordHasher.verify(safePassword, storedHash)) {
                System.out.println("Invalid password.");
                return false;
            }

            collection.updateOne(
                    new Document("_id", student.getObjectId("_id")),
                    new Document("$set", new Document("lastLoginAt", new Date()))
            );
            System.out.println("Login successful.");
            return true;
        } catch (MongoException ex) {
            System.out.println("Login failed due to a database error.");
            return false;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
