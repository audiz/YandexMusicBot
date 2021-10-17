////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Garmin Canada Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2021 Garmin Canada Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 21.53Release
// Tag = production/akw/21.53.00-0-g1b82aa2b
////////////////////////////////////////////////////////////////////////////////


package com.garmin.fit;


public enum ExdDataUnits  {
    NO_UNITS((short)0),
    LAPS((short)1),
    MILES_PER_HOUR((short)2),
    KILOMETERS_PER_HOUR((short)3),
    FEET_PER_HOUR((short)4),
    METERS_PER_HOUR((short)5),
    DEGREES_CELSIUS((short)6),
    DEGREES_FARENHEIT((short)7),
    ZONE((short)8),
    GEAR((short)9),
    RPM((short)10),
    BPM((short)11),
    DEGREES((short)12),
    MILLIMETERS((short)13),
    METERS((short)14),
    KILOMETERS((short)15),
    FEET((short)16),
    YARDS((short)17),
    KILOFEET((short)18),
    MILES((short)19),
    TIME((short)20),
    ENUM_TURN_TYPE((short)21),
    PERCENT((short)22),
    WATTS((short)23),
    WATTS_PER_KILOGRAM((short)24),
    ENUM_BATTERY_STATUS((short)25),
    ENUM_BIKE_LIGHT_BEAM_ANGLE_MODE((short)26),
    ENUM_BIKE_LIGHT_BATTERY_STATUS((short)27),
    ENUM_BIKE_LIGHT_NETWORK_CONFIG_TYPE((short)28),
    LIGHTS((short)29),
    SECONDS((short)30),
    MINUTES((short)31),
    HOURS((short)32),
    CALORIES((short)33),
    KILOJOULES((short)34),
    MILLISECONDS((short)35),
    SECOND_PER_MILE((short)36),
    SECOND_PER_KILOMETER((short)37),
    CENTIMETER((short)38),
    ENUM_COURSE_POINT((short)39),
    BRADIANS((short)40),
    ENUM_SPORT((short)41),
    INCHES_HG((short)42),
    MM_HG((short)43),
    MBARS((short)44),
    HECTO_PASCALS((short)45),
    FEET_PER_MIN((short)46),
    METERS_PER_MIN((short)47),
    METERS_PER_SEC((short)48),
    EIGHT_CARDINAL((short)49),
    INVALID((short)255);

    protected short value;

    private ExdDataUnits(short value) {
        this.value = value;
    }

    public static ExdDataUnits getByValue(final Short value) {
        for (final ExdDataUnits type : ExdDataUnits.values()) {
            if (value == type.value)
                return type;
        }

        return ExdDataUnits.INVALID;
    }

    /**
     * Retrieves the String Representation of the Value
     * @return The string representation of the value
     */
    public static String getStringFromValue( ExdDataUnits value ) {
        return value.name();
    }

    public short getValue() {
        return value;
    }


}
