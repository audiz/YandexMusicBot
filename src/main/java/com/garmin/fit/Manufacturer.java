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

import java.util.HashMap;
import java.util.Map;

public class Manufacturer  {
    public static final int GARMIN = 1;
    public static final int GARMIN_FR405_ANTFS = 2; // Do not use.  Used by FR405 for ANTFS man id.
    public static final int ZEPHYR = 3;
    public static final int DAYTON = 4;
    public static final int IDT = 5;
    public static final int SRM = 6;
    public static final int QUARQ = 7;
    public static final int IBIKE = 8;
    public static final int SARIS = 9;
    public static final int SPARK_HK = 10;
    public static final int TANITA = 11;
    public static final int ECHOWELL = 12;
    public static final int DYNASTREAM_OEM = 13;
    public static final int NAUTILUS = 14;
    public static final int DYNASTREAM = 15;
    public static final int TIMEX = 16;
    public static final int METRIGEAR = 17;
    public static final int XELIC = 18;
    public static final int BEURER = 19;
    public static final int CARDIOSPORT = 20;
    public static final int A_AND_D = 21;
    public static final int HMM = 22;
    public static final int SUUNTO = 23;
    public static final int THITA_ELEKTRONIK = 24;
    public static final int GPULSE = 25;
    public static final int CLEAN_MOBILE = 26;
    public static final int PEDAL_BRAIN = 27;
    public static final int PEAKSWARE = 28;
    public static final int SAXONAR = 29;
    public static final int LEMOND_FITNESS = 30;
    public static final int DEXCOM = 31;
    public static final int WAHOO_FITNESS = 32;
    public static final int OCTANE_FITNESS = 33;
    public static final int ARCHINOETICS = 34;
    public static final int THE_HURT_BOX = 35;
    public static final int CITIZEN_SYSTEMS = 36;
    public static final int MAGELLAN = 37;
    public static final int OSYNCE = 38;
    public static final int HOLUX = 39;
    public static final int CONCEPT2 = 40;
    public static final int SHIMANO = 41;
    public static final int ONE_GIANT_LEAP = 42;
    public static final int ACE_SENSOR = 43;
    public static final int BRIM_BROTHERS = 44;
    public static final int XPLOVA = 45;
    public static final int PERCEPTION_DIGITAL = 46;
    public static final int BF1SYSTEMS = 47;
    public static final int PIONEER = 48;
    public static final int SPANTEC = 49;
    public static final int METALOGICS = 50;
    public static final int _4IIIIS = 51;
    public static final int SEIKO_EPSON = 52;
    public static final int SEIKO_EPSON_OEM = 53;
    public static final int IFOR_POWELL = 54;
    public static final int MAXWELL_GUIDER = 55;
    public static final int STAR_TRAC = 56;
    public static final int BREAKAWAY = 57;
    public static final int ALATECH_TECHNOLOGY_LTD = 58;
    public static final int MIO_TECHNOLOGY_EUROPE = 59;
    public static final int ROTOR = 60;
    public static final int GEONAUTE = 61;
    public static final int ID_BIKE = 62;
    public static final int SPECIALIZED = 63;
    public static final int WTEK = 64;
    public static final int PHYSICAL_ENTERPRISES = 65;
    public static final int NORTH_POLE_ENGINEERING = 66;
    public static final int BKOOL = 67;
    public static final int CATEYE = 68;
    public static final int STAGES_CYCLING = 69;
    public static final int SIGMASPORT = 70;
    public static final int TOMTOM = 71;
    public static final int PERIPEDAL = 72;
    public static final int WATTBIKE = 73;
    public static final int MOXY = 76;
    public static final int CICLOSPORT = 77;
    public static final int POWERBAHN = 78;
    public static final int ACORN_PROJECTS_APS = 79;
    public static final int LIFEBEAM = 80;
    public static final int BONTRAGER = 81;
    public static final int WELLGO = 82;
    public static final int SCOSCHE = 83;
    public static final int MAGURA = 84;
    public static final int WOODWAY = 85;
    public static final int ELITE = 86;
    public static final int NIELSEN_KELLERMAN = 87;
    public static final int DK_CITY = 88;
    public static final int TACX = 89;
    public static final int DIRECTION_TECHNOLOGY = 90;
    public static final int MAGTONIC = 91;
    public static final int _1PARTCARBON = 92;
    public static final int INSIDE_RIDE_TECHNOLOGIES = 93;
    public static final int SOUND_OF_MOTION = 94;
    public static final int STRYD = 95;
    public static final int ICG = 96; // Indoorcycling Group
    public static final int MIPULSE = 97;
    public static final int BSX_ATHLETICS = 98;
    public static final int LOOK = 99;
    public static final int CAMPAGNOLO_SRL = 100;
    public static final int BODY_BIKE_SMART = 101;
    public static final int PRAXISWORKS = 102;
    public static final int LIMITS_TECHNOLOGY = 103; // Limits Technology Ltd.
    public static final int TOPACTION_TECHNOLOGY = 104; // TopAction Technology Inc.
    public static final int COSINUSS = 105;
    public static final int FITCARE = 106;
    public static final int MAGENE = 107;
    public static final int GIANT_MANUFACTURING_CO = 108;
    public static final int TIGRASPORT = 109; // Tigrasport
    public static final int SALUTRON = 110;
    public static final int TECHNOGYM = 111;
    public static final int BRYTON_SENSORS = 112;
    public static final int LATITUDE_LIMITED = 113;
    public static final int SOARING_TECHNOLOGY = 114;
    public static final int IGPSPORT = 115;
    public static final int THINKRIDER = 116;
    public static final int GOPHER_SPORT = 117;
    public static final int WATERROWER = 118;
    public static final int ORANGETHEORY = 119;
    public static final int INPEAK = 120;
    public static final int KINETIC = 121;
    public static final int JOHNSON_HEALTH_TECH = 122;
    public static final int POLAR_ELECTRO = 123;
    public static final int SEESENSE = 124;
    public static final int NCI_TECHNOLOGY = 125;
    public static final int IQSQUARE = 126;
    public static final int LEOMO = 127;
    public static final int IFIT_COM = 128;
    public static final int COROS_BYTE = 129;
    public static final int VERSA_DESIGN = 130;
    public static final int CHILEAF = 131;
    public static final int CYCPLUS = 132;
    public static final int GRAVAA_BYTE = 133;
    public static final int SIGEYI = 134;
    public static final int COOSPO = 135;
    public static final int DEVELOPMENT = 255;
    public static final int HEALTHANDLIFE = 257;
    public static final int LEZYNE = 258;
    public static final int SCRIBE_LABS = 259;
    public static final int ZWIFT = 260;
    public static final int WATTEAM = 261;
    public static final int RECON = 262;
    public static final int FAVERO_ELECTRONICS = 263;
    public static final int DYNOVELO = 264;
    public static final int STRAVA = 265;
    public static final int PRECOR = 266; // Amer Sports
    public static final int BRYTON = 267;
    public static final int SRAM = 268;
    public static final int NAVMAN = 269; // MiTAC Global Corporation (Mio Technology)
    public static final int COBI = 270; // COBI GmbH
    public static final int SPIVI = 271;
    public static final int MIO_MAGELLAN = 272;
    public static final int EVESPORTS = 273;
    public static final int SENSITIVUS_GAUGE = 274;
    public static final int PODOON = 275;
    public static final int LIFE_TIME_FITNESS = 276;
    public static final int FALCO_E_MOTORS = 277; // Falco eMotors Inc.
    public static final int MINOURA = 278;
    public static final int CYCLIQ = 279;
    public static final int LUXOTTICA = 280;
    public static final int TRAINER_ROAD = 281;
    public static final int THE_SUFFERFEST = 282;
    public static final int FULLSPEEDAHEAD = 283;
    public static final int VIRTUALTRAINING = 284;
    public static final int FEEDBACKSPORTS = 285;
    public static final int OMATA = 286;
    public static final int VDO = 287;
    public static final int MAGNETICDAYS = 288;
    public static final int HAMMERHEAD = 289;
    public static final int KINETIC_BY_KURT = 290;
    public static final int SHAPELOG = 291;
    public static final int DABUZIDUO = 292;
    public static final int JETBLACK = 293;
    public static final int COROS = 294;
    public static final int VIRTUGO = 295;
    public static final int VELOSENSE = 296;
    public static final int CYCLIGENTINC = 297;
    public static final int TRAILFORKS = 298;
    public static final int MAHLE_EBIKEMOTION = 299;
    public static final int NURVV = 300;
    public static final int MICROPROGRAM = 301;
    public static final int ZONE5CLOUD = 302;
    public static final int GREENTEG = 303;
    public static final int YAMAHA_MOTORS = 304;
    public static final int WHOOP = 305;
    public static final int GRAVAA = 306;
    public static final int ONELAP = 307;
    public static final int MONARK_EXERCISE = 308;
    public static final int FORM = 309;
    public static final int DECATHLON = 310;
    public static final int SYNCROS = 311;
    public static final int ACTIGRAPHCORP = 5759;
    public static final int INVALID = Fit.UINT16_INVALID;

