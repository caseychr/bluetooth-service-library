package com.driverapp.bluetoothandroidlibrary.Refactor;

public interface MessageUpdate {

    void updateEngineRPM(String RPM);

    void updateVehicleSpeed(String speed);

    void updateAirFlow(String airFlow);

    void updateIntakeAirTemp(String intakeAirTemp);

    void updateCoolantTemp(String coolant);

    void updateDistance(String distance);

    void updateErrorMessage(String message);
}
