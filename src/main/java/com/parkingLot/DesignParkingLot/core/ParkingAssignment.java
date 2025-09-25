package com.parkingLot.DesignParkingLot.core;

import com.parkingLot.DesignParkingLot.entities.Spot;
import com.parkingLot.DesignParkingLot.entities.Vehicle;

import java.util.Collection;
import java.util.Optional;

public interface ParkingAssignment {

    Optional<Spot> pickSpot(Collection<Spot> allSpots, Vehicle v);
}
