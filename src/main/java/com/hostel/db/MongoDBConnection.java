package com.hostel.db;

import com.hostel.AppConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

public class MongoDBConnection {

    private static final MongoClient CLIENT = MongoClients.create(AppConfig.getMongoUri());

    public static MongoDatabase getDatabase() {
        return CLIENT.getDatabase(AppConfig.getDatabaseName());
    }

    public static void ensureIndexes() {
        MongoDatabase db = getDatabase();

        MongoCollection<Document> students = db.getCollection("students");
        try {
            students.createIndex(Indexes.ascending("studentId"), new IndexOptions().unique(true));
        } catch (Exception ex) {
            System.out.println("Warning: unable to ensure student indexes: " + ex.getMessage());
        }

        MongoCollection<Document> bookings = db.getCollection("bookings");
        try {
            bookings.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("resource"),
                            Indexes.ascending("date"),
                            Indexes.ascending("timeSlot")
                    ),
                    new IndexOptions()
                            .unique(true)
                            .partialFilterExpression(new Document("status", "Booked"))
            );
        } catch (Exception ex) {
            System.out.println("Warning: unable to ensure booking indexes: " + ex.getMessage());
        }

        MongoCollection<Document> complaints = db.getCollection("complaints");
        try {
            complaints.createIndex(Indexes.ascending("studentId"));
        } catch (Exception ex) {
            System.out.println("Warning: unable to ensure complaint indexes: " + ex.getMessage());
        }
    }
}
