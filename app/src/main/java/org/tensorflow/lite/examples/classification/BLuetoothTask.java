package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.os.AsyncTask;

import Bluetooth.Bluetooth;

class BluetoothTask extends AsyncTask<Void, Void, Void> {

    Bluetooth ble = new Bluetooth();
    Context context;

//    BluetoothTask(Context context){
//        this.context = context;
//    }
    @Override
    protected Void doInBackground(Void... voids) {
        ble.connectBluetooth();
        ble.missionGo();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ble.disconnect("CE:2C:01:E0:21:9A");
    }
}
