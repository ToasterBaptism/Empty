package com.controllertuner.mapping;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.controllertuner.model.ControllerProfile;
import com.controllertuner.input.ControllerInputManager;

/**
 * Engine that processes raw controller input and applies user-defined configurations
 */
public class InputMappingEngine {
    
    private static final String TAG = "InputMappingEngine";
    
    private ControllerProfile currentProfile;
    private MappedInputListener listener;
    
    public interface MappedInputListener {
        void onMappedMotionEvent(MappedMotionEvent mappedEvent);
        void onMappedKeyEvent(MappedKeyEvent mappedEvent);
    }
    
    /**
     * Represents processed motion input with applied configurations
     */
    public static class MappedMotionEvent {
        public final MotionEvent originalEvent;
        public final InputDevice device;
        
        // Left stick values (after processing)
        public final float leftStickX;
        public final float leftStickY;
        public final float leftStickMagnitude;
        
        // Right stick values (after processing)
        public final float rightStickX;
        public final float rightStickY;
        public final float rightStickMagnitude;
        
        // Trigger values (after processing)
        public final float leftTrigger;
        public final float rightTrigger;
        
        // D-pad values
        public final float dpadX;
        public final float dpadY;
        
        // Raw values (before processing)
        public final float rawLeftStickX;
        public final float rawLeftStickY;
        public final float rawRightStickX;
        public final float rawRightStickY;
        public final float rawLeftTrigger;
        public final float rawRightTrigger;
        
        public MappedMotionEvent(MotionEvent originalEvent, InputDevice device,
                               float leftStickX, float leftStickY, float leftStickMagnitude,
                               float rightStickX, float rightStickY, float rightStickMagnitude,
                               float leftTrigger, float rightTrigger,
                               float dpadX, float dpadY,
                               float rawLeftStickX, float rawLeftStickY,
                               float rawRightStickX, float rawRightStickY,
                               float rawLeftTrigger, float rawRightTrigger) {
            this.originalEvent = originalEvent;
            this.device = device;
            this.leftStickX = leftStickX;
            this.leftStickY = leftStickY;
            this.leftStickMagnitude = leftStickMagnitude;
            this.rightStickX = rightStickX;
            this.rightStickY = rightStickY;
            this.rightStickMagnitude = rightStickMagnitude;
            this.leftTrigger = leftTrigger;
            this.rightTrigger = rightTrigger;
            this.dpadX = dpadX;
            this.dpadY = dpadY;
            this.rawLeftStickX = rawLeftStickX;
            this.rawLeftStickY = rawLeftStickY;
            this.rawRightStickX = rawRightStickX;
            this.rawRightStickY = rawRightStickY;
            this.rawLeftTrigger = rawLeftTrigger;
            this.rawRightTrigger = rawRightTrigger;
        }
    }
    
    /**
     * Represents processed key input with applied configurations
     */
    public static class MappedKeyEvent {
        public final KeyEvent originalEvent;
        public final InputDevice device;
        public final int keyCode;
        public final int action;
        public final boolean isPressed;
        public final boolean isRepeated;
        
        public MappedKeyEvent(KeyEvent originalEvent, InputDevice device) {
            this.originalEvent = originalEvent;
            this.device = device;
            this.keyCode = originalEvent.getKeyCode();
            this.action = originalEvent.getAction();
            this.isPressed = originalEvent.getAction() == KeyEvent.ACTION_DOWN;
            this.isRepeated = originalEvent.getRepeatCount() > 0;
        }
    }
    
    public InputMappingEngine() {
        // Initialize with default profile
        this.currentProfile = ControllerProfile.createDefaultProfile();
    }
    
    /**
     * Set the current controller profile
     */
    public void setProfile(ControllerProfile profile) {
        this.currentProfile = profile != null ? profile : ControllerProfile.createDefaultProfile();
    }
    
    /**
     * Get the current controller profile
     */
    public ControllerProfile getCurrentProfile() {
        return currentProfile;
    }
    
    /**
     * Set the listener for mapped input events
     */
    public void setListener(MappedInputListener listener) {
        this.listener = listener;
    }
    
