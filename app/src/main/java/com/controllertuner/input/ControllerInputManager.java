package com.controllertuner.input;

import android.content.Context;
import android.hardware.input.InputManager;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages controller input detection and processing
 */
public class ControllerInputManager implements InputManager.InputDeviceListener {
    
    private static final String TAG = "ControllerInputManager";
    
    // Stick constants
    public static final int STICK_LEFT = 0;
    public static final int STICK_RIGHT = 1;
    
    // Trigger constants
    public static final int TRIGGER_LEFT = 0;
    public static final int TRIGGER_RIGHT = 1;
    
    // Button constants
    public static final int BUTTON_A = KeyEvent.KEYCODE_BUTTON_A;
    public static final int BUTTON_B = KeyEvent.KEYCODE_BUTTON_B;
    public static final int BUTTON_X = KeyEvent.KEYCODE_BUTTON_X;
    public static final int BUTTON_Y = KeyEvent.KEYCODE_BUTTON_Y;
    public static final int BUTTON_L1 = KeyEvent.KEYCODE_BUTTON_L1;
    public static final int BUTTON_R1 = KeyEvent.KEYCODE_BUTTON_R1;
    public static final int BUTTON_L3 = KeyEvent.KEYCODE_BUTTON_THUMBL;
    public static final int BUTTON_R3 = KeyEvent.KEYCODE_BUTTON_THUMBR;
    public static final int BUTTON_START = KeyEvent.KEYCODE_BUTTON_START;
    public static final int BUTTON_SELECT = KeyEvent.KEYCODE_BUTTON_SELECT;
    public static final int DPAD_UP = KeyEvent.KEYCODE_DPAD_UP;
    public static final int DPAD_DOWN = KeyEvent.KEYCODE_DPAD_DOWN;
    public static final int DPAD_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
    public static final int DPAD_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
    
    private final Context context;
    private final InputManager inputManager;
    private final List<ControllerInputListener> listeners;
    private final List<InputListener> inputListeners;
    private final List<InputDevice> connectedControllers;
    
    private InputDevice currentController;
    private boolean isListening = false;
    
    public interface ControllerInputListener {
        void onControllerConnected(InputDevice controller);
        void onControllerDisconnected(InputDevice controller);
        void onMotionEvent(MotionEvent event, InputDevice controller);
        void onKeyEvent(KeyEvent event, InputDevice controller);
    }
    
    public interface InputListener {
        void onStickInput(int stickId, float x, float y);
        void onTriggerInput(int triggerId, float value);
        void onButtonInput(int buttonId, boolean pressed);
        void onControllerConnectionChanged(boolean connected);
    }
    
    public ControllerInputManager(Context context) {
        this.context = context;
        this.inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        this.listeners = new CopyOnWriteArrayList<>();
        this.inputListeners = new CopyOnWriteArrayList<>();
        this.connectedControllers = new ArrayList<>();
    }
    
    /**
     * Start listening for controller input
     */
    public void startListening() {
        if (!isListening) {
            inputManager.registerInputDeviceListener(this, null);
            scanForControllers();
            isListening = true;
        }
    }
    
    /**
     * Stop listening for controller input
     */
    public void stopListening() {
        if (isListening) {
            inputManager.unregisterInputDeviceListener(this);
            isListening = false;
        }
    }
    
