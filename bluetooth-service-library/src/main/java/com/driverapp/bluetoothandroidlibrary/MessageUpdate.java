package com.driverapp.bluetoothandroidlibrary;

public interface MessageUpdate {

    void updateBTConnected(boolean connected);

    void updateEngineRPM(String RPM);

    void updateVehicleSpeed(String speed);

    void updateAirFlow(String airFlow);

    void updateIntakeAirTemp(String intakeAirTemp);

    void updateCoolantTemp(String coolant);

    void updateDistance(String distance);

    void updateErrorMessage(String message);
}