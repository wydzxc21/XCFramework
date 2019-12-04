/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xc.framework.port.serial;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * Author：ZhangXuanChen
 * Time：2019/12/4 8:24
 * Description：SerialPortFinder
 */
public class SerialPortFinder {
    static SerialPortFinder mSerialPortFinder;
    private static final String TAG = "SerialPortFinder";
    private Vector<SerialDevice> mSerialDevices = null;

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 8:18
     * Description：getInstance
     */
    public static SerialPortFinder getInstance() {
        if (mSerialPortFinder == null) {
            mSerialPortFinder = new SerialPortFinder();
        }
        return mSerialPortFinder;
    }

    public class SerialDevice {
        private String mDriverName;
        private String mDeviceRoot;
        Vector<File> mDevices = null;

        public SerialDevice(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        public String getName() {
            return mDriverName;
        }

        public Vector<File> getSerialDevices() {
            if (mDevices == null) {
                mDevices = new Vector<File>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                if (files != null) {
                    int i;
                    for (i = 0; i < files.length; i++) {
                        if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
//                            Log.d(TAG, "Found new device: " + files[i]);
                            mDevices.add(files[i]);
                        }
                    }
                }
            }
            return mDevices;
        }


    }

    Vector<SerialDevice> getSerialDevices() throws IOException {
        if (mSerialDevices == null) {
            mSerialDevices = new Vector<SerialDevice>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null) {
                // Issue 3:
                // Since driver name may contain spaces, we do not extract driver name with split()
                String drivername = l.substring(0, 0x15).trim();
                String[] w = l.split(" +");
                if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
//                    Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length - 4]);
                    mSerialDevices.add(new SerialDevice(drivername, w[w.length - 4]));
                }
            }
            r.close();
        }
        return mSerialDevices;
    }

    public String[] getAllSerialDevices() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<SerialDevice> itdriv;
        try {
            itdriv = getSerialDevices().iterator();
            while (itdriv.hasNext()) {
                SerialDevice serialDevice = itdriv.next();
                Iterator<File> itdev = serialDevice.getSerialDevices().iterator();
                while (itdev.hasNext()) {
                    String device = itdev.next().getName();
                    String value = String.format("%s (%s)", device, serialDevice.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }


    public String[] getAllSerialDevicesPath() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<SerialDevice> itdriv;
        try {
            itdriv = getSerialDevices().iterator();
            while (itdriv.hasNext()) {
                SerialDevice serialDevice = itdriv.next();
                Iterator<File> itdev = serialDevice.getSerialDevices().iterator();
                while (itdev.hasNext()) {
                    String device = itdev.next().getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }
}