    /**
     * Map stick input with current profile settings
     */
    public float[] mapStickInput(int stickId, float x, float y, ControllerProfile profile) {
        if (profile == null) {
            return new float[]{x, y};
        }
        
        float deadZone, sensitivity;
        boolean accelerationEnabled;
        float accelerationCurve, accelerationThreshold, maxAcceleration;
        
        if (stickId == ControllerInputManager.STICK_LEFT) {
            deadZone = profile.getLeftStickDeadZone();
            sensitivity = profile.getLeftStickSensitivity();
            accelerationEnabled = profile.isAccelerationEnabled();
            accelerationCurve = profile.getAccelerationCurve();
            accelerationThreshold = profile.getAccelerationThreshold();
            maxAcceleration = profile.getMaxAcceleration();
        } else {
            deadZone = profile.getRightStickDeadZone();
            sensitivity = profile.getRightStickSensitivity();
            accelerationEnabled = profile.isAccelerationEnabled() || profile.isRightStickAccelerationEnabled();
            accelerationCurve = profile.isRightStickAccelerationEnabled() ? 
                              profile.getRightStickAccelerationCurve() : profile.getAccelerationCurve();
            accelerationThreshold = profile.isRightStickAccelerationEnabled() ? 
                                  profile.getRightStickAccelerationThreshold() : profile.getAccelerationThreshold();
            maxAcceleration = profile.isRightStickAccelerationEnabled() ? 
                            profile.getRightStickMaxAcceleration() : profile.getMaxAcceleration();
        }
        
        float mappedX = processStickAxis(x, deadZone, sensitivity, accelerationEnabled, 
                                       accelerationCurve, accelerationThreshold, maxAcceleration);
        float mappedY = processStickAxis(y, deadZone, sensitivity, accelerationEnabled, 
                                       accelerationCurve, accelerationThreshold, maxAcceleration);
        
        return new float[]{mappedX, mappedY};
    }
    
    /**
     * Map trigger input with current profile settings
     */
    public float mapTriggerInput(int triggerId, float value, ControllerProfile profile) {
        if (profile == null) {
            return value;
        }
        
        return processTriggerAxis(value, profile.getTriggerDeadZone(), profile.getTriggerSensitivity());
    }
    
    /**
     * Process a motion event and apply current profile settings
     */
    public void processMotionEvent(MotionEvent event, InputDevice device) {
        if (currentProfile == null || listener == null) {
            return;
        }
        
        // Extract raw values
        float rawLeftStickX = event.getAxisValue(MotionEvent.AXIS_X);
        float rawLeftStickY = event.getAxisValue(MotionEvent.AXIS_Y);
        float rawRightStickX = event.getAxisValue(MotionEvent.AXIS_Z);
        float rawRightStickY = event.getAxisValue(MotionEvent.AXIS_RZ);
        float rawLeftTrigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        float rawRightTrigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        
        // D-pad values (usually don't need processing)
        float dpadX = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float dpadY = event.getAxisValue(MotionEvent.AXIS_HAT_Y);
        
        // Process left stick
        float leftStickX = processStickAxis(rawLeftStickX, 
                                          currentProfile.getLeftStickDeadZone(),
                                          currentProfile.getLeftStickSensitivity(),
                                          currentProfile.isAccelerationEnabled(),
                                          currentProfile.getAccelerationCurve(),
                                          currentProfile.getAccelerationThreshold(),
                                          currentProfile.getMaxAcceleration());
        
        float leftStickY = processStickAxis(rawLeftStickY,
                                          currentProfile.getLeftStickDeadZone(),
                                          currentProfile.getLeftStickSensitivity(),
                                          currentProfile.isAccelerationEnabled(),
                                          currentProfile.getAccelerationCurve(),
                                          currentProfile.getAccelerationThreshold(),
                                          currentProfile.getMaxAcceleration());
        
        float leftStickMagnitude = ControllerInputManager.getStickMagnitude(leftStickX, leftStickY);
        
        // Process right stick (with separate acceleration settings)
        boolean useRightStickAcceleration = currentProfile.isRightStickAccelerationEnabled();
        float rightStickAccelCurve = useRightStickAcceleration ? 
                                   currentProfile.getRightStickAccelerationCurve() : 
                                   currentProfile.getAccelerationCurve();
        float rightStickAccelThreshold = useRightStickAcceleration ? 
                                       currentProfile.getRightStickAccelerationThreshold() : 
                                       currentProfile.getAccelerationThreshold();
        float rightStickMaxAccel = useRightStickAcceleration ? 
                                 currentProfile.getRightStickMaxAcceleration() : 
                                 currentProfile.getMaxAcceleration();
        
        float rightStickX = processStickAxis(rawRightStickX,
                                           currentProfile.getRightStickDeadZone(),
                                           currentProfile.getRightStickSensitivity(),
                                           currentProfile.isAccelerationEnabled() || useRightStickAcceleration,
                                           rightStickAccelCurve,
                                           rightStickAccelThreshold,
                                           rightStickMaxAccel);
        
        float rightStickY = processStickAxis(rawRightStickY,
                                           currentProfile.getRightStickDeadZone(),
                                           currentProfile.getRightStickSensitivity(),
                                           currentProfile.isAccelerationEnabled() || useRightStickAcceleration,
                                           rightStickAccelCurve,
                                           rightStickAccelThreshold,
                                           rightStickMaxAccel);
        
        float rightStickMagnitude = ControllerInputManager.getStickMagnitude(rightStickX, rightStickY);
        
        // Process triggers
        float leftTrigger = processTriggerAxis(rawLeftTrigger,
                                             currentProfile.getTriggerDeadZone(),
                                             currentProfile.getTriggerSensitivity());
        
        float rightTrigger = processTriggerAxis(rawRightTrigger,
                                              currentProfile.getTriggerDeadZone(),
                                              currentProfile.getTriggerSensitivity());
        
        // Create mapped event
        MappedMotionEvent mappedEvent = new MappedMotionEvent(
                event, device,
                leftStickX, leftStickY, leftStickMagnitude,
                rightStickX, rightStickY, rightStickMagnitude,
                leftTrigger, rightTrigger,
                dpadX, dpadY,
                rawLeftStickX, rawLeftStickY,
                rawRightStickX, rawRightStickY,
                rawLeftTrigger, rawRightTrigger
        );
        
        // Notify listener
        listener.onMappedMotionEvent(mappedEvent);
    }
    
