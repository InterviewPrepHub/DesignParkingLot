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

Client calls park(vehicle)
Strategy scans free spots and returns a suitable spot (fit rules)
Lot marks the spot occupied, creates a Ticket, stores in active map, and returns it

## Leave flow

Client calls leave(ticketId)
Lot looks up the ticket, frees that spot, closes the ticket (end time), and removes it from active map

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

-- Queries
-- Find first free compatible spot for CAR (MEDIUM or LARGE), prefer MEDIUM:
SELECT spot_id FROM spots
WHERE is_free = TRUE AND size IN ('MEDIUM','LARGE')
ORDER BY CASE size WHEN 'MEDIUM' THEN 0 ELSE 1 END, spot_id
LIMIT 1;

-- Mark occupied / free:
UPDATE spots SET is_free = FALSE WHERE spot_id = ?;
UPDATE spots SET is_free = TRUE  WHERE spot_id = ?;

