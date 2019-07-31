package com.driverapp.bluetoothandroidlibrary;

import static org.junit.Assert.*;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class BluetoothServiceTest {

    @Mock
    private BluetoothService.ConnectThread mConnectThread;
    @Mock
    private BluetoothService.ConnectedThread mConnectedThread;

    /*@Mock
    BluetoothService mBluetoothService;

    @Before
    public void setup() {
        mBluetoothService = BluetoothService.getInstance();
    }*/

    @After
    public void tearDown() {

    }

    @Test
    public void connectTest() {

    }

    @Test
    public void connectedTest() {

    }

    @Test
    public void writeTest() {

    }

    @Test
    public void stopTest() {

    }


}