package com.driverapp.bluetoothandroidlibrary;

import android.os.Message;
import android.util.Log;

public class MessageReceptor {
    private static final String TAG = "MessageReceptor";

    static MessageUpdate mUpdate;

    public MessageReceptor(MessageUpdate messageUpdate) {
        mUpdate = messageUpdate;
    }

    /**
     * Get raw Message from Device
     * @param message
     */
    public static void retrieveMsg(Message message) {
        Log.i(TAG, "retrieveMsg: "+message.toString());
        String tmpmsg = cleanMsg(message);

        // Check that the message has what we need to interpret correctly
        checkPIDS(tmpmsg);

        try {

            parsePIDs(tmpmsg);
        } catch (Exception e) {
        }
    }

    public static String cleanMsg(Message msg) {
        String tmpmsg = msg.obj.toString();

        tmpmsg = tmpmsg.replace("null", "");
        tmpmsg = tmpmsg.replaceAll("\\s", ""); //removes all [ \t\n\x0B\f\r]
        tmpmsg = tmpmsg.replaceAll(">", "");
        tmpmsg = tmpmsg.replaceAll("SEARCHING...", "");
        tmpmsg = tmpmsg.replaceAll("ATZ", "");
        tmpmsg = tmpmsg.replaceAll("ATI", "");
        tmpmsg = tmpmsg.replaceAll("atz", "");
        tmpmsg = tmpmsg.replaceAll("ati", "");
        tmpmsg = tmpmsg.replaceAll("ATDP", "");
        tmpmsg = tmpmsg.replaceAll("atdp", "");
        tmpmsg = tmpmsg.replaceAll("ATRV", "");
        tmpmsg = tmpmsg.replaceAll("atrv", "");

        return tmpmsg;
    }

    //TODO can we remove this?
    private static void checkPIDS(String tmpmsg) {
        Log.i(TAG, "checkPIDS: "+tmpmsg);
        if (tmpmsg.indexOf("41") != -1) {
            int index = tmpmsg.indexOf("41");

            String pidmsg = tmpmsg.substring(index, tmpmsg.length());

            if (pidmsg.contains("4100")) {

                return;
            }
        }
    }

    /**
     * Strip and format the com.driverapp.bluetoothandroidlibrary.PIDS as needed to Calculate Correctly
     * @param values
     */
    private static void parsePIDs(String values) {
        Log.i(TAG, "parsePIDs: "+values);
        if(values.contains("UNABLETOCONNECT")) {
            Log.i(TAG, "UNABLETOCONNECT");
            BluetoothController.reportError("Unable to connect with Bluetooth Device");
        }
        int A = 0;
        int B = 0;
        int PID = 0;

        int index = values.indexOf("41");

        String tmpmsg = null;

        if (index != -1) {

            tmpmsg = values.substring(index, values.length());

            if (tmpmsg.substring(0, 2).equals("41")) {

                PID = Integer.parseInt(tmpmsg.substring(2, 4), 16);
                A = Integer.parseInt(tmpmsg.substring(4, 6), 16);
                if(tmpmsg.length()>6)
                    B = Integer.parseInt(tmpmsg.substring(6, 8), 16);
                calcValues(PID, A, B);
            }
        }
    }

    /**
     * Takes from AnalyzePIDs and correctly calculates and formats data for consumption
     * @param PID
     * @param A
     * @param B
     */
    public static void calcValues(int PID, int A, int B) {
        Log.i(TAG, "calcValues: "+PID+", "+A+", "+B);
        double val = 0;
        int intval = 0;
        int tempC = 0;

        switch (PID) {

            case 5://com.driverapp.bluetoothandroidlibrary.PIDS(05): Coolant Temperature

                // A-40
                tempC = A - 40;
                int coolantTemp = tempC;
                PIDS.setCoolantTemp(coolantTemp+"");
                mUpdate.updateCoolantTemp(PIDS.getCoolantTemp());
                break;

            case 12: //com.driverapp.bluetoothandroidlibrary.PIDS(0C): RPM

                //((A*256)+B)/4
                val = ((A * 256) + B) / 4;
                intval = (int) val;
                int rpmval = intval;
                PIDS.setEngineRPM(String.valueOf(rpmval / 100));
                mUpdate.updateEngineRPM(PIDS.getEngineRPM());

                break;


            case 13://com.driverapp.bluetoothandroidlibrary.PIDS(0D): KM

                // A
                PIDS.setVehicleSpeed(A+"");
                mUpdate.updateVehicleSpeed(PIDS.getVehicleSpeed());

                break;

            case 15://com.driverapp.bluetoothandroidlibrary.PIDS(0F): Intake Temperature

                // A - 40
                tempC = A - 40;
                int intakeairtemp = tempC;
                PIDS.setIntakeAirTemp(intakeairtemp+"");
                mUpdate.updateIntakeAirTemp(PIDS.getIntakeAirTemp());

                break;

            case 16://com.driverapp.bluetoothandroidlibrary.PIDS(10): Maf

                // ((256*A)+B) / 100  [g/s]
                val = ((256 * A) + B) / 100;
                intval = (int) val;
                PIDS.setAirFlow(intval+"");
                mUpdate.updateAirFlow(PIDS.getAirFlow());

                break;

            case 49://com.driverapp.bluetoothandroidlibrary.PIDS(31)

                //(256*A)+B km
                val = (A * 256) + B;
                intval = (int) val;
                PIDS.setDistance(intval+"");
                mUpdate.updateDistance(PIDS.getDistance());

                break;

            default:
        }
    }
}