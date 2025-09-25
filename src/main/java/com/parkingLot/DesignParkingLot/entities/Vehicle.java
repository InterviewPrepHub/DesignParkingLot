package com.parkingLot.DesignParkingLot.entities;

import com.parkingLot.DesignParkingLot.enums.VehicleType;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Vehicle {

    private final String licensePlate;
    private final VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = Objects.requireNonNull(licensePlate);
        this.type = Objects.requireNonNull(type);
    }

}