    private static final Map<Integer, String> stringMap;

    static {
        stringMap = new HashMap<Integer, String>();
        stringMap.put(GARMIN, "GARMIN");
        stringMap.put(GARMIN_FR405_ANTFS, "GARMIN_FR405_ANTFS");
        stringMap.put(ZEPHYR, "ZEPHYR");
        stringMap.put(DAYTON, "DAYTON");
        stringMap.put(IDT, "IDT");
        stringMap.put(SRM, "SRM");
        stringMap.put(QUARQ, "QUARQ");
        stringMap.put(IBIKE, "IBIKE");
        stringMap.put(SARIS, "SARIS");
        stringMap.put(SPARK_HK, "SPARK_HK");
        stringMap.put(TANITA, "TANITA");
        stringMap.put(ECHOWELL, "ECHOWELL");
        stringMap.put(DYNASTREAM_OEM, "DYNASTREAM_OEM");
        stringMap.put(NAUTILUS, "NAUTILUS");
        stringMap.put(DYNASTREAM, "DYNASTREAM");
        stringMap.put(TIMEX, "TIMEX");
        stringMap.put(METRIGEAR, "METRIGEAR");
        stringMap.put(XELIC, "XELIC");
        stringMap.put(BEURER, "BEURER");
        stringMap.put(CARDIOSPORT, "CARDIOSPORT");
        stringMap.put(A_AND_D, "A_AND_D");
        stringMap.put(HMM, "HMM");
        stringMap.put(SUUNTO, "SUUNTO");
        stringMap.put(THITA_ELEKTRONIK, "THITA_ELEKTRONIK");
        stringMap.put(GPULSE, "GPULSE");
        stringMap.put(CLEAN_MOBILE, "CLEAN_MOBILE");
        stringMap.put(PEDAL_BRAIN, "PEDAL_BRAIN");
        stringMap.put(PEAKSWARE, "PEAKSWARE");
        stringMap.put(SAXONAR, "SAXONAR");
        stringMap.put(LEMOND_FITNESS, "LEMOND_FITNESS");
        stringMap.put(DEXCOM, "DEXCOM");
        stringMap.put(WAHOO_FITNESS, "WAHOO_FITNESS");
        stringMap.put(OCTANE_FITNESS, "OCTANE_FITNESS");
        stringMap.put(ARCHINOETICS, "ARCHINOETICS");
        stringMap.put(THE_HURT_BOX, "THE_HURT_BOX");
        stringMap.put(CITIZEN_SYSTEMS, "CITIZEN_SYSTEMS");
        stringMap.put(MAGELLAN, "MAGELLAN");
        stringMap.put(OSYNCE, "OSYNCE");
        stringMap.put(HOLUX, "HOLUX");
        stringMap.put(CONCEPT2, "CONCEPT2");
        stringMap.put(SHIMANO, "SHIMANO");
        stringMap.put(ONE_GIANT_LEAP, "ONE_GIANT_LEAP");
        stringMap.put(ACE_SENSOR, "ACE_SENSOR");
        stringMap.put(BRIM_BROTHERS, "BRIM_BROTHERS");
        stringMap.put(XPLOVA, "XPLOVA");
        stringMap.put(PERCEPTION_DIGITAL, "PERCEPTION_DIGITAL");
        stringMap.put(BF1SYSTEMS, "BF1SYSTEMS");
        stringMap.put(PIONEER, "PIONEER");
        stringMap.put(SPANTEC, "SPANTEC");
        stringMap.put(METALOGICS, "METALOGICS");
        stringMap.put(_4IIIIS, "_4IIIIS");
        stringMap.put(SEIKO_EPSON, "SEIKO_EPSON");
        stringMap.put(SEIKO_EPSON_OEM, "SEIKO_EPSON_OEM");
        stringMap.put(IFOR_POWELL, "IFOR_POWELL");
        stringMap.put(MAXWELL_GUIDER, "MAXWELL_GUIDER");
        stringMap.put(STAR_TRAC, "STAR_TRAC");
        stringMap.put(BREAKAWAY, "BREAKAWAY");
        stringMap.put(ALATECH_TECHNOLOGY_LTD, "ALATECH_TECHNOLOGY_LTD");
        stringMap.put(MIO_TECHNOLOGY_EUROPE, "MIO_TECHNOLOGY_EUROPE");
        stringMap.put(ROTOR, "ROTOR");
        stringMap.put(GEONAUTE, "GEONAUTE");
        stringMap.put(ID_BIKE, "ID_BIKE");
        stringMap.put(SPECIALIZED, "SPECIALIZED");
        stringMap.put(WTEK, "WTEK");
        stringMap.put(PHYSICAL_ENTERPRISES, "PHYSICAL_ENTERPRISES");
        stringMap.put(NORTH_POLE_ENGINEERING, "NORTH_POLE_ENGINEERING");
        stringMap.put(BKOOL, "BKOOL");
        stringMap.put(CATEYE, "CATEYE");
        stringMap.put(STAGES_CYCLING, "STAGES_CYCLING");
        stringMap.put(SIGMASPORT, "SIGMASPORT");
        stringMap.put(TOMTOM, "TOMTOM");
        stringMap.put(PERIPEDAL, "PERIPEDAL");
        stringMap.put(WATTBIKE, "WATTBIKE");
        stringMap.put(MOXY, "MOXY");
        stringMap.put(CICLOSPORT, "CICLOSPORT");
        stringMap.put(POWERBAHN, "POWERBAHN");
        stringMap.put(ACORN_PROJECTS_APS, "ACORN_PROJECTS_APS");
        stringMap.put(LIFEBEAM, "LIFEBEAM");
        stringMap.put(BONTRAGER, "BONTRAGER");
        stringMap.put(WELLGO, "WELLGO");
        stringMap.put(SCOSCHE, "SCOSCHE");
        stringMap.put(MAGURA, "MAGURA");
        stringMap.put(WOODWAY, "WOODWAY");
        stringMap.put(ELITE, "ELITE");
        stringMap.put(NIELSEN_KELLERMAN, "NIELSEN_KELLERMAN");
        stringMap.put(DK_CITY, "DK_CITY");
        stringMap.put(TACX, "TACX");
        stringMap.put(DIRECTION_TECHNOLOGY, "DIRECTION_TECHNOLOGY");
        stringMap.put(MAGTONIC, "MAGTONIC");
        stringMap.put(_1PARTCARBON, "_1PARTCARBON");
        stringMap.put(INSIDE_RIDE_TECHNOLOGIES, "INSIDE_RIDE_TECHNOLOGIES");
        stringMap.put(SOUND_OF_MOTION, "SOUND_OF_MOTION");
        stringMap.put(STRYD, "STRYD");
        stringMap.put(ICG, "ICG");
        stringMap.put(MIPULSE, "MIPULSE");
        stringMap.put(BSX_ATHLETICS, "BSX_ATHLETICS");
        stringMap.put(LOOK, "LOOK");
        stringMap.put(CAMPAGNOLO_SRL, "CAMPAGNOLO_SRL");
        stringMap.put(BODY_BIKE_SMART, "BODY_BIKE_SMART");
        stringMap.put(PRAXISWORKS, "PRAXISWORKS");
        stringMap.put(LIMITS_TECHNOLOGY, "LIMITS_TECHNOLOGY");
        stringMap.put(TOPACTION_TECHNOLOGY, "TOPACTION_TECHNOLOGY");
        stringMap.put(COSINUSS, "COSINUSS");
        stringMap.put(FITCARE, "FITCARE");
        stringMap.put(MAGENE, "MAGENE");
        stringMap.put(GIANT_MANUFACTURING_CO, "GIANT_MANUFACTURING_CO");
        stringMap.put(TIGRASPORT, "TIGRASPORT");
        stringMap.put(SALUTRON, "SALUTRON");
        stringMap.put(TECHNOGYM, "TECHNOGYM");
        stringMap.put(BRYTON_SENSORS, "BRYTON_SENSORS");
        stringMap.put(LATITUDE_LIMITED, "LATITUDE_LIMITED");
        stringMap.put(SOARING_TECHNOLOGY, "SOARING_TECHNOLOGY");
        stringMap.put(IGPSPORT, "IGPSPORT");
        stringMap.put(THINKRIDER, "THINKRIDER");
        stringMap.put(GOPHER_SPORT, "GOPHER_SPORT");
        stringMap.put(WATERROWER, "WATERROWER");
        stringMap.put(ORANGETHEORY, "ORANGETHEORY");
        stringMap.put(INPEAK, "INPEAK");
        stringMap.put(KINETIC, "KINETIC");
        stringMap.put(JOHNSON_HEALTH_TECH, "JOHNSON_HEALTH_TECH");
        stringMap.put(POLAR_ELECTRO, "POLAR_ELECTRO");
        stringMap.put(SEESENSE, "SEESENSE");
        stringMap.put(NCI_TECHNOLOGY, "NCI_TECHNOLOGY");
        stringMap.put(IQSQUARE, "IQSQUARE");
        stringMap.put(LEOMO, "LEOMO");
        stringMap.put(IFIT_COM, "IFIT_COM");
        stringMap.put(COROS_BYTE, "COROS_BYTE");
        stringMap.put(VERSA_DESIGN, "VERSA_DESIGN");
        stringMap.put(CHILEAF, "CHILEAF");
        stringMap.put(CYCPLUS, "CYCPLUS");
        stringMap.put(GRAVAA_BYTE, "GRAVAA_BYTE");
        stringMap.put(SIGEYI, "SIGEYI");
        stringMap.put(COOSPO, "COOSPO");
        stringMap.put(DEVELOPMENT, "DEVELOPMENT");
        stringMap.put(HEALTHANDLIFE, "HEALTHANDLIFE");
        stringMap.put(LEZYNE, "LEZYNE");
        stringMap.put(SCRIBE_LABS, "SCRIBE_LABS");
        stringMap.put(ZWIFT, "ZWIFT");
        stringMap.put(WATTEAM, "WATTEAM");
        stringMap.put(RECON, "RECON");
        stringMap.put(FAVERO_ELECTRONICS, "FAVERO_ELECTRONICS");
        stringMap.put(DYNOVELO, "DYNOVELO");
        stringMap.put(STRAVA, "STRAVA");
        stringMap.put(PRECOR, "PRECOR");
        stringMap.put(BRYTON, "BRYTON");
        stringMap.put(SRAM, "SRAM");
        stringMap.put(NAVMAN, "NAVMAN");
        stringMap.put(COBI, "COBI");
        stringMap.put(SPIVI, "SPIVI");
        stringMap.put(MIO_MAGELLAN, "MIO_MAGELLAN");
        stringMap.put(EVESPORTS, "EVESPORTS");
        stringMap.put(SENSITIVUS_GAUGE, "SENSITIVUS_GAUGE");
        stringMap.put(PODOON, "PODOON");
        stringMap.put(LIFE_TIME_FITNESS, "LIFE_TIME_FITNESS");
        stringMap.put(FALCO_E_MOTORS, "FALCO_E_MOTORS");
        stringMap.put(MINOURA, "MINOURA");
        stringMap.put(CYCLIQ, "CYCLIQ");
        stringMap.put(LUXOTTICA, "LUXOTTICA");
        stringMap.put(TRAINER_ROAD, "TRAINER_ROAD");
        stringMap.put(THE_SUFFERFEST, "THE_SUFFERFEST");
        stringMap.put(FULLSPEEDAHEAD, "FULLSPEEDAHEAD");
        stringMap.put(VIRTUALTRAINING, "VIRTUALTRAINING");
        stringMap.put(FEEDBACKSPORTS, "FEEDBACKSPORTS");
        stringMap.put(OMATA, "OMATA");
        stringMap.put(VDO, "VDO");
        stringMap.put(MAGNETICDAYS, "MAGNETICDAYS");
        stringMap.put(HAMMERHEAD, "HAMMERHEAD");
        stringMap.put(KINETIC_BY_KURT, "KINETIC_BY_KURT");
        stringMap.put(SHAPELOG, "SHAPELOG");
        stringMap.put(DABUZIDUO, "DABUZIDUO");
        stringMap.put(JETBLACK, "JETBLACK");
        stringMap.put(COROS, "COROS");
        stringMap.put(VIRTUGO, "VIRTUGO");
        stringMap.put(VELOSENSE, "VELOSENSE");
        stringMap.put(CYCLIGENTINC, "CYCLIGENTINC");
        stringMap.put(TRAILFORKS, "TRAILFORKS");
        stringMap.put(MAHLE_EBIKEMOTION, "MAHLE_EBIKEMOTION");
        stringMap.put(NURVV, "NURVV");
        stringMap.put(MICROPROGRAM, "MICROPROGRAM");
        stringMap.put(ZONE5CLOUD, "ZONE5CLOUD");
        stringMap.put(GREENTEG, "GREENTEG");
        stringMap.put(YAMAHA_MOTORS, "YAMAHA_MOTORS");
        stringMap.put(WHOOP, "WHOOP");
        stringMap.put(GRAVAA, "GRAVAA");
        stringMap.put(ONELAP, "ONELAP");
        stringMap.put(MONARK_EXERCISE, "MONARK_EXERCISE");
        stringMap.put(FORM, "FORM");
        stringMap.put(DECATHLON, "DECATHLON");
        stringMap.put(SYNCROS, "SYNCROS");
        stringMap.put(ACTIGRAPHCORP, "ACTIGRAPHCORP");
    }


    /**
     * Retrieves the String Representation of the Value
     * @return The string representation of the value, or empty if unknown
     */
    public static String getStringFromValue( Integer value ) {
        if( stringMap.containsKey( value ) ) {
            return stringMap.get( value );
        }

        return "";
    }

    /**
     * Retrieves a value given a string representation
     * @return The value or INVALID if unkwown
     */
    public static Integer getValueFromString( String value ) {
        for( Map.Entry<Integer, String> entry : stringMap.entrySet() ) {
            if( entry.getValue().equals( value ) ) {
                return entry.getKey();
            }
        }

        return INVALID;
    }

}
