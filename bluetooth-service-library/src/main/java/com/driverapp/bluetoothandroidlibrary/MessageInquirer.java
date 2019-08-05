package com.driverapp.bluetoothandroidlibrary;

import android.util.Log;

import java.util.List;

public class MessageInquirer {
    private static final String TAG = "MessageInquirer";

    int num = 0;
    boolean initialized = false;
    List<String> mCommands;

    public void initCommands(){
        Log.i(TAG, "initCommands");
        mCommands = PIDS.populatePIDs();
        sendInitCommands();
    }

    private void sendMsg(String msg) {
        Log.i(TAG, "sendMsg: "+msg);
        msg = msg + "\r";
        byte[] send = msg.getBytes();
        BluetoothConnector.getInstance().write(send);
    }

    public void sendInitCommands() {
        Log.i(TAG, "sendInitCommands");
        if(!mCommands.isEmpty()){
            if(num < 0)
                num = 0;
            String send = mCommands.get(num);
            sendMsg(send);
            if(num == mCommands.size()-1){
                num = 0;
                initialized = true;
                resendInitCommands();
            }
            else
                num++;
        }
    }

    public void resendInitCommands() {
        Log.i(TAG, "resendInitCommands");
        if(!mCommands.isEmpty()){
            if(num < 0)
                num = 0;
            String send = mCommands.get(num);
            sendMsg(send);
            if(num == mCommands.size()-1){
                num = 0;
            }
            else
                num++;
        }
    }
}