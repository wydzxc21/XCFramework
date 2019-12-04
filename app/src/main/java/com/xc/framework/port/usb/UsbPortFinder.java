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

package com.xc.framework.port.usb;


import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author：ZhangXuanChen
 * Time：2019/12/4 8:36
 * Description：UsbPortFinder
 */
public class UsbPortFinder {
    private static final String TAG = "UsbPortFinder";
    static UsbPortFinder mUsbPortFinder;

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 8:44
     * Description：getInstance
     */
    public static UsbPortFinder getInstance() {
        if (mUsbPortFinder == null) {
            mUsbPortFinder = new UsbPortFinder();
        }
        return mUsbPortFinder;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 8:33
     * Description：getAllUsbDevices
     */
    public List<UsbDevice> getAllUsbDevices(Context context) {
        List<UsbDevice> usbList = new ArrayList<UsbDevice>();
        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (mUsbManager != null) {
            Iterator<UsbDevice> iterator = mUsbManager.getDeviceList().values().iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    usbList.add(iterator.next());
                }
            }
        }
        return usbList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 8:42
     * Description：getUsbDevice
     */
    public UsbDevice getUsbDevice(Context context, int vid, int pid) {
        if (context != null && vid >= 0 && pid >= 0) {
            List<UsbDevice> usbList = getAllUsbDevices(context);
            if (usbList != null && !usbList.isEmpty()) {
                for (UsbDevice usb : usbList) {
                    if (usb.getVendorId() == vid && usb.getProductId() == pid) {
                        return usb;
                    }
                }
            }
        }
        return null;
    }
}
