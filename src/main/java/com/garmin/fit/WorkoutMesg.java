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



public class WorkoutMesg extends Mesg   {

    
    public static final int SportFieldNum = 4;
    
    public static final int CapabilitiesFieldNum = 5;
    
    public static final int NumValidStepsFieldNum = 6;
    
    public static final int WktNameFieldNum = 8;
    
    public static final int SubSportFieldNum = 11;
    
    public static final int PoolLengthFieldNum = 14;
    
    public static final int PoolLengthUnitFieldNum = 15;
    

    protected static final  Mesg workoutMesg;
    static {
        // workout
        workoutMesg = new Mesg("workout", MesgNum.WORKOUT);
        workoutMesg.addField(new Field("sport", SportFieldNum, 0, 1, 0, "", false, Profile.Type.SPORT));
        
        workoutMesg.addField(new Field("capabilities", CapabilitiesFieldNum, 140, 1, 0, "", false, Profile.Type.WORKOUT_CAPABILITIES));
        
        workoutMesg.addField(new Field("num_valid_steps", NumValidStepsFieldNum, 132, 1, 0, "", false, Profile.Type.UINT16));
        
        workoutMesg.addField(new Field("wkt_name", WktNameFieldNum, 7, 1, 0, "", false, Profile.Type.STRING));
        
        workoutMesg.addField(new Field("sub_sport", SubSportFieldNum, 0, 1, 0, "", false, Profile.Type.SUB_SPORT));
        
        workoutMesg.addField(new Field("pool_length", PoolLengthFieldNum, 132, 100, 0, "m", false, Profile.Type.UINT16));
        
        workoutMesg.addField(new Field("pool_length_unit", PoolLengthUnitFieldNum, 0, 1, 0, "", false, Profile.Type.DISPLAY_MEASURE));
        
    }

    public WorkoutMesg() {
        super(Factory.createMesg(MesgNum.WORKOUT));
    }

    public WorkoutMesg(final Mesg mesg) {
        super(mesg);
    }


    /**
     * Get sport field
     *
     * @return sport
     */
    public Sport getSport() {
        Short value = getFieldShortValue(4, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
        if (value == null) {
            return null;
        }
        return Sport.getByValue(value);
    }

    /**
     * Set sport field
     *
     * @param sport
     */
    public void setSport(Sport sport) {
        setFieldValue(4, 0, sport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get capabilities field
     *
     * @return capabilities
     */
    public Long getCapabilities() {
        return getFieldLongValue(5, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set capabilities field
     *
     * @param capabilities
     */
    public void setCapabilities(Long capabilities) {
        setFieldValue(5, 0, capabilities, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get num_valid_steps field
     * Comment: number of valid steps
     *
     * @return num_valid_steps
     */
    public Integer getNumValidSteps() {
        return getFieldIntegerValue(6, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set num_valid_steps field
     * Comment: number of valid steps
     *
     * @param numValidSteps
     */
    public void setNumValidSteps(Integer numValidSteps) {
        setFieldValue(6, 0, numValidSteps, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get wkt_name field
     *
     * @return wkt_name
     */
    public String getWktName() {
        return getFieldStringValue(8, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set wkt_name field
     *
     * @param wktName
     */
    public void setWktName(String wktName) {
        setFieldValue(8, 0, wktName, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get sub_sport field
     *
     * @return sub_sport
     */
    public SubSport getSubSport() {
        Short value = getFieldShortValue(11, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
        if (value == null) {
            return null;
        }
        return SubSport.getByValue(value);
    }

    /**
     * Set sub_sport field
     *
     * @param subSport
     */
    public void setSubSport(SubSport subSport) {
        setFieldValue(11, 0, subSport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get pool_length field
     * Units: m
     *
     * @return pool_length
     */
    public Float getPoolLength() {
        return getFieldFloatValue(14, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set pool_length field
     * Units: m
     *
     * @param poolLength
     */
    public void setPoolLength(Float poolLength) {
        setFieldValue(14, 0, poolLength, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get pool_length_unit field
     *
     * @return pool_length_unit
     */
    public DisplayMeasure getPoolLengthUnit() {
        Short value = getFieldShortValue(15, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
        if (value == null) {
            return null;
        }
        return DisplayMeasure.getByValue(value);
    }

    /**
     * Set pool_length_unit field
     *
     * @param poolLengthUnit
     */
    public void setPoolLengthUnit(DisplayMeasure poolLengthUnit) {
        setFieldValue(15, 0, poolLengthUnit.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

}
