package com.driverapp.bluetoothandroidlibrary.Refactor;

import java.util.List;

public class MessageInquirer {

    int num = 0;
    boolean initialized = false;
    List<String> mCommands;

    public void initCommands(){
        mCommands = PIDS.populatePIDs();
        sendInitCommands();
    }

    private void sendMsg(String msg) {
        msg = msg + "\r";
        byte[] send = msg.getBytes();
        BluetoothConnector.getInstance().write(send);
    }

    public void sendInitCommands() {
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
