package com.parkingLot.DesignParkingLot;

import com.parkingLot.DesignParkingLot.core.BestFitStrategy;
import com.parkingLot.DesignParkingLot.core.FCFSParkingAssignmentImpl;
import com.parkingLot.DesignParkingLot.entities.Ticket;
import com.parkingLot.DesignParkingLot.entities.Vehicle;
import com.parkingLot.DesignParkingLot.enums.SpotSize;
import com.parkingLot.DesignParkingLot.enums.VehicleType;
import com.parkingLot.DesignParkingLot.factory.ParkingLot;
import com.parkingLot.DesignParkingLot.factory.SpotFactory;

public class Demo {

    public static void main(String[] args) {
        ParkingLot lot = ParkingLot.getInstance("P1", "Indiranagar Lot",
                new BestFitStrategy());

        // Add some spots (you can loop to add hundreds)
        lot.addSpot(SpotFactory.create(SpotSize.SMALL, 1, "001"));
        lot.addSpot(SpotFactory.create(SpotSize.MEDIUM, 1, "002"));
        lot.addSpot(SpotFactory.create(SpotSize.MEDIUM, 1, "003"));
        lot.addSpot(SpotFactory.create(SpotSize.LARGE, 1, "004"));

        // Vehicles
        Vehicle v1 = new Vehicle("KA-01-1234", VehicleType.CAR);
        Vehicle v2 = new Vehicle("KA-02-9999", VehicleType.MOTORCYCLE);
        Vehicle v3 = new Vehicle("KA-03-4444", VehicleType.TRUCK);

        // Park
        Ticket t1 = lot.park(v1); // goes to MEDIUM/LARGE
        Ticket t2 = lot.park(v2); // can take any free spot (SMALL preferred by strategy order)
        System.out.println("Availability after 2 parks: " + lot.availability());

        // Park truck
        Ticket t3 = lot.park(v3); // must take LARGE
        System.out.println("Availability after truck: " + lot.availability());

        // Leave
        lot.leave(t1.getId());
        System.out.println("Availability after car leaves: " + lot.availability());
    }
}
