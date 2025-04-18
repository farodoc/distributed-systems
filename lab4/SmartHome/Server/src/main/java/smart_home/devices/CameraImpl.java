package smart_home.devices;

import SmartHomeIce.*;
import com.zeroc.Ice.Current;
import org.apache.commons.lang3.Range;

public class CameraImpl extends SmartDeviceImpl implements Camera {
    protected static final int MOVE_INTERVAL = 5;
    protected static final int ZOOM_INTERVAL = 1;
    protected CameraSettings settings;
    protected final Range<Integer> panRange;
    protected final Range<Integer> tiltRange;
    protected final Range<Float> zoomRange;
    protected boolean isRecording = false;

    public CameraImpl(CameraSettings settings,
                      Range<Integer> panRange,
                      Range<Integer> tiltRange,
                      Range<Float> zoomRange)
    {
        this.settings = settings;
        this.panRange = panRange;
        this.tiltRange = tiltRange;
        this.zoomRange = zoomRange;
    }

    public CameraImpl() {
        this(new CameraSettings(0, 0, 0),
             Range.between(-60, 60),
             Range.between(-30, 30),
             Range.between(1f, 5f)
        );
    }

    @Override
    public void move(moveCommand command, Current current) throws DeviceError, DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        switch (command) {
            case PanLeft -> settings.pan = Math.max(settings.pan - MOVE_INTERVAL, panRange.getMinimum());
            case PanRight -> settings.pan = Math.min(settings.pan + MOVE_INTERVAL, panRange.getMaximum());
            case TiltUp -> settings.tilt = Math.min(settings.tilt + MOVE_INTERVAL, tiltRange.getMaximum());
            case TiltDown -> settings.tilt = Math.max(settings.tilt - MOVE_INTERVAL, tiltRange.getMinimum());
            case ZoomIn -> settings.zoom = Math.min(settings.zoom + ZOOM_INTERVAL, zoomRange.getMaximum());
            case ZoomOut -> settings.zoom = Math.max(settings.zoom - ZOOM_INTERVAL, zoomRange.getMinimum());
            default -> throw new DeviceError("Invalid camera move command");
        }

        printMessage("Camera moved: " + command + "\n"
                    + "Current position: " + settings.pan + "° pan, "
                    + settings.tilt + "° tilt, "
                    + settings.zoom + "x zoom",
                    current);
    }

    @Override
    public CameraSettings getCurrentSettings(Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        return settings;
    }

    @Override
    public void setCurrentSettings(CameraSettings settings, Current current) throws DeviceError, NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        if (settings.pan < panRange.getMinimum() || settings.pan > panRange.getMaximum()) {
            throw new DeviceError("Pan value out of range");
        }
        if (settings.tilt < tiltRange.getMinimum() || settings.tilt > tiltRange.getMaximum()) {
            throw new DeviceError("Tilt value out of range");
        }
        if (settings.zoom < zoomRange.getMinimum() || settings.zoom > zoomRange.getMaximum()) {
            throw new DeviceError("Zoom value out of range");
        }

        this.settings = settings;
    }

    @Override
    public boolean isRecording(Current current) throws DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        return isRecording;
    }

    @Override
    public void startRecording(Current current) throws DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        if (!isRecording) {
            isRecording = true;
            printMessage("Recording started", current);
        } else {
            printMessage("Camera is already recording", current);
        }
    }

    @Override
    public void stopRecording(Current current) throws DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        if (isRecording) {
            isRecording = false;
            printMessage("Recording stopped", current);
        } else {
            printMessage("Camera is not recording", current);
        }
    }
}
