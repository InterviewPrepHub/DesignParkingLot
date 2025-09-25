package com.parkingLot.DesignParkingLot.entities;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Ticket {

    private final UUID id = UUID.randomUUID();
    private final String spotId;
    private final Vehicle vehicle;
    private final long startEpochMillis;
    private Long endEpochMillis; // nullable until leave()

    public Ticket(String spotId, Vehicle vehicle) {
        this.spotId = spotId;
        this.vehicle = vehicle;
        this.startEpochMillis = System.currentTimeMillis();
    }

    public void close() { this.endEpochMillis = System.currentTimeMillis(); }
}
