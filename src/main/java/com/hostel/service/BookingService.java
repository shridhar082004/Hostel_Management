package com.hostel.service;

import com.hostel.AppConfig;
import com.hostel.db.MongoDBConnection;
import com.hostel.util.Validation;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bson.Document;
import org.bson.types.ObjectId;

public class BookingService {

    private final MongoCollection<Document> collection =
            MongoDBConnection.getDatabase().getCollection("bookings");
    private final List<String> allowedSlots = AppConfig.getBookingSlots();
    private final Set<String> allowedSlotSet = new HashSet<>(allowedSlots);

    public boolean book(String resource, int id, String slot, String date) {
        String safeResource = null;
        String safeDate = null;
        try {
            Validation.requirePositive(id, "Student ID");
            safeResource = Validation.requireNonBlank(resource, "Resource");
            String safeSlot = Validation.requireNonBlank(slot, "Time slot");
            safeDate = Validation.normalizeDate(date);

            if (!allowedSlotSet.contains(safeSlot)) {
                System.out.println("Invalid time slot.");
                printAvailableSlots(safeResource, safeDate);
                return false;
            }

            Document collisionQuery = new Document("resource", safeResource)
                    .append("date", safeDate)
                    .append("timeSlot", safeSlot)
                    .append("status", new Document("$ne", "Cancelled"));

            if (collection.find(collisionQuery).first() != null) {
                System.out.println("Slot already booked.");
                printAvailableSlots(safeResource, safeDate);
                return false;
            }

            Document doc = new Document("resource", safeResource)
                    .append("studentId", id)
                    .append("timeSlot", safeSlot)
                    .append("date", safeDate)
                    .append("status", "Booked")
                    .append("createdAt", new Date());

            collection.insertOne(doc);
            System.out.println("Booking successful.");
            return true;
        } catch (MongoWriteException ex) {
            if (ex.getError() != null && ex.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                System.out.println("Slot already booked.");
                if (safeResource != null && safeDate != null) {
                    printAvailableSlots(safeResource, safeDate);
                }
                return false;
            }
            System.out.println("Booking failed due to a database error.");
            return false;
        } catch (MongoException ex) {
            System.out.println("Booking failed due to a database error.");
            return false;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public void viewBookings(int studentId) {
        try (MongoCursor<Document> cursor = collection.find(new Document("studentId", studentId)).iterator()) {
            if (!cursor.hasNext()) {
                System.out.println("No bookings found.");
                return;
            }
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                System.out.println("ID: " + doc.getObjectId("_id"));
                System.out.println(doc.toJson());
            }
        } catch (MongoException ex) {
            System.out.println("Failed to load bookings due to a database error.");
        }
    }

    public boolean cancelBooking(String bookingId, int studentId) {
        if (!ObjectId.isValid(bookingId)) {
            System.out.println("Invalid booking ID.");
            return false;
        }

        try {
            Document query = new Document("_id", new ObjectId(bookingId))
                    .append("studentId", studentId);
            Document update = new Document("$set", new Document("status", "Cancelled")
                    .append("updatedAt", new Date()));

            if (collection.updateOne(query, update).getModifiedCount() > 0) {
                System.out.println("Booking cancelled.");
                return true;
            }

            System.out.println("Booking not found.");
            return false;
        } catch (MongoException ex) {
            System.out.println("Cancellation failed due to a database error.");
            return false;
        }
    }

    public List<String> listAvailableSlots(String resource, String date) {
        String safeResource = Validation.requireNonBlank(resource, "Resource");
        String safeDate = Validation.normalizeDate(date);
        return getAvailableSlots(safeResource, safeDate);
    }

    public void showAvailableSlots(String resource, String date) {
        try {
            String safeResource = Validation.requireNonBlank(resource, "Resource");
            String safeDate = Validation.normalizeDate(date);
            printAvailableSlots(safeResource, safeDate);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void printAvailableSlots(String resource, String date) {
        List<String> available = getAvailableSlots(resource, date);
        if (available.isEmpty()) {
            System.out.println("No available slots for " + resource + " on " + date + ".");
            return;
        }

        System.out.println("Available slots for " + resource + " on " + date + ":");
        for (String slot : available) {
            System.out.println("- " + slot);
        }
    }

    private List<String> getAvailableSlots(String resource, String date) {
        Set<String> booked = new HashSet<>();
        Document query = new Document("resource", resource)
                .append("date", date)
                .append("status", new Document("$ne", "Cancelled"));

        try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String slot = doc.getString("timeSlot");
                if (slot != null) {
                    booked.add(slot);
                }
            }
        } catch (MongoException ex) {
            System.out.println("Failed to load availability due to a database error.");
            return List.of();
        }

        List<String> available = new ArrayList<>();
        for (String slot : allowedSlots) {
            if (!booked.contains(slot)) {
                available.add(slot);
            }
        }
        return available;
    }
}