    /**
     * Add a listener for controller events
     */
    public void addListener(ControllerInputListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener for controller events
     */
    public void removeListener(ControllerInputListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Add an input listener for processed input events
     */
    public void addListener(InputListener listener) {
        inputListeners.add(listener);
    }
    
    /**
     * Remove an input listener
     */
    public void removeListener(InputListener listener) {
        inputListeners.remove(listener);
    }
    
    /**
     * Get the currently active controller
     */
    public InputDevice getCurrentController() {
        return currentController;
    }
    
    /**
     * Get list of all connected controllers
     */
    public List<InputDevice> getConnectedControllers() {
        return new ArrayList<>(connectedControllers);
    }
    
    /**
     * Set the active controller
     */
    public void setCurrentController(InputDevice controller) {
        this.currentController = controller;
    }
    
    /**
     * Check if any controller is connected
     */
    public boolean isControllerConnected() {
        return currentController != null && !connectedControllers.isEmpty();
    }
    
    /**
     * Get the name of the current controller
     */
    public String getControllerName() {
        if (currentController != null) {
            return currentController.getName();
        }
        return "No Controller";
    }
    
    /**
     * Check if a device is a game controller
     */
    public static boolean isGameController(InputDevice device) {
        if (device == null) return false;
        
        int sources = device.getSources();
        
        // Check if device has gamepad or joystick sources
        return (sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
               (sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
    }
    
    /**
     * Get controller type description
     */
    public static String getControllerType(InputDevice device) {
        if (device == null) return "Unknown";
        
        String name = device.getName().toLowerCase();
        
        if (name.contains("xbox")) {
            return "Xbox Controller";
        } else if (name.contains("playstation") || name.contains("dualshock") || name.contains("dualsense")) {
            return "PlayStation Controller";
        } else if (name.contains("nintendo") || name.contains("pro controller")) {
            return "Nintendo Controller";
        } else if (name.contains("8bitdo")) {
            return "8BitDo Controller";
        } else {
            return "Generic Controller";
        }
    }
    
    /**
     * Scan for currently connected controllers
     */
    private void scanForControllers() {
        connectedControllers.clear();
        
        int[] deviceIds = inputManager.getInputDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice device = inputManager.getInputDevice(deviceId);
            if (isGameController(device)) {
                connectedControllers.add(device);
                
                // Set as current controller if none is set
                if (currentController == null) {
                    currentController = device;
                }
                
                // Notify listeners
                for (ControllerInputListener listener : listeners) {
                    listener.onControllerConnected(device);
                }
            }
        }
    }
    
    /**
     * Process motion events from controllers
     */
    public boolean processMotionEvent(MotionEvent event) {
        InputDevice device = event.getDevice();
        if (isGameController(device)) {
            // Notify raw listeners
            for (ControllerInputListener listener : listeners) {
                listener.onMotionEvent(event, device);
            }
            
            // Process and notify input listeners
            processInputFromMotionEvent(event);
            
            return true;
        }
        return false;
    }
    
    /**
     * Process motion event and extract stick/trigger input
     */
    private void processInputFromMotionEvent(MotionEvent event) {
        // Extract stick values
        float leftStickX = event.getAxisValue(MotionEvent.AXIS_X);
        float leftStickY = event.getAxisValue(MotionEvent.AXIS_Y);
        float rightStickX = event.getAxisValue(MotionEvent.AXIS_Z);
        float rightStickY = event.getAxisValue(MotionEvent.AXIS_RZ);
        
        // Extract trigger values
        float leftTrigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        float rightTrigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        
        // Notify input listeners
        for (InputListener listener : inputListeners) {
            listener.onStickInput(STICK_LEFT, leftStickX, leftStickY);
            listener.onStickInput(STICK_RIGHT, rightStickX, rightStickY);
            listener.onTriggerInput(TRIGGER_LEFT, leftTrigger);
            listener.onTriggerInput(TRIGGER_RIGHT, rightTrigger);
        }
    }
    
    /**
     * Process key events from controllers
     */
    public boolean processKeyEvent(KeyEvent event) {
        InputDevice device = event.getDevice();
        if (isGameController(device)) {
            // Notify raw listeners
            for (ControllerInputListener listener : listeners) {
                listener.onKeyEvent(event, device);
            }
            
            // Process and notify input listeners
            processInputFromKeyEvent(event);
            
            return true;
        }
        return false;
    }
    
    /**
     * Process key event and extract button input
     */
    private void processInputFromKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        boolean pressed = event.getAction() == KeyEvent.ACTION_DOWN;
        
        // Notify input listeners for button events
        for (InputListener listener : inputListeners) {
            listener.onButtonInput(keyCode, pressed);
        }
    }
    
    @Override
    public void onInputDeviceAdded(int deviceId) {
        InputDevice device = inputManager.getInputDevice(deviceId);
        if (isGameController(device)) {
            connectedControllers.add(device);
            
            // Set as current controller if none is set
            if (currentController == null) {
                currentController = device;
            }
            
            // Notify listeners
            for (ControllerInputListener listener : listeners) {
                listener.onControllerConnected(device);
            }
            
            // Notify input listeners
            for (InputListener listener : inputListeners) {
                listener.onControllerConnectionChanged(true);
            }
        }
    }
    
    @Override
    public void onInputDeviceRemoved(int deviceId) {
        // Find and remove the device from our list
        InputDevice removedDevice = null;
        for (InputDevice device : connectedControllers) {
            if (device.getId() == deviceId) {
                removedDevice = device;
                break;
            }
        }
        
        if (removedDevice != null) {
            connectedControllers.remove(removedDevice);
            
            // If this was the current controller, switch to another one
            if (currentController != null && currentController.getId() == deviceId) {
                currentController = connectedControllers.isEmpty() ? null : connectedControllers.get(0);
            }
            
            // Notify listeners
            for (ControllerInputListener listener : listeners) {
                listener.onControllerDisconnected(removedDevice);
            }
            
            // Notify input listeners
            for (InputListener listener : inputListeners) {
                listener.onControllerConnectionChanged(false);
            }
        }
    }
    
    @Override
    public void onInputDeviceChanged(int deviceId) {
        // Handle device configuration changes
        InputDevice device = inputManager.getInputDevice(deviceId);
        if (isGameController(device)) {
            // Update device in our list
            for (int i = 0; i < connectedControllers.size(); i++) {
                if (connectedControllers.get(i).getId() == deviceId) {
                    connectedControllers.set(i, device);
                    break;
                }
            }
            
            // Update current controller if it's the changed device
            if (currentController != null && currentController.getId() == deviceId) {
                currentController = device;
            }
        }
    }
    
    /**
     * Get axis value with dead zone applied
     */
    public static float getAxisValue(MotionEvent event, int axis, float deadZone) {
        float value = event.getAxisValue(axis);
        
        // Apply dead zone
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
     * Get stick magnitude (distance from center)
     */
    public static float getStickMagnitude(float x, float y) {
        return (float) Math.sqrt(x * x + y * y);
    }
    
    /**
     * Apply acceleration curve to input value
     */
    public static float applyAcceleration(float value, float curve, float threshold, float maxAcceleration) {
        float absValue = Math.abs(value);
        
        if (absValue < threshold) {
            // Below threshold, use linear response
            return value;
        }
        
        // Above threshold, apply acceleration curve
        float normalizedValue = (absValue - threshold) / (1.0f - threshold);
        float acceleratedValue = (float) Math.pow(normalizedValue, curve);
        acceleratedValue = threshold + acceleratedValue * (1.0f - threshold) * maxAcceleration;
        
        // Clamp to maximum value
        acceleratedValue = Math.min(acceleratedValue, 1.0f);
        
        return value >= 0 ? acceleratedValue : -acceleratedValue;
    }
}