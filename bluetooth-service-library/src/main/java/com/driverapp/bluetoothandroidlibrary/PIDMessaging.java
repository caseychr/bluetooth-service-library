package com.driverapp.bluetoothandroidlibrary;

import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class PIDMessaging {

    private static final String RESET = "ATZ";
    private static final String DISTANCE = "0131";
    private static final String ENGINE_RPM = "010C";
    private static final String VEHICLE_SPEED = "010D";
    private static final String COOLANT_TEMP = "0105";
    private static final String AIR_FLOW = "0110";

    static int num = 0;
    static boolean initialized = false;
    static List<String> mMyCommands;

    public PIDMessaging() {

    }

    public static void initCommands(){
        mMyCommands = new ArrayList<>();
        mMyCommands.add(RESET);mMyCommands.add(DISTANCE);
        mMyCommands.add(ENGINE_RPM);mMyCommands.add(VEHICLE_SPEED);
        mMyCommands.add(COOLANT_TEMP);mMyCommands.add(AIR_FLOW);
        sendInitCommands();
    }

    private static void sendMessage(String msg) {
        msg = msg + "\r";
        byte[] send = msg.getBytes();
        BluetoothService.getInstance().write(send);
    }

    public static void sendInitCommands() {
        if(!mMyCommands.isEmpty()){
            if(num < 0)
                num = 0;
            String send = mMyCommands.get(num);
            sendMessage(send);
            if(num == mMyCommands.size()-1){
                num = 0;
                initialized = true;
                sendDefaultCommands();
            }
            else
                num++;
        }
    }

    private static void sendDefaultCommands() {
        if(!mMyCommands.isEmpty()){
            if(num < 0)
                num = 0;
            String send = mMyCommands.get(num);
            sendMessage(send);
            if(num == mMyCommands.size()-1){
                num = 0;
            }
            else
                num++;
        }
    }

    public static void retrieveMyMessage(Message message) {
        String tmpmsg = clearMsg(message);

        checkPids(tmpmsg);

        try {

            analyzePIDs(tmpmsg);
        } catch (Exception e) {
        }
        sendDefaultCommands();
    }

    private static String clearMsg(Message msg) {
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

    private static void checkPids(String tmpmsg) {
        if (tmpmsg.indexOf("41") != -1) {
            int index = tmpmsg.indexOf("41");

            String pidmsg = tmpmsg.substring(index, tmpmsg.length());

            if (pidmsg.contains("4100")) {

                return;
            }
        }
    }

    private static void analyzePIDs(String values) {
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
                BluetoothDeviceController.calcValues(PID, A, B);
            }
        }
    }
}
