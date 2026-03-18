package com.hostel.service;

import com.hostel.db.MongoDBConnection;
import com.hostel.util.Validation;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.Date;
import org.bson.Document;

public class ComplaintService {

    private final MongoCollection<Document> collection =
            MongoDBConnection.getDatabase().getCollection("complaints");

    public boolean addComplaint(int studentId, String type, String desc) {
        try {
            Validation.requirePositive(studentId, "Student ID");
            String safeType = Validation.requireNonBlank(type, "Type");
            String safeDesc = Validation.requireMinLength(desc, "Description", 5);

            Document doc = new Document("studentId", studentId)
                    .append("type", safeType)
                    .append("description", safeDesc)
                    .append("status", "Pending")
                    .append("createdAt", new Date());

            collection.insertOne(doc);
            System.out.println("Complaint added.");
            return true;
        } catch (MongoException ex) {
            System.out.println("Failed to add complaint due to a database error.");
            return false;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public void viewComplaints(int studentId) {
        try (MongoCursor<Document> cursor = collection.find(new Document("studentId", studentId)).iterator()) {
            if (!cursor.hasNext()) {
                System.out.println("No complaints found.");
                return;
            }
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                System.out.println("ID: " + doc.getObjectId("_id"));
                System.out.println(doc.toJson());
            }
        } catch (MongoException ex) {
            System.out.println("Failed to load complaints due to a database error.");
        }
    }
}
