package simplesocketserver;

import java.io.IOException;
import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;
import jdk.dio.uart.UART;
import jdk.dio.uart.UARTConfig;

//This is a simple wrapper class for the DIO library.
//This is solely to make explaining this project easier.
public class GPIOUtils implements PinListener {

    public static GPIOPin ConfigLEDPin(int pinNumber) {
        GPIOPin pin = null;
        try {
            GPIOPinConfig pinConfig = new GPIOPinConfig(DeviceConfig.DEFAULT,
                    pinNumber,
                    GPIOPinConfig.DIR_OUTPUT_ONLY,
                    GPIOPinConfig.MODE_OUTPUT_PUSH_PULL,
                    GPIOPinConfig.TRIGGER_NONE,
                    false);
            pin = (GPIOPin) DeviceManager.open(GPIOPin.class, pinConfig);
        } catch (IOException ioe) {
            System.out.println("IOException while opening device. Make sure you have the appropriate operating system permission to access GPIO devices." + ioe.getMessage());
        }
        return pin;
    }

    public static GPIOPin ConfigButtonPin(int pinNumber) {
        GPIOPin pin = null;
        try {
            GPIOPinConfig pinConfig = new GPIOPinConfig(DeviceConfig.DEFAULT,
                    pinNumber,
                    GPIOPinConfig.DIR_INPUT_ONLY,
                    DeviceConfig.DEFAULT,
                    GPIOPinConfig.TRIGGER_RISING_EDGE,
                    false);
            pin = (GPIOPin) DeviceManager.open(GPIOPin.class, pinConfig);

        } catch (IOException ioe) {
            System.out.println("IOException while opening device. Make sure you have the appropriate operating system permission to access GPIO devices." + ioe.getMessage());
        }
        return pin;
    }

    public static UART ConfigUART() {
        UART uart = null;
        int UART_DEVICE_ID = 100;    //set in /dio/config/dio.properties
        int UART_BAUD_RATE = 9600;
        int UART_DATA_BITS = UARTConfig.DATABITS_8;
        int UART_PARITY_BIT = UARTConfig.PARITY_NONE;
        int UART_STOP_BIT = UARTConfig.STOPBITS_1;

        try {

            uart = (UART) DeviceManager.open(UART_DEVICE_ID); // 40 is UART device ID
            uart.setBaudRate(UART_BAUD_RATE);
            uart.setDataBits(UART_DATA_BITS);
            uart.setParity(UART_PARITY_BIT);
            uart.setStopBits(UART_STOP_BIT);

        } catch (IOException ex) {
            System.out.println("IOException while opening device. Make sure you have the appropriate operating system permission to access GPIO devices." + ex.getMessage());
        }

        return uart;
    }

    @Override
    public void valueChanged(PinEvent pe) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
