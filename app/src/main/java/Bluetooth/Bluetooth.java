package Bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tensorflow.lite.examples.classification.CameraActivity;
import org.tensorflow.lite.examples.classification.ClassifierActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.concurrent.TimeUnit;

/**
 * Try and connect to the bluetooth device
 * sending or writing bytes to arduino
 *
 * @author          Shakhawat Hossain
 * @version         1.0
 * @since           11/18/2019
 */

public class Bluetooth extends AppCompatActivity{

    private static final String TAG = "BLE_PI";

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static UUID SERVICE_UUID = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214");
    private static UUID arduino_UUID = UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214"); //switchCharacteristic

    private static String address = "CE:2C:01:E0:21:9A";
    private Map<String, Object> bluetoothGatts = new HashMap<>();

    private byte[] valueMove = new byte[]{0x00};
    private byte[] valueMoveRight = new byte[]{0x01};
    private byte[] valueMoveLeft = new byte[]{0x02};
    private byte[] valueToCenter = new byte[]{0x00};
    private byte[] valueGo = new byte[]{0x03};
    private byte[] valueStop = new byte[]{0x04};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Try and connect to the bluetooth device
     *
     * @param address
     * @author          Shakhawat Hossain
     * @version         1.0
     * @since           11/18/2019
     */
    // android phone connects to arduino using MAC add
    private boolean connect(String address){
        Log.i(TAG, "Connecting to " + address);
        if(mBluetoothAdapter == null || address == null){
            Log.i(TAG, "BluetoothAdapter is not initialized");
            return false;
        }

        // here mobile adapter is finding a bluetooth device based on MAC address
        // creating a GATT connection
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // We want to directly connect to the device, so we are setting the autoConnect parameter to true
        BluetoothGatt bluetoothGatt = device.connectGatt(this, true, mGattCallback);

        Log.i(TAG, "Connected to Arduino....");
        bluetoothGatts.put(address, bluetoothGatt);

        return true;
    }

    /**
     * Callback method
     *
     * @author          Shakhawat Hossain
     * @version         1.0
     * @since           11/18/2019
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            String address = gatt.gatDevice().getAddress();

            if(newState == BluetoothProfile.STATE_CONNECTED){
                gatt.discoverServices();
                Log.i(TAG, "Attempting to start service discovery: " + gatt.discoverServices());
            }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
//                onDisconnected(gatt);
                Log.i(TAG, "Disconnected from GATT server");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status ==BluetoothGatt.GATT_SUCCESS){
                // success, we can communicate with the device
                BluetoothGattCharacteristic characteristic =
                        gatt.getService(SERVICE_UUID).getCharacteristic(arduino_UUID);
                gatt.setCharacteristicNotification(characteristic, true);
                Log.i(TAG, "characteristic: " + characteristic);

                // calling method to insert byte data to move servo
                //writeCharacteristic(address, characteristic, valueMove);
            }else {
                // failure
                Log.i(TAG, "onServicesDiscovered failure: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if(status == BluetoothGatt.GATT_SUCCESS){
                writeCharacteristic(address, characteristic, valueMove);
            }
        }
    };

    // for multiple characterisrics we use this
    // we dont need this here
    // my arduino uuid has only one characteristic
    @Nullable
    private BluetoothGattCharacteristic findCharacteristic(String address, UUID characteristicUUID){
        BluetoothGatt bluetoothGatt = (BluetoothGatt) bluetoothGatts.get(address);

        if (bluetoothGatt == null) {
            return null;
        }

        for (BluetoothGattService service : bluetoothGatt.getServices()) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                Log.i(TAG, "Service: " + characteristic);
                return characteristic;
            }
        }
        return null;
    }

    /**
     * writing bytes to arduino
     *
     * @param value
     * @param characteristic
     * @param address
     * @author          Shakhawat Hossain
     * @version         1.0
     * @since           11/18/2019
     */
    //this method adds value to the characteristic so we that we can control arduino
    protected boolean writeCharacteristic(String address, @NotNull BluetoothGattCharacteristic characteristic, byte[] value){

        BluetoothGatt bluetoothGatt = (BluetoothGatt) bluetoothGatts.get(address);

        Log.i(TAG, "Existing written value: " + characteristic.getValue());
        if (bluetoothGatt != null) {
            if(value != null) {
                characteristic.setValue(value);
                Log.i(TAG, "writeCharacteristic sets value" + characteristic.getValue());
                return bluetoothGatt.writeCharacteristic(characteristic);
            }
        }
        return false;
    }

