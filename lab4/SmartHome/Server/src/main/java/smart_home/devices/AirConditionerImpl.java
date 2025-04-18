package smart_home.devices;

import SmartHomeIce.*;
import com.zeroc.Ice.Current;

public class AirConditionerImpl extends SmartDeviceImpl implements AirConditioner {
    private final static int MIN_CELSIUS_TEMPERATURE = 16;
    private final static int MAX_CELSIUS_TEMPERATURE = 35;
    private final static int MIN_FAHRENHEIT_TEMPERATURE = 60;
    private final static int MAX_FAHRENHEIT_TEMPERATURE = 95;
    private ACSettings settings;
    private TemperatureUnit currentTemperatureUnit;

    public AirConditionerImpl(ACSettings settings, TemperatureUnit temperatureUnit) {
        this.settings = settings;
        this.currentTemperatureUnit = temperatureUnit;
    }

    public AirConditionerImpl() {
        this(new ACSettings(
                new Temperature(22.0f, TemperatureUnit.Celsius),
                FanSpeed.Medium,
                true
                ),
                TemperatureUnit.Celsius);
    }

    @Override
    public Temperature getCurrentTemperature(Current current) throws DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        float currentTemperature;

        if (currentTemperatureUnit == TemperatureUnit.Celsius) {
            currentTemperature = (float) (Math.random() * 15 + 15);
        } else {
            currentTemperature = (float) (Math.random() * 27 + 59);
        }

        return new Temperature(currentTemperature, currentTemperatureUnit);
    }

    @Override
    public Temperature getTargetTemperature(Current current) throws DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        return settings.targetTemperature;
    }

    @Override
    public double getHumidity(Current current) throws DeviceNotInOperationalState {
        if (getPowerState(current) != SmartHomeIce.PowerState.On) {
            throw new DeviceNotInOperationalState();
        }

        return Math.random() * 100;
    }

    @Override
    public TemperatureUnit getTemperatureUnit(Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        return currentTemperatureUnit;
    }

    @Override
    public void setTemperatureUnit(TemperatureUnit unit, Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        this.currentTemperatureUnit = unit;
    }

    @Override
    public ACSettings getSettings(Current current) throws NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        return settings;
    }

    @Override
    public void setSettings(ACSettings settings, Current current) throws DeviceError, NoPowerError {
        if (getPowerState(current) == SmartHomeIce.PowerState.Off) {
            throw new NoPowerError();
        }

        switch (settings.targetTemperature.unit) {
            case Celsius:
                if (settings.targetTemperature.value < MIN_CELSIUS_TEMPERATURE || settings.targetTemperature.value > MAX_CELSIUS_TEMPERATURE) {
                    throw new DeviceError("Celsius temperature out of range, must be between " + MIN_CELSIUS_TEMPERATURE + " and " + MAX_CELSIUS_TEMPERATURE);
                }
                break;
            case Fahrenheit:
                if (settings.targetTemperature.value < MIN_FAHRENHEIT_TEMPERATURE || settings.targetTemperature.value > MAX_FAHRENHEIT_TEMPERATURE) {
                    throw new DeviceError("Fahrenheit temperature out of range, must be between " + MIN_FAHRENHEIT_TEMPERATURE + " and " + MAX_FAHRENHEIT_TEMPERATURE);
                }
                break;
            default:
                throw new DeviceError("Unsupported temperature unit");
        }

        this.settings = settings;
    }
}
