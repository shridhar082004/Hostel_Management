package com.hostel.service;

import com.hostel.db.MongoDBConnection;
import com.hostel.util.Validation;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;

public class AdminService {

    private final MongoCollection<Document> collection =
            MongoDBConnection.getDatabase().getCollection("complaints");

    public void viewAllComplaints() {
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
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

    public boolean updateStatus(String id, String newStatus) {
        if (!ObjectId.isValid(id)) {
            System.out.println("Invalid complaint ID.");
            return false;
        }

        try {
            String safeStatus = Validation.requireNonBlank(newStatus, "Status");
            Document query = new Document("_id", new ObjectId(id));
            Document update = new Document("$set",
                    new Document("status", safeStatus).append("updatedAt", new Date()));

            if (collection.updateOne(query, update).getModifiedCount() > 0) {
                System.out.println("Status updated.");
                return true;
            }

            System.out.println("Complaint not found.");
            return false;
        } catch (MongoException ex) {
            System.out.println("Status update failed due to a database error.");
            return false;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