    /**
     * method to disconnect Arduino from Phone
     *
     * @param address
     * @author          Shakhawat Hossain
     * @version         1.0
     * @since           11/18/2019
     */
    // method to disconnect Arduino from Phone
    public void disconnect(String address){
        if(mBluetoothAdapter == null){
            Log.i(TAG, "Not connected to Arduino");
        }
        BluetoothGatt bluetoothGatt = (BluetoothGatt) bluetoothGatts.get(address);
        if(bluetoothGatt != null){
            bluetoothGatt.disconnect();
            bluetoothGatts.remove(address);
            Log.i(TAG, "Disconnected from arduino");
        }
    }

    /**
     * moving servo when connected
     *
     * @author          Shakhawat Hossain
     * @version         1.0
     * @since           11/18/2019
     */
    // method to move
    // needs to implement parameter - object found
    // will call it mission
    public void missionGo(){
        moveForward();
        waitTime(25);
        moveRight();
        waitTime(7);
        moveCenter();
        waitTime(4);
        moveForward();
        waitTime(20);
        moveLeft();
        waitTime(7);
        moveCenter();
        waitTime(3);
        moveForward();
        waitTime(19);
        stopRover(true);

    }

    public void moveForward(){
        boolean pass = false;
        while (!pass){
            try{
                valueMove = valueGo;
                writeCharacteristic(address, findCharacteristic(address, arduino_UUID), valueMove );
                pass = true;
            }catch (Exception e){
                Log.i(TAG, "writeCharacteristic ERROR");
                pass = false;
            }
        }
    }

    public void moveLeft(){
        boolean pass = false;
        while (!pass) {
            try {
                valueMove = valueMoveLeft;
                writeCharacteristic(address, findCharacteristic(address, arduino_UUID), valueMove);
                Log.i(TAG, "Rover Should Stop, with a value passed: " + valueStop);
                pass = true;
            } catch (Exception e) {
                Log.i(TAG, "writeCharacteristic ERROR_moveLeft");
                pass = false;
            }
        }
    }

    public void moveRight(){
        boolean pass = false;
        while (!pass) {
            try {
                valueMove = valueMoveRight;
                writeCharacteristic(address, findCharacteristic(address, arduino_UUID), valueMove);
                Log.i(TAG, "Rover Should Stop, with a value passed: " + valueStop);
                pass = true;
            } catch (Exception e) {
                Log.i(TAG, "writeCharacteristic ERROR_moveRight");
                pass = false;
            }
        }
    }

    public void moveCenter(){
        boolean pass = false;
        while (!pass) {
            try {
                valueMove = valueToCenter;
                writeCharacteristic(address, findCharacteristic(address, arduino_UUID), valueMove);
                Log.i(TAG, "Rover Should Stop, with a value passed: " + valueStop);
                pass = true;
            } catch (Exception e) {
                Log.i(TAG, "writeCharacteristic ERROR_moveRight");
                pass = false;
            }
        }
    }

    public void stopRover(boolean value){
        if(value) {
            boolean pass = false;
            while (!pass) {
                try {
                    System.out.println("TEST STOP ROVER METHOD");
                    valueMove = valueStop;
                    writeCharacteristic(address, findCharacteristic(address, arduino_UUID), valueStop);
                    Log.i(TAG, "Rover Should Stop, with a value passed: " + valueStop);
                    pass = true;
                } catch (Exception e) {
                    Log.i(TAG, "writeCharacteristic ERROR_Stop");
                    pass = false;
                }
            }
        }
    }

    public void connectBluetooth(){
        connect(address);
    }

    public void waitTime(int sec){
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
