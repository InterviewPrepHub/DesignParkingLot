## What we’re designing (quick spec)

    Vehicle types: MOTORCYCLE, CAR, TRUCK
    Spot sizes: SMALL, MEDIUM, LARGE

### Fit rules (common interview convention):
    MOTORCYCLE → SMALL/MEDIUM/LARGE
    CAR → MEDIUM/LARGE
    TRUCK → LARGE only

### Operations:

    park(vehicle) → returns Ticket (spot assigned)
    leave(ticketId) → frees spot
    availability() → counts per spot size / total free

### Extensible:
    Pluggable assignment strategy (e.g., first-available, nearest, price-aware, level-aware)
    Multiple levels/floors supported (optional in this base, easy to add)

## High-level design & flow
### Class diagram (text)

    +-------------------+               +----------------------+
    |   ParkingLot (*)  |<--Singleton-->| ParkingAssignment.   |
    +-------------------+               +----------------------+
    | id, name          |               | pickSpot(...)        |
    | Map<UUID,Ticket>  |               +----------------------+
    | Map<SpotId,Spot>  |                         ^
    | Strategy strategy |                         |
    | park(v)           |                         | (e.g., FirstAvailableStrategy)
    | leave(ticketId)   |                         |
    | availability()    |                         |
    +---------^---------+                         |
    |                                   |
    +---+--------------+            +-------+-------------------+
    |    SpotFactory   |            | FCFSParkingAssignmentImpl |
    +------------------+            +---------------------------+
    | create(size,...) |            | scans free spots by fit   |
    +------------------+            +---------------------------+
    
    +--------------------+          +------------------+
    |      Spot          |<>--------|    Vehicle       |
    +--------------------+          +------------------+
    | id, size, level    |          | license, type    |
    | isFree, parkedVeh  |          +------------------+
    | canFit(v)          |
    | park(v), free()    |          +------------------+
    +--------------------+          |    Ticket        |
    +------------------+
    | id, vehicle,     |
    | spotId, start    |
    +------------------+

## Park flow

- Client calls park(vehicle)
- Strategy scans free spots and returns a suitable spot (fit rules)
- Lot marks the spot occupied, creates a Ticket, stores in active map, and returns it

## Leave flow

- Client calls leave(ticketId)
- Lot looks up the ticket, frees that spot, closes the ticket (end time), and removes it from active map

## Optional DB sketch

    TABLE spots(
    spot_id VARCHAR PRIMARY KEY,
    size ENUM('SMALL','MEDIUM','LARGE'),
    level INT,
    is_free BOOLEAN NOT NULL
    );

    TABLE vehicles(
    license_plate VARCHAR PRIMARY KEY,
    type ENUM('MOTORCYCLE','CAR','TRUCK')
    );

    TABLE tickets(
    ticket_id CHAR(36) PRIMARY KEY,
    spot_id VARCHAR REFERENCES spots(spot_id),
    license_plate VARCHAR REFERENCES vehicles(license_plate),
    start_epoch_ms BIGINT NOT NULL,
    end_epoch_ms BIGINT
    );

- Queries 

Find first free compatible spot for CAR (MEDIUM or LARGE), prefer MEDIUM:

    SELECT spot_id FROM spots
    WHERE is_free = TRUE AND size IN ('MEDIUM','LARGE')
    ORDER BY CASE size WHEN 'MEDIUM' THEN 0 ELSE 1 END, spot_id
    LIMIT 1;

Mark occupied / free:

    UPDATE spots SET is_free = FALSE WHERE spot_id = ?;
    UPDATE spots SET is_free = TRUE  WHERE spot_id = ?;

# Design Pattern

## Factory Pattern

- creational design pattern.
- It’s used when you don’t want your code to directly create objects with the new keyword everywhere.
- Instead, you centralize object creation in a factory class (or method).

### 🚗 Applying it to Parking System

In a Parking System, we have different types of vehicles (Car, Bike, Truck) and different types of parking spots (Compact, Large, Handicapped, Motorcycle).

### Without Factory Pattern:

- The client code (Main method or ParkingLot) would directly say new Car(), new MotorcycleSpot(), etc.
- This means client code is tightly coupled with object creation.

### With Factory Pattern:

We create a VehicleFactory and a ParkingSpotFactory. 
The client just says: “I need a CAR” or “I need a spot for a TRUCK”.
The factory decides which object to return.

    // Vehicle base class
    abstract class Vehicle {
        String licensePlate;
    }

    // Different vehicle types
    class Car extends Vehicle {}
    class Bike extends Vehicle {}
    class Truck extends Vehicle {}

    // Factory for creating vehicles
    class VehicleFactory {
        public static Vehicle createVehicle(String type) {
            switch (type.toUpperCase()) {
                case "CAR": return new Car();
                case "BIKE": return new Bike();
                case "TRUCK": return new Truck();
                default: throw new IllegalArgumentException("Unknown vehicle type");
            }
        }
    }


## Singleton Pattern

- The Singleton Pattern is a creational design pattern.
- It ensures that only one object (instance) of a class exists in the whole application.
- Also, it provides a global point of access to that instance.

- In our parking system, there should only be one ParkingLot managing all spots and vehicles.
- If we allowed multiple new ParkingLot(), each one would track its own cars/spots — which makes no sense.


    class ParkingLot {
        // Step 1: static variable to hold single instance
        private static ParkingLot instance;

        private int capacity;
        private ParkingLot(int capacity) {  // private constructor
            this.capacity = capacity;
        }

        // Step 2: static method to return the instance
        public static synchronized ParkingLot getInstance(int capacity) {
            if (instance == null) {
                instance = new ParkingLot(capacity);  // create once
            }
            return instance;  // always return the same object
        }
    }

- Private constructor → stops anyone from creating a new ParkingLot using new ParkingLot().
- Static instance variable → stores the single copy.
- Public static method (getInstance) → gives you that single copy.

- If a ParkingLot has multiple floors, then the ParkingLot itself stays as the Singleton (only one for the entire mall/complex), but inside it you add a Floor abstraction.
- Structure in simple words 
  - ParkingLot (Singleton) → only one in the system.
  - Floor(s) → each floor has its own collection of spots.
  - ParkingSpot(s) → belong to a specific floor.
    
    - quick uml:

      ParkingLot (Singleton)
        |
        +-- List<Floor>
                |
                +-- List<ParkingSpot>












