package com.driverapp.bluetoothandroidlibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceController {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    private static String mCoolantTemp;
    private static String mEngineRPM;
    private static String mVehicleSpeed;
    private static String mAirFlow;
    private static String mDistance;
    private static String mErrorMessage;

    static int coolantTemp;
    static int rpmval;
    static int intakeairtemp;

    static BluetoothDeviceView sView;

    public BluetoothDeviceController(String timeframe) {
        if(timeframe.equals(BluetoothService.FULFILL_INPUTS)){
            BluetoothService.READ_ONCE = true;
        } else if(timeframe.equals(BluetoothService.POLLING)){
            //TODO DEV implement Counter
            BluetoothService.getInstance()
                    .createCountdownTimer(BluetoothService.FIVE_MINUTES, BluetoothService.INTERVAL);
        }
    }

    public static String getmCoolantTemp() {
        return mCoolantTemp;
    }

    public static String getmEngineRPM() {
        return mEngineRPM;
    }

    public static String getmVehicleSpeed() {
        return mVehicleSpeed;
    }

    public static String getmAirFlow() {
        return mAirFlow;
    }

    public static String getmDistance() {
        return mDistance;
    }

    public static String getmErrorMessage() {
        return mErrorMessage;
    }

    /**
     * Checks for BluetoothService errors and initiates connection if no errors
     * (INVOKE THIS METHOD WHEN YOU WANT TO START READING)
     */
    public void init() {
        if(BluetoothAdapter.getDefaultAdapter() != null) {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                if (!BluetoothAdapter.getDefaultAdapter().getBondedDevices().isEmpty()) {
                    reportError(null);
                    List<BluetoothDevice> deviceList = new ArrayList<>();
                    deviceList.addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
                    BluetoothService.sBluetoothDevice = deviceList.get(0);
                    BluetoothService.getInstance().connect();
                } else {
                    reportError("RMBluetoothError: There are no devices paired.");
                }
            } else {
                reportError("RMBluetoothError: BluetoothService is not turned on.");
            }
        } else {
            reportError("RMBluetoothError: this device is not setup for BluetoothService.");
        }

    }

    private static void checkStop() {
        if(!mCoolantTemp.isEmpty() && !mEngineRPM.isEmpty() && !mVehicleSpeed.isEmpty() && !mAirFlow.isEmpty() && !mDistance.isEmpty()){
            exit();
        }
    }

    /**
     * Closes BluetoothService socket
     */
    public static void exit() {
        BluetoothService.getInstance().stop();
    }

    /**
     * Updates user with any errors
     * @param message
     */
    public static void reportError(String message) {
        if (message != null){
            mErrorMessage = message;
            sView.updateErrorMessage();
        }
    }

    public static void calcValues(int PID, int A, int B) {
        double val = 0;
        int intval = 0;
        int tempC = 0;

        switch (PID) {

            case 5://PID(05): Coolant Temperature

                // A-40
                tempC = A - 40;
                coolantTemp = tempC;
                mCoolantTemp = coolantTemp+" C°";
                sView.updateCoolant();

                break;

            case 12: //PID(0C): RPM

                //((A*256)+B)/4
                val = ((A * 256) + B) / 4;
                intval = (int) val;
                rpmval = intval;
                mEngineRPM = String.valueOf(rpmval / 100);
                sView.updateRPM();

                break;


            case 13://PID(0D): KM

                // A
                mVehicleSpeed = String.valueOf(A);
                sView.updateSpeed();

                break;

            case 15://PID(0F): Intake Temperature

                // A - 40
                tempC = A - 40;
                intakeairtemp = tempC;

                break;

            case 16://PID(10): Maf

                // ((256*A)+B) / 100  [g/s]
                val = ((256 * A) + B) / 100;
                intval = (int) val;
                mAirFlow = Integer.toString(intval)+" g/s";
                sView.updateAirFlow();

                break;

            case 49://PID(31)

                //(256*A)+B km
                val = (A * 256) + B;
                intval = (int) val;
                mDistance = Integer.toString(intval)+" km";
                sView.updateDistance();

                break;

            default:
        }
        checkStop();
    }

    public static final Handler mBtHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:

                            PIDMessaging.initCommands();
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    //TODO this is the problem. Fix retrieveMyMessage(msg);
                    PIDMessaging.retrieveMyMessage(msg);
                    break;
            }
        }
    };

}
