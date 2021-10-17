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

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Description of a Developer Field
 */
public class DeveloperFieldDescription {
    private final FieldDescriptionMesg fieldDescription;
    private final DeveloperDataIdMesg developerId;

    /**
     * Constructs a new Developer Field Description
     * @param developerId Developer Id Message for the field. Can't be <code>null</code>
     * @param fieldDescription Field Description Message for the field. Can't be <code>null</code>
     */
    DeveloperFieldDescription(DeveloperDataIdMesg developerId, FieldDescriptionMesg fieldDescription) {
        this.developerId = developerId;
        this.fieldDescription = fieldDescription;
    }

    /**
     * Retrieves the Application Version of the generating Field
     * @return <code>null</code> if there is no version encoded in the file
     */
    public long getApplicationVersion() {
        Long applicationVer = developerId.getApplicationVersion();
        if(applicationVer == null) {
            return (long)0xFFFF;
        }

        return applicationVer.longValue();
    }

    /**
     * Retrieves the Application Id of the generating Field
     * @return Application Id or <code>null</code> if there is no valid Application Id for the description
     */
    public UUID getApplicationId() {
        Byte[] appId = developerId.getApplicationId();
        if(appId == null || appId.length != 16) {
            return null;
        }

        byte[] primitiveId = new byte[appId.length];

        for(byte i = 0; i < appId.length; i++) {
            if(appId[i] != null) {
                primitiveId[i] = appId[i];
            } else {
                primitiveId[i] = (byte)0xFF;
            }
        }

        ByteBuffer bb = ByteBuffer.wrap(primitiveId);
        long high = bb.getLong();
        long low = bb.getLong();

        return new UUID(high, low);
    }

    /**
     * Retrieves the Field Definition Number of the generating Field
     * @return Field Definition Number
     */
    public short getFieldDefinitionNumber() {
        Short num = fieldDescription.getFieldDefinitionNumber();
        if(num == null) {
            return (short)0xFF;
        }

        return num.shortValue();
    }
}
