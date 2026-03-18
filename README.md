# Hostel Management System

Java + MongoDB console app for student registration, secure login, resource bookings, and complaint tracking. Includes admin workflows for complaint review and status updates, slot collision checks, and availability listing.

## Features
- Student registration with password hashing (PBKDF2).
- Student login with validation and last-login timestamp.
- Resource booking with collision detection by resource, date, and time slot.
- Available slots listing for a given resource and date.
- Booking cancellation (status-based).
- Complaint submission and per-student viewing.
- Admin view of all complaints and status updates.
- Input validation and error handling across flows.

## Tech Stack
- Java 25
- MongoDB (mongodb-driver-sync)
- Maven

## Requirements
- Java 25
- Maven
- MongoDB running locally or accessible via URI

## Configuration
You can configure using environment variables or JVM properties:

```text
MONGODB_URI (default: mongodb://127.0.0.1:27017)
MONGODB_DB  (default: hostelDB)
ADMIN_USER  (default: admin)
ADMIN_PASS  (default: admin123)
BOOKING_SLOTS (default: 08:00-10:00,10:00-12:00,12:00-14:00,14:00-16:00,16:00-18:00)
```

Example with JVM properties:

```text
-Dmongodb.uri=mongodb://127.0.0.1:27017
-Dmongodb.db=hostelDB
-Dadmin.user=admin
-Dadmin.pass=admin123
-Dbooking.slots=06:00-08:00,08:00-10:00,10:00-12:00
```

## Run
```text
mvn -q -DskipTests package
java -cp target/classes com.hostel.Main
```

## Usage
1. Register as a student with ID, name, and password.
2. Log in as a student to raise complaints or book resources.
3. Use “View Available Slots” or “Book Resource” to see available slots.
4. Admin can log in to view all complaints and update statuses.

## Notes
- Date format for booking is `yyyy-MM-dd`.
- Slot collision is checked by `resource + date + timeSlot` for active bookings.
- Cancelling a booking marks status as `Cancelled`.
- If a student existed before passwords were introduced, re-registering the same ID sets a password.
- Change admin credentials from the defaults in production.

## Common Issues
- “Slot already booked”: pick another available time slot.
- “Invalid time slot”: use one of the configured `BOOKING_SLOTS`.
- MongoDB connection errors: verify `MONGODB_URI` and that MongoDB is running.

