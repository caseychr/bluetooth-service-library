package com.driverapp.bluetoothandroidlibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * com.driverapp.bluetoothandroidlibrary.BluetoothController class invokes all other classes. This is the class the user needs to implement
 * in order for the library to work.
 */
public class BluetoothController {
    private static final String TAG = "BluetoothController";

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Initialize send and receive classes for BT
    private MessageInquirer mMessageInquirer;
    private MessageReceptor mMessageReceptor;

    Context mContext;
    static MessageUpdate mMessageUpdate;


    public BluetoothController(String timeframe, BluetoothDevice device, MessageUpdate messageUpdate) {
        Log.i(TAG, "Timeframe: "+timeframe+", Device: "+device.getName());
        mMessageUpdate = messageUpdate;
        MessageReceptor.mUpdate = messageUpdate;
        mMessageInquirer = new MessageInquirer();
        mMessageReceptor = new MessageReceptor(messageUpdate);
        BluetoothConnector.sBluetoothDevice = device;
        BluetoothConnector.getInstance().mConnectionHandler = mHandler;
        if(timeframe.equals("INITIAL_READ")){
            BluetoothConnector.READ_ONCE = true;
        } else if(timeframe.equals("POLLING")){
            //TODO DEV implement Counter
            BluetoothConnector.getInstance()
                    .createCountdownTimer(
                            10000, 1000);
        }
    }

    /**
     * Checks for BluetoothService errors and initiates connection if no errors
     * (INVOKE THIS METHOD WHEN YOU WANT TO START READING)
     */
    public void init(Context context) {
        Log.i(TAG, "init");
        mContext = context;
        if(BluetoothAdapter.getDefaultAdapter() != null) {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                if (!BluetoothAdapter.getDefaultAdapter().getBondedDevices().isEmpty()) {
                    reportError(null);
                    List<BluetoothDevice> deviceList = new ArrayList<>();
                    deviceList.addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
                    mMessageUpdate.updateBTConnected(true);
                    BluetoothConnector.getInstance().connect();
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

    /**
     * Check if we have received all necessary data and can stop writing to the device
     */
    private void checkStop() {
        Log.i(TAG, "checkStop");
        if(PIDS.getDistance() != null && PIDS.getCoolantTemp() != null && PIDS.getEngineRPM() != null &&
                PIDS.getAirFlow() != null && PIDS.getVehicleSpeed() != null) {
            exit();
        }
    }

    /**
     * Closes BluetoothService socket
     */
    public static void exit() {
        mMessageUpdate.updateBTConnected(false);
        BluetoothConnector.getInstance().stop();
    }

    /**
     * Updates user with any errors
     * @param message
     */
    public static void reportError(String message) {
        Log.i(TAG, "reportError: "+message);
        if (message != null){
            PIDS.setErrorMessage(message);
            mMessageUpdate.updateErrorMessage(message);
        }
    }

    /**
     * Bluetooth handler passed in instructor
     */
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothConnector.STATE_CONNECTED:
                            mMessageInquirer.initCommands();
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    mMessageReceptor.retrieveMsg(msg);
                    mMessageInquirer.resendInitCommands();
                    checkStop();
                    break;
            }
        }
    };
}
