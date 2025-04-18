package smart_home.devices;

import SmartHomeIce.COSensor;
import SmartHomeIce.DeviceError;
import SmartHomeIce.DeviceNotInOperationalState;
import SmartHomeIce.NoPowerError;
import com.zeroc.Ice.Current;

public class COSensorImpl extends SmartDeviceImpl implements COSensor {
    private static final int INITIAL_ALARM_THRESHOLD = 300;
    private int alarmThreshold = 300;

    @Override
    public boolean isCOLevelSafe(Current current) throws DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        return getCOlevel(current) < this.alarmThreshold;
    }

    @Override
    public int getCOlevel(Current current) throws DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        int coLevel = (int) (Math.random() * INITIAL_ALARM_THRESHOLD * 1.5);
        return coLevel;
    }

    @Override
    public void setCOAlarmThreshold(int threshold, Current current) throws DeviceError, NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        if (threshold < 0) {
            throw new DeviceError("Alarm ppm threshold cannot be negative");
        }

        this.alarmThreshold = threshold;
    }
}
