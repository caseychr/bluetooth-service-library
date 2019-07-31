package com.driverapp.bluetoothandroidlibrary;

import static org.junit.Assert.*;

import android.bluetooth.BluetoothAdapter;
import android.os.Message;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PIDMessagingTest {


    @Before
    public void setup() {
    }

    @Before
    public void tearDown() {

    }

    @Test
    public void initCommandsTest() {
        PIDMessaging.initCommands();
        assertTrue(PIDMessaging.mMyCommands.size() == 6);
    }

    @Test
    public void clearMsgTest() {
        // TODO: Need to Mock Message
    }

    @Test
    public void checkPIDsTest() {

    }

    @Test
    public void analyzePIDsTest() {
    }

    @Test
    public void sendMessageTest() {

    }

    @Test
    public void sendInitCommandsTest() {

    }

    @Test
    public void sendDefaultCommandsTest() {

    }

    @Test
    public void retrieveMessageTest() {

    }

}