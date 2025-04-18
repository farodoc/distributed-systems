#ifndef SMARTHOME_ICE
#define SMARTHOME_ICE

module SmartHomeIce
{
    exception DeviceError {
        string message;
    };

    exception NoPowerError {};
    exception DeviceNotInOperationalState {};

    enum PowerState { On, Off, Standby };

    interface SmartDevice {
        idempotent PowerState getPowerState();
        idempotent void setPowerState(PowerState state);
    };

    // Sensor
    interface COSensor extends SmartDevice {
        idempotent bool isCOLevelSafe() throws DeviceNotInOperationalState;
        idempotent int getCOlevel() throws DeviceNotInOperationalState; // returns CO level in ppm
        idempotent void setCOAlarmThreshold(int threshold) throws NoPowerError, DeviceError;
    };

    // Camera (PTZ)
    enum moveCommand { PanLeft, PanRight, TiltUp, TiltDown, ZoomIn, ZoomOut };

    struct CameraSettings {
        float pan;
        float tilt;
        float zoom;
    };

    sequence<CameraSettings> CameraSettingsList;

    interface Camera extends SmartDevice {
        void move(moveCommand command) throws DeviceNotInOperationalState, DeviceError;
        idempotent CameraSettings getCurrentSettings() throws NoPowerError;
        idempotent void setCurrentSettings(CameraSettings settings) throws NoPowerError, DeviceError;

        idempotent bool isRecording() throws DeviceNotInOperationalState;
        idempotent void startRecording() throws DeviceNotInOperationalState;
        idempotent void stopRecording() throws DeviceNotInOperationalState;
    };

    interface IndoorCamera extends Camera {
        idempotent bool isMotionDetectionModeOn() throws NoPowerError;
        idempotent void setMotionDetectionMode(bool mode) throws NoPowerError;
    };

    interface OutdoorCamera extends Camera {
        idempotent bool isNightVisionModeOn() throws NoPowerError;
        idempotent void setNightVisionMode(bool mode) throws NoPowerError;

        idempotent bool isPresetMovementModeOn() throws NoPowerError;
        idempotent void setPresetMovementMode(bool mode) throws NoPowerError;
        idempotent CameraSettingsList getPresetSettingsList() throws NoPowerError;
        idempotent void setPresetSettingsList(CameraSettingsList settingsList) throws NoPowerError, DeviceError;
        void goToNextPreset() throws DeviceNotInOperationalState, DeviceError;
        void goToPreviousPreset() throws DeviceNotInOperationalState, DeviceError;
    };


    // AC
    enum TemperatureUnit { Celsius, Fahrenheit };

    enum FanSpeed { Low, Medium, High, Auto };

    struct Temperature {
        float value;
        TemperatureUnit unit;
    };

    struct ACSettings {
        Temperature targetTemperature;
        FanSpeed fanSpeed;
        bool swingEnabled;
    };

    interface AirConditioner extends SmartDevice {
        idempotent Temperature getCurrentTemperature() throws DeviceNotInOperationalState;
        idempotent Temperature getTargetTemperature() throws DeviceNotInOperationalState;
        idempotent double getHumidity() throws DeviceNotInOperationalState;

        idempotent TemperatureUnit getTemperatureUnit() throws NoPowerError;
        idempotent void setTemperatureUnit(TemperatureUnit unit) throws NoPowerError;

        idempotent ACSettings getSettings() throws NoPowerError;
        idempotent void setSettings(ACSettings settings) throws NoPowerError, DeviceError;
    };
};

#endif

// slice2java slice/smarthome.ice --output-dir Server/src/main/java/
// slice2py slice/smarthome.ice --output-dir Client/
