package com.parkingLot.DesignParkingLot.entities;

import com.parkingLot.DesignParkingLot.enums.SpotSize;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Spot {

    private final String spotId;   // unique spot id, e.g. L1-M-001
    private final SpotSize size;
    private final int level;       // optional level/floor
    private boolean free = true;
    private Vehicle parkedVehicle;

    public Spot(String spotId, SpotSize size, int level) {
        this.spotId = Objects.requireNonNull(spotId);
        this.size = Objects.requireNonNull(size);
        this.level = level;
    }

    // Fit rules
    public boolean canFit(Vehicle v) {
        switch (v.getType()) {
            case MOTORCYCLE: return true;
            case CAR: return (size == SpotSize.MEDIUM || size == SpotSize.LARGE);
            case TRUCK: return (size == SpotSize.LARGE);
        }
        return false;
    }

    public void park(Vehicle v) {
        if (!free) throw new IllegalStateException("Spot already occupied");
        if (!canFit(v)) throw new IllegalArgumentException("Vehicle does not fit this spot");
        this.parkedVehicle = v;
        this.free = false;
    }

    public void free() {
        this.parkedVehicle = null;
        this.free = true;
    }
}