    /**
     * Process a key event
     */
    public void processKeyEvent(KeyEvent event, InputDevice device) {
        if (listener == null) {
            return;
        }
        
        MappedKeyEvent mappedEvent = new MappedKeyEvent(event, device);
        listener.onMappedKeyEvent(mappedEvent);
    }
    
    /**
     * Process a single stick axis value
     */
    private float processStickAxis(float rawValue, float deadZone, float sensitivity,
                                 boolean accelerationEnabled, float accelerationCurve,
                                 float accelerationThreshold, float maxAcceleration) {
        
        // Apply dead zone
        float value = ControllerInputManager.getAxisValue(null, 0, deadZone) != 0 ? 
                     applyDeadZone(rawValue, deadZone) : rawValue;
        
        // Apply sensitivity
        value *= sensitivity;
        
        // Apply acceleration if enabled
        if (accelerationEnabled) {
            value = ControllerInputManager.applyAcceleration(value, accelerationCurve, 
                                                           accelerationThreshold, maxAcceleration);
        }
        
        // Clamp to valid range
        return Math.max(-1.0f, Math.min(1.0f, value));
    }
    
    /**
     * Process a trigger axis value
     */
    private float processTriggerAxis(float rawValue, float deadZone, float sensitivity) {
        // Apply dead zone
        float value = applyDeadZone(rawValue, deadZone);
        
        // Apply sensitivity
        value *= sensitivity;
        
        // Clamp to valid range (triggers are 0-1)
        return Math.max(0.0f, Math.min(1.0f, value));
    }
    
    /**
     * Apply dead zone to an axis value
     */
    private float applyDeadZone(float value, float deadZone) {
        if (Math.abs(value) < deadZone) {
            return 0.0f;
        }
        
        // Scale the value to account for dead zone
        if (value > 0) {
            return (value - deadZone) / (1.0f - deadZone);
        } else {
            return (value + deadZone) / (1.0f - deadZone);
        }
    }
    
    /**
     * Get a description of the current mapping configuration
     */
    public String getMappingDescription() {
        if (currentProfile == null) {
            return "No profile loaded";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Profile: ").append(currentProfile.getName()).append("\n");
        sb.append("Left Stick Sensitivity: ").append((int)(currentProfile.getLeftStickSensitivity() * 100)).append("%\n");
        sb.append("Right Stick Sensitivity: ").append((int)(currentProfile.getRightStickSensitivity() * 100)).append("%\n");
        sb.append("Trigger Sensitivity: ").append((int)(currentProfile.getTriggerSensitivity() * 100)).append("%\n");
        sb.append("Left Stick Dead Zone: ").append((int)(currentProfile.getLeftStickDeadZone() * 100)).append("%\n");
        sb.append("Right Stick Dead Zone: ").append((int)(currentProfile.getRightStickDeadZone() * 100)).append("%\n");
        sb.append("Trigger Dead Zone: ").append((int)(currentProfile.getTriggerDeadZone() * 100)).append("%\n");
        
        if (currentProfile.isAccelerationEnabled()) {
            sb.append("Acceleration: Enabled\n");
            sb.append("Acceleration Curve: ").append(String.format("%.1f", currentProfile.getAccelerationCurve())).append("\n");
            sb.append("Acceleration Threshold: ").append((int)(currentProfile.getAccelerationThreshold() * 100)).append("%\n");
            sb.append("Max Acceleration: ").append(String.format("%.1fx", currentProfile.getMaxAcceleration())).append("\n");
        } else {
            sb.append("Acceleration: Disabled\n");
        }
        
        if (currentProfile.isRightStickAccelerationEnabled()) {
            sb.append("Right Stick Acceleration: Enabled\n");
            sb.append("Right Stick Acceleration Curve: ").append(String.format("%.1f", currentProfile.getRightStickAccelerationCurve())).append("\n");
            sb.append("Right Stick Acceleration Threshold: ").append((int)(currentProfile.getRightStickAccelerationThreshold() * 100)).append("%\n");
            sb.append("Right Stick Max Acceleration: ").append(String.format("%.1fx", currentProfile.getRightStickMaxAcceleration()));
        } else {
            sb.append("Right Stick Acceleration: Disabled");
        }
        
        return sb.toString();
    }
}