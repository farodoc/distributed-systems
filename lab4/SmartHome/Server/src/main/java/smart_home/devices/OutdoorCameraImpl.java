package smart_home.devices;

import SmartHomeIce.*;
import com.zeroc.Ice.Current;

import java.util.List;

public class OutdoorCameraImpl extends CameraImpl implements OutdoorCamera {
    private boolean nightVisionMode = false;
    private boolean presetMode = false;
    private List<CameraSettings> presetSettingsList;
    private int currentPresetIndex = 0;

    @Override
    public boolean isNightVisionModeOn(Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        return nightVisionMode;
    }

    @Override
    public void setNightVisionMode(boolean mode, Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        this.nightVisionMode = mode;
    }

    @Override
    public boolean isPresetMovementModeOn(Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        return presetMode;
    }

    @Override
    public void setPresetMovementMode(boolean mode, Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        this.presetMode = mode;
    }

    @Override
    public CameraSettings[] getPresetSettingsList(Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        return presetSettingsList != null ? presetSettingsList.toArray(new CameraSettings[0]) : new CameraSettings[0];
    }

    @Override
    public void setPresetSettingsList(CameraSettings[] settingsList, Current current) throws DeviceError, NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        if (settingsList == null || settingsList.length == 0) {
            throw new DeviceError("Preset settings list cannot be null or empty");
        }

        for (int i = 0; i < settingsList.length; i++) {
            CameraSettings settings = settingsList[i];
            if (settings.pan < panRange.getMinimum() || settings.pan > panRange.getMaximum()) {
                throw new DeviceError("Pan value out of range in preset number " + i);
            }
            if (settings.tilt < tiltRange.getMinimum() || settings.tilt > tiltRange.getMaximum()) {
                throw new DeviceError("Tilt value out of range in preset number " + i);
            }
            if (settings.zoom < zoomRange.getMinimum() || settings.zoom > zoomRange.getMaximum()) {
                throw new DeviceError("Zoom value out of range in preset number " + i);
            }
        }

        this.presetSettingsList = List.of(settingsList);
    }

    @Override
    public void goToNextPreset(Current current) throws DeviceError, DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        if (!presetMode) {
            throw new DeviceError("Preset movement mode is disabled");
        }

        if (presetSettingsList == null || presetSettingsList.isEmpty()) {
            throw new DeviceError("No preset settings available");
        }

        currentPresetIndex = (currentPresetIndex + 1) % presetSettingsList.size();
        this.settings = presetSettingsList.get(currentPresetIndex);
    }

    @Override
    public void goToPreviousPreset(Current current) throws DeviceError, DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        if (!presetMode) {
            throw new DeviceError("Preset movement mode is disabled");
        }

        if (presetSettingsList == null || presetSettingsList.isEmpty()) {
            throw new DeviceError("No preset settings available");
        }

        currentPresetIndex = (currentPresetIndex - 1 + presetSettingsList.size()) % presetSettingsList.size();
        this.settings = presetSettingsList.get(currentPresetIndex);
    }
}
