package com.parkingLot.DesignParkingLot.core;

import com.parkingLot.DesignParkingLot.entities.Spot;
import com.parkingLot.DesignParkingLot.entities.Vehicle;

import java.util.Collection;
import java.util.Optional;

// Concrete: first available that fits (stable, simple, O(n))
public class FCFSParkingAssignmentImpl implements ParkingAssignment {
    @Override
    public Optional<Spot> pickSpot(Collection<Spot> allSpots, Vehicle v) {
        for (Spot s : allSpots) {
            if (s.isFree() && s.canFit(v)) return Optional.of(s);
        }
        return Optional.empty();
    }
}
