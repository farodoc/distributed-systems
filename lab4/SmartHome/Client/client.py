import sys
import Ice
import SmartHomeIce
from typing import Dict, List
# import smarthome_ice as SmartHomeIce


def run_client(communicator: Ice.CommunicatorI):
    try:
        base1 = communicator.propertyToProxy("IndoorCamera1.Proxy")
        base2 = communicator.propertyToProxy("OutdoorCamera1.Proxy")
        base3 = communicator.propertyToProxy("COSensor2.Proxy")
        base4 = communicator.propertyToProxy("AirConditioner1.Proxy")
        base5 = communicator.propertyToProxy("AirConditioner2.Proxy")
        
        indoor_camera = SmartHomeIce.IndoorCameraPrx.checkedCast(base1)
        outdoor_camera = SmartHomeIce.OutdoorCameraPrx.checkedCast(base2)
        co_sensor = SmartHomeIce.COSensorPrx.checkedCast(base3)
        air_conditioner1 = SmartHomeIce.AirConditionerPrx.checkedCast(base4)
        air_conditioner2 = SmartHomeIce.AirConditionerPrx.checkedCast(base5)

        if not (indoor_camera and outdoor_camera and co_sensor and air_conditioner1 and air_conditioner2):
            raise RuntimeError("Invalid proxy")
        
        print("Connection established successfully.")
        
        devices: Dict[str, SmartHomeIce.SmartDevicePrx] = {
            "IndoorCamera": indoor_camera,
            "OutdoorCamera": outdoor_camera,
            "COSensor": co_sensor,
            "AirConditioner1": air_conditioner1,
            "AirConditioner2": air_conditioner2
        }
        
        running = True
        while running:
            print("\n=====< SMART HOME CONTROL SYSTEM >=====")
            print("1. Power control")
            print("2. Camera controls")
            print("3. CO Sensor controls")
            print("4. Air Conditioner controls")
            print("0. Exit")
            
            choice = input("\n>> ").strip()
            
            if choice == "0":
                running = False
                print("Exiting...")
            
            elif choice == "1":
                while True:
                    print("\n-----< POWER CONTROL >-----")
                    print("Select device:")

                    for i, device_name in enumerate(devices.keys(), 1):
                        try:
                            power_state = devices[device_name].getPowerState()
                            print(f"{i}. {device_name} - Current state: {power_state}")
                        except Exception as e:
                            print(f"{i}. {device_name} - Error: {e}")

                    print("0. <=")
                    
                    device_choice = input("\n>> ").strip()

                    if device_choice == "0":
                        break
                    
                    try:
                        device_idx = int(device_choice) - 1
                        device_name = list(devices.keys())[device_idx]
                        device = devices[device_name]
                        
                        print(f"\nSelect power state for {device_name}:")
                        print("1. On")
                        print("2. Off") 
                        print("3. Standby")
                        
                        state_choice = input("\n>> ").strip()

                        state_map = {
                            "1": SmartHomeIce.PowerState.On,
                            "2": SmartHomeIce.PowerState.Off,
                            "3": SmartHomeIce.PowerState.Standby
                        }
                        
                        if state_choice in state_map:
                            device.setPowerState(state_map[state_choice])
                            print(f"Power state changed successfully for {device_name}")
                        else:
                            print("Invalid choice")
                            
                    except (ValueError, IndexError) as e:
                        print(f"Invalid selection: {e}")
                    except Ice.UserException as e:
                        print(f"Ice error: {e}")
            
            elif choice == "2":
                while True:
                    print("\n-----< CAMERA CONTROLS >-----")
                    print("Select camera:")
                    print("1. Indoor Camera")
                    print("2. Outdoor Camera")
                    print("0. <=")
                    
                    camera_choice = input("\n>> ").strip()

                    if camera_choice == "0":
                        break
                    
                    camera = None

                    if camera_choice == "1":
                        camera = indoor_camera
                        camera_name = "Indoor Camera"
                    elif camera_choice == "2":
                        camera = outdoor_camera
                        camera_name = "Outdoor Camera"
                    else:
                        print("Invalid choice")
                        continue

                    while True:
                        print(f"\n{camera_name} Controls:")
                        print("1. Start recording")
                        print("2. Stop recording")
                        print("3. Get camera settings")
                        print("4. Move camera")
                        print("5. Special functions")
                        print("0. <=")
                        
                        action = input("\n>> ").strip()

                        if action == "0":
                            break
                        
                        try:
                            if action == "1":
                                camera.startRecording()
                                print("Recording started")
                            elif action == "2":
                                camera.stopRecording()
                                print("Recording stopped")
                            elif action == "3":
                                settings = camera.getCurrentSettings()
                                print(settings)
                            elif action == "4":
                                while True:
                                    print("\nMove command:")
                                    print("1. Pan Left")
                                    print("2. Pan Right")
                                    print("3. Tilt Up")
                                    print("4. Tilt Down")
                                    print("5. Zoom In")
                                    print("6. Zoom Out")
                                    print("0. <=")
                                    
                                    move_choice = input("\n>> ").strip()

                                    if move_choice == "0":
                                        break
                                    
                                    move_map = {
                                        "1": SmartHomeIce.moveCommand.PanLeft,
                                        "2": SmartHomeIce.moveCommand.PanRight,
                                        "3": SmartHomeIce.moveCommand.TiltUp,
                                        "4": SmartHomeIce.moveCommand.TiltDown,
                                        "5": SmartHomeIce.moveCommand.ZoomIn,
                                        "6": SmartHomeIce.moveCommand.ZoomOut
                                    }
                                    
                                    if move_choice in move_map:
                                        camera.move(move_map[move_choice])
                                        current_pos = camera.getCurrentSettings()
                                        print(current_pos)
                                    else:
                                        print("Invalid move command")
                            
                            elif action == "5":
                                if camera_choice == "1":  # Indoor Camera
                                    while True:
                                        print("\nIndoor Camera Special Functions:")
                                        print("1. Get motion detection mode status")
                                        print("2. Toggle motion detection mode")
                                        print("0. <=")
                                        
                                        special_choice = input("\n>> ").strip()

                                        if special_choice == "0":
                                            break
                                        elif special_choice == "1":
                                            motion_detection_mode_status = indoor_camera.isMotionDetectionModeOn()
                                            print(f"Motion detection mode is {'ON' if motion_detection_mode_status else 'OFF'}")
                                        elif special_choice == "2":
                                            current = indoor_camera.isMotionDetectionModeOn()
                                            indoor_camera.setMotionDetectionMode(not current)
                                            print(f"Motion detection set to: {not current}")
                                        else:
                                            print("Invalid choice")
                                
                                elif camera_choice == "2":  # Outdoor Camera
                                    while True:
                                        print("\nOutdoor Camera Special Functions:")
                                        print("1. Get night vision mode status")
                                        print("2. Toggle night vision mode")
                                        print("3. Get preset movement mode status")
                                        print("4. Toggle preset movement mode")
                                        print("5. Get preset settings list")
                                        print("6. [EXAMPLE ONLY] Set preset settings")
                                        print("7. Go to previous preset position")
                                        print("8. Go to next preset position")
                                        print("9. Get current camera settings")
                                        print("0. <=")

                                        special_choice = input("\n>> ").strip()

                                        if special_choice == "0":
                                            break

                                        try:
                                            if special_choice == "1":
                                                night_vision_mode_status = outdoor_camera.isNightVisionModeOn()
                                                print(f"Night vision mode is {'ON' if night_vision_mode_status else 'OFF'}")
                                            elif special_choice == "2":
                                                current = outdoor_camera.isNightVisionModeOn()
                                                outdoor_camera.setNightVisionMode(not current)
                                                print(f"Night vision set to: {not current}")
                                            elif special_choice == "3":
                                                preset_movement_mode_status = outdoor_camera.isPresetMovementModeOn()
                                                print(f"Preset movement mode is {'ON' if preset_movement_mode_status else 'OFF'}")
                                            elif special_choice == "4":
                                                current = outdoor_camera.isPresetMovementModeOn()
                                                outdoor_camera.setPresetMovementMode(not current)
                                                print(f"Preset movement set to: {not current}")
                                            elif special_choice == "5":
                                                preset_settings = outdoor_camera.getPresetSettingsList()
                                                print("Preset settings:")
                                                print("----------------")
                                                for setting in preset_settings:
                                                    print(setting)
                                                    print("----------------")
                                            elif special_choice == "6":
                                                preset_positions_list: List[SmartHomeIce.CameraSettings] = [
                                                    SmartHomeIce.CameraSettings(
                                                        pan=11.0,
                                                        tilt=5.0,
                                                        zoom=1.0,
                                                    ),
                                                    SmartHomeIce.CameraSettings(
                                                        pan=22.0,
                                                        tilt=10.0,
                                                        zoom=2.0,
                                                    ),
                                                    SmartHomeIce.CameraSettings(
                                                        pan=33.0,
                                                        tilt=7.0,
                                                        zoom=5.0,
                                                    ),
                                                ]
                                                outdoor_camera.setPresetSettingsList(preset_positions_list)
                                                print("Preset settings updated successfully")
                                            elif special_choice == "7":
                                                outdoor_camera.goToPreviousPreset()
                                                camera_pos = outdoor_camera.getCurrentSettings()
                                                print("Moved to previous preset position")
                                                print(camera_pos)
                                            elif special_choice == "8":
                                                outdoor_camera.goToNextPreset()
                                                camera_pos = outdoor_camera.getCurrentSettings()
                                                print("Moved to next preset position")
                                                print(camera_pos)
                                            elif special_choice == "9":
                                                current_settings = outdoor_camera.getCurrentSettings()
                                                print("Current camera settings:")
                                                print(current_settings)
                                        except Ice.UserException as e:
                                            print(f"Error: {e}")
                        except Ice.UserException as e:
                            print(f"Error: {e}")
            
            elif choice == "3":
                while True:
                    print("\n-----< CO SENSOR CONTROLS >-----")
                    print("1. Check if CO level is safe")
                    print("2. Get current CO level (ppm)")
                    print("3. Set CO alarm threshold")
                    print("0. <=")
                    
                    action = input("\n>> ").strip()

                    if action == "0":
                        break
                    
                    try:
                        if action == "1":
                            is_safe = co_sensor.isCOLevelSafe()
                            print(f"CO level is {'safe' if is_safe else 'UNSAFE!'}")
                        elif action == "2":
                            level = co_sensor.getCOlevel()
                            print(f"Current CO level: {level} ppm")
                        elif action == "3":
                            threshold = input("Enter new threshold value (ppm): ").strip()
                            try:
                                threshold_val = int(threshold)
                                co_sensor.setCOAlarmThreshold(threshold_val)
                                print(f"CO alarm threshold set to {threshold_val} ppm")
                            except ValueError:
                                print("Invalid threshold value, must be an integer")
                        else:
                            print("Invalid choice")
                    except Ice.UserException as e:
                        print(f"Error: {e}")
            
            elif choice == "4":
                while True:
                    print("\n-----< AIR CONDITIONER CONTROLS >-----")
                    print("Select air conditioner:")
                    print("1. Air Conditioner 1")
                    print("2. Air Conditioner 2")
                    print("0. <=")
                    
                    ac_choice = input("\n>> ").strip()

                    if ac_choice == "0":
                        break
                    
                    ac = None

                    if ac_choice == "1":
                        ac = air_conditioner1
                        ac_name = "Air Conditioner 1"
                    elif ac_choice == "2":
                        ac = air_conditioner2
                        ac_name = "Air Conditioner 2"
                    else:
                        print("Invalid choice")
                        continue
                    
                    while True:
                        print(f"\n{ac_name} Controls:")
                        print("1. Get current temperature")
                        print("2. Get target temperature")
                        print("3. Get humidity")
                        print("4. Get settings")
                        print("5. Change settings")
                        print("6. Change temperature unit")
                        print("0. <=")
                        
                        action = input("\n>> ").strip()

                        if action == "0":
                            break
                        
                        try:
                            if action == "1":
                                temp = ac.getCurrentTemperature()
                                print(f"Current temperature: {temp}")
                            elif action == "2":
                                temp = ac.getTargetTemperature()
                                print(f"Target temperature: {temp}")
                            elif action == "3":
                                humidity = ac.getHumidity()
                                print(f"Current humidity: {humidity}%")
                            elif action == "4":
                                settings = ac.getSettings()
                                print("Current settings:")
                                print(settings)
                            elif action == "5":
                                current_settings = ac.getSettings()
                                print(f"\nCurrent settings: {current_settings}")
                                
                                print("\nChange settings:")
                                try:
                                    print("\nSelect temperature unit:")
                                    print("1. Celsius (°C)")
                                    print("2. Fahrenheit (°F)")

                                    temp_unit = input("\n>> ").strip()

                                    if temp_unit not in ["1", "2"]:
                                        print("Invalid choice")
                                        continue

                                    temp_unit_map = {
                                        "1": SmartHomeIce.TemperatureUnit.Celsius,
                                        "2": SmartHomeIce.TemperatureUnit.Fahrenheit
                                    }

                                    temp_value = float(input("New temperature value: ").strip())
                                    
                                    print("\nFan speed:")
                                    print("1. Low")
                                    print("2. Medium")
                                    print("3. High")
                                    print("4. Auto")

                                    fan_choice = input("\n>> ").strip()

                                    fan_map = {
                                        "1": SmartHomeIce.FanSpeed.Low,
                                        "2": SmartHomeIce.FanSpeed.Medium,
                                        "3": SmartHomeIce.FanSpeed.High,
                                        "4": SmartHomeIce.FanSpeed.Auto
                                    }
                                    
                                    if fan_choice not in fan_map:
                                        print("Invalid fan speed, using Auto")
                                        fan_speed = SmartHomeIce.FanSpeed.Auto
                                    else:
                                        fan_speed = fan_map[fan_choice]
                                    
                                    swing = input("Enable swing (y/n): ").strip().lower() == "y"

                                    new_target_temp = SmartHomeIce.Temperature()
                                    new_target_temp.value = temp_value
                                    new_target_temp.unit = temp_unit_map[temp_unit]
                                    
                                    new_settings = SmartHomeIce.ACSettings()
                                    new_settings.targetTemperature = new_target_temp
                                    new_settings.fanSpeed = fan_speed
                                    new_settings.swingEnabled = swing
                                    
                                    ac.setSettings(new_settings)
                                    print("Settings updated successfully")
                                except ValueError as e:
                                    print(f"Invalid input: {e}")
                                except Ice.UserException as e:
                                    print(f"Error: {e}")
                            elif action == "6":
                                print("Select temperature unit:")
                                print("1. Celsius (°C)")
                                print("2. Fahrenheit (°F)")
                                print("0. <=")
                                
                                unit_choice = input("\n>> ").strip()

                                if unit_choice == "0":
                                    continue

                                if unit_choice == "1":
                                    ac.setTemperatureUnit(SmartHomeIce.TemperatureUnit.Celsius)
                                    print("Temperature unit set to Celsius")
                                elif unit_choice == "2":
                                    ac.setTemperatureUnit(SmartHomeIce.TemperatureUnit.Fahrenheit)
                                    print("Temperature unit set to Fahrenheit")
                                else:
                                    print("Invalid choice")
                            else:
                                print("Invalid choice")
                        except Ice.UserException as e:
                            print(f"Error: {e}")
            
            else:
                print("Invalid choice")
        
        return 0
        
    except Exception as e:
        print(f"Error during client execution: {e}")
        return 1

def main():
    status = 0
    communicator = None
    
    try:
        communicator = Ice.initialize(sys.argv)
        status = run_client(communicator)
    except Exception as e:
        print(f"Exception: {e}")
        status = 1
    finally:
        if communicator:
            try:
                communicator.destroy()
            except Exception as e:
                print(f"Exception during communicator destruction: {e}")
                status = 1
    
    return status

if __name__ == "__main__":
    sys.exit(main())
