package com.driverapp.bluetoothandroidlibrary.Refactor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.driverapp.bluetoothandroidlibrary.R;

import java.util.ArrayList;
import java.util.List;


/**
 * BluetoothController class invokes all other classes. This is the class the user needs to implement
 * in order for the library to work.
 */
public class BluetoothController {

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
        mMessageInquirer = new MessageInquirer();
        mMessageReceptor = new MessageReceptor();
        BluetoothConnector.sBluetoothDevice = device;
        BluetoothConnector.getInstance().mConnectionHandler = mHandler;
        if(timeframe.equals(mContext.getString(R.string.initial_read))){
            BluetoothConnector.READ_ONCE = true;
        } else if(timeframe.equals(mContext.getString(R.string.polling))){
            //TODO DEV implement Counter
            BluetoothConnector.getInstance()
                    .createCountdownTimer(R.integer.ten_seconds, R.integer.interval);
        }
    }

    /**
     * Checks for BluetoothService errors and initiates connection if no errors
     * (INVOKE THIS METHOD WHEN YOU WANT TO START READING)
     */
    public void init(Context context) {
        mContext = context;
        if(BluetoothAdapter.getDefaultAdapter() != null) {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                if (!BluetoothAdapter.getDefaultAdapter().getBondedDevices().isEmpty()) {
                    reportError(null);
                    List<BluetoothDevice> deviceList = new ArrayList<>();
                    deviceList.addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
                    BluetoothConnector.getInstance().connect();
                } else {
                    reportError(context.getString(R.string.error_bt_no_paired_devices));
                }
            } else {
                reportError(context.getString(R.string.error_bt_not_on));
            }
        } else {
            reportError(context.getString(R.string.error_bt_not_supported));
        }

    }

    /**
     * Check if we have received all necessary data and can stop writing to the device
     */
    private void checkStop() {
        if(!PIDS.getCoolantTemp().isEmpty() && !PIDS.getEngineRPM().isEmpty() &&
                !PIDS.getVehicleSpeed().isEmpty() && !PIDS.getAirFlow().isEmpty() && !PIDS.getDistance().isEmpty()){
            exit();
        }
    }

    /**
     * Closes BluetoothService socket
     */
    public static void exit() {
        BluetoothConnector.getInstance().stop();
    }

    /**
     * Updates user with any errors
     * @param message
     */
    public static void reportError(String message) {
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
