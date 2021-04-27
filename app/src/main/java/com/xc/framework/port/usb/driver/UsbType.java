package com.xc.framework.port.usb.driver;

/**
 * @Date：2021/4/25
 * @Author：ZhangXuanChen
 * @Description：usb驱动枚举
 */
public enum UsbType {
    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：Qinheng CH34x, CH341A, CH340
     */
    Ch34x,
    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：FTDI FT232R, FT2232H, FT4232H, FT232H, FT230X, FT231X, FT234XD
     */
    Ftdi,
    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：Silabs CP210x, CP2105, CP2108, CP2102 and other CP210x single port devices
     */
    Cp21x,
    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：Prolific PL2303
     */
    Prolific,
    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：CDC driver, Arduino, Teensyduino, Atmel Lufa,Leaflabs Maple,ARM mbed
     */
    CdcAcm

}
