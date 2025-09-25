package com.parkingLot.DesignParkingLot.factory;

import com.parkingLot.DesignParkingLot.enums.SpotSize;
import com.parkingLot.DesignParkingLot.core.ParkingAssignment;
import com.parkingLot.DesignParkingLot.entities.Spot;
import com.parkingLot.DesignParkingLot.entities.Ticket;
import com.parkingLot.DesignParkingLot.entities.Vehicle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ParkingLot {

    private static volatile ParkingLot INSTANCE; // double-checked lock safe
    private final String id;
    private final String name;

    private final Map<String, Spot> spotsById = new ConcurrentHashMap<>();
    private final Map<UUID, Ticket> activeTickets = new ConcurrentHashMap<>();
    private ParkingAssignment strategy;

    private ParkingLot(String id, String name, ParkingAssignment strategy) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.strategy = Objects.requireNonNull(strategy);
    }

    public static ParkingLot getInstance(String id, String name, ParkingAssignment strategy) {
        if (INSTANCE == null) {
            synchronized (ParkingLot.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ParkingLot(id, name, strategy);
                }
            }
        }
        return INSTANCE;
    }

    // Admin: add a spot
    public void addSpot(Spot spot) {
        if (spotsById.containsKey(spot.getSpotId()))
            throw new IllegalArgumentException("Duplicate spot id: " + spot.getSpotId());
        spotsById.put(spot.getSpotId(), spot);
    }

    // Client API: park a vehicle → ticket
    public Ticket park(Vehicle v) {
        Optional<Spot> candidate = strategy.pickSpot(spotsById.values(), v);
        if (candidate.isEmpty()) throw new IllegalStateException("No free compatible spot");

        Spot s = candidate.get();
        s.park(v);
        Ticket t = new Ticket(s.getSpotId(), v);
        activeTickets.put(t.getId(), t);
        return t;
    }

    // Client API: leave
    public void leave(UUID ticketId) {
        Ticket t = activeTickets.remove(ticketId);
        if (t == null) throw new NoSuchElementException("Unknown ticket: " + ticketId);
        Spot s = spotsById.get(t.getSpotId());
        if (s == null) throw new IllegalStateException("Spot missing for ticket: " + t.getSpotId());
        s.free();
        t.close();
    }

    public Map<SpotSize, Long> availability() {
        return spotsById.values().stream()
                .filter(Spot::isFree)
                .collect(Collectors.groupingBy(Spot::getSize, Collectors.counting()));
    }

    // Optional: swap strategy at runtime
    public void setStrategy(ParkingAssignment strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }
}
