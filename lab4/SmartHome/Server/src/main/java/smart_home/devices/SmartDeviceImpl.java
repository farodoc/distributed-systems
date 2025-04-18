package smart_home.devices;

import SmartHomeIce.PowerState;
import SmartHomeIce.SmartDevice;
import com.zeroc.Ice.Current;

public class SmartDeviceImpl implements SmartDevice {
    private PowerState powerState = PowerState.Standby;

    @Override
    public PowerState getPowerState(Current current) {
        return powerState;
    }

    @Override
    public void setPowerState(PowerState powerState, Current current) {
        this.powerState = powerState;
        printMessage("Power state changed to: " + powerState, current);
    }

    protected void printMessage(String message, Current current) {
        System.out.println(current.id + "> " + message);
    }
}
