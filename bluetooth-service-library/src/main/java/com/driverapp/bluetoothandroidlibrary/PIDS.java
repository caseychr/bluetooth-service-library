package com.driverapp.bluetoothandroidlibrary;

import java.util.ArrayList;
import java.util.List;

public class PIDS {

    private static final String PID_RESET = "ATZ";
    private static final String PID_DISTANCE = "0131";
    private static final String PID_ENGINE_RPM = "010C";
    private static final String PID_VEHICLE_SPEED = "010D";
    private static final String PID_INTAKE_AIR_TEMP = "010F";
    private static final String PID_COOLANT_TEMP = "0105";
    private static final String PID_AIR_FLOW = "0110";

    private static String sCoolantTemp;
    private static String sEngineRPM;
    private static String sVehicleSpeed;
    private static String sAirFlow;
    private static String sIntakeAirTemp;
    private static String sDistance;
    private static String sErrorMessage;
    private static List<String> mPIDList;

    public static String getCoolantTemp() {
        return sCoolantTemp;
    }

    public static void setCoolantTemp(String coolantTemp) {
        sCoolantTemp = coolantTemp;
    }

    public static String getEngineRPM() {
        return sEngineRPM;
    }

    public static void setEngineRPM(String engineRPM) {
        sEngineRPM = engineRPM;
    }

    public static String getVehicleSpeed() {
        return sVehicleSpeed;
    }

    public static void setVehicleSpeed(String vehicleSpeed) {
        sVehicleSpeed = vehicleSpeed;
    }

    public static String getAirFlow() {
        return sAirFlow;
    }

    public static void setAirFlow(String airFlow) {
        sAirFlow = airFlow;
    }

    public static String getIntakeAirTemp() {
        return sIntakeAirTemp;
    }

    public static void setIntakeAirTemp(String intakeAirTemp) {
        sIntakeAirTemp = intakeAirTemp;
    }

    public static String getDistance() {
        return sDistance;
    }

    public static void setDistance(String distance) {
        sDistance = distance;
    }

    public static String getErrorMessage() {
        return sErrorMessage;
    }

    public static void setErrorMessage(String errorMessage) {
        sErrorMessage = errorMessage;
    }


    public static List<String> populatePIDs() {
        mPIDList = new ArrayList<>();
        mPIDList.add(PID_RESET);mPIDList.add(PID_DISTANCE);
        mPIDList.add(PID_ENGINE_RPM);mPIDList.add(PID_VEHICLE_SPEED);
        mPIDList.add(PID_COOLANT_TEMP);mPIDList.add(PID_AIR_FLOW);
        mPIDList.add(PID_INTAKE_AIR_TEMP);
        return mPIDList;
    }
}
