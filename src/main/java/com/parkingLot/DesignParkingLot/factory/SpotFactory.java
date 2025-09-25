package com.parkingLot.DesignParkingLot.factory;

import com.parkingLot.DesignParkingLot.enums.SpotSize;
import com.parkingLot.DesignParkingLot.entities.Spot;

public class SpotFactory {

    public static Spot create(SpotSize size, int level, String numericSuffix) {
        String prefix = switch (size) {
            case SMALL -> "S";
            case MEDIUM -> "M";
            case LARGE -> "L";
        };
        String spotId = "L" + level + "-" + prefix + "-" + numericSuffix;
        return new Spot(spotId, size, level);
    }
}
