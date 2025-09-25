package com.parkingLot.DesignParkingLot.core;

import com.parkingLot.DesignParkingLot.entities.Spot;
import com.parkingLot.DesignParkingLot.entities.Vehicle;
import com.parkingLot.DesignParkingLot.enums.SpotSize;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BestFitStrategy implements ParkingAssignment{

    @Override
    public Optional<Spot> pickSpot(Collection<Spot> allSpots, Vehicle v) {
        List<SpotSize> order = switch (v.getType()) {
            case MOTORCYCLE -> List.of(SpotSize.SMALL, SpotSize.MEDIUM, SpotSize.LARGE);
            case CAR        -> List.of(SpotSize.MEDIUM, SpotSize.LARGE);
            case TRUCK      -> List.of(SpotSize.LARGE);
        };

        // Try in preferred order: pick the first free spot of that size
        for (SpotSize target : order) {
            for (Spot s : allSpots) {
                if (s.isFree() && s.getSize() == target) {
                    return Optional.of(s);
                }
            }
        }
        return Optional.empty();
    }
}
