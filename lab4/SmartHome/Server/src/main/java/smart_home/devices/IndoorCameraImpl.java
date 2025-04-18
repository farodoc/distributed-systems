package smart_home.devices;

import SmartHomeIce.IndoorCamera;
import SmartHomeIce.NoPowerError;
import com.zeroc.Ice.Current;

public class IndoorCameraImpl extends CameraImpl implements IndoorCamera {
    private boolean motionDetectionMode = false;

    @Override
    public boolean isMotionDetectionModeOn(Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        return motionDetectionMode;
    }

    @Override
    public void setMotionDetectionMode(boolean mode, Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        this.motionDetectionMode = mode;
    }
}
