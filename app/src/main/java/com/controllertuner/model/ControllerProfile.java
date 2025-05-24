package com.controllertuner.model;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

/**
 * Data model representing a controller configuration profile
 */
public class ControllerProfile {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("created_timestamp")
    private long createdTimestamp;
    
    @SerializedName("modified_timestamp")
    private long modifiedTimestamp;
    
    @SerializedName("is_default")
    private boolean isDefault;
    
    // Sensitivity settings
    @SerializedName("left_stick_sensitivity")
    private float leftStickSensitivity;
    
    @SerializedName("right_stick_sensitivity")
    private float rightStickSensitivity;
    
    @SerializedName("trigger_sensitivity")
    private float triggerSensitivity;
    
    // Dead zone settings
    @SerializedName("left_stick_dead_zone")
    private float leftStickDeadZone;
    
    @SerializedName("right_stick_dead_zone")
    private float rightStickDeadZone;
    
    @SerializedName("trigger_dead_zone")
    private float triggerDeadZone;
    
    // Acceleration settings
    @SerializedName("acceleration_enabled")
    private boolean accelerationEnabled;
    
    @SerializedName("acceleration_curve")
    private float accelerationCurve;
    
    @SerializedName("acceleration_threshold")
    private float accelerationThreshold;
    
    @SerializedName("max_acceleration")
    private float maxAcceleration;
    
    // Right stick specific acceleration
    @SerializedName("right_stick_acceleration_enabled")
    private boolean rightStickAccelerationEnabled;
    
    @SerializedName("right_stick_acceleration_curve")
    private float rightStickAccelerationCurve;
    
    @SerializedName("right_stick_acceleration_threshold")
    private float rightStickAccelerationThreshold;
    
    @SerializedName("right_stick_max_acceleration")
    private float rightStickMaxAcceleration;
    
    // Default constructor
    public ControllerProfile() {
        this.id = java.util.UUID.randomUUID().toString();
        this.createdTimestamp = System.currentTimeMillis();
        this.modifiedTimestamp = this.createdTimestamp;
        setDefaultValues();
    }
    
    // Constructor with name
    public ControllerProfile(String name) {
        this();
        this.name = name;
    }
    
    // Constructor with name and description
    public ControllerProfile(String name, String description) {
        this(name);
        this.description = description;
    }
    
    /**
     * Set default values for all settings
     */
    private void setDefaultValues() {
        // Default sensitivity values (50%)
        this.leftStickSensitivity = 0.5f;
        this.rightStickSensitivity = 0.5f;
        this.triggerSensitivity = 0.5f;
        
        // Default dead zone values
        this.leftStickDeadZone = 0.1f;
        this.rightStickDeadZone = 0.1f;
        this.triggerDeadZone = 0.05f;
        
        // Default acceleration settings
        this.accelerationEnabled = false;
        this.accelerationCurve = 1.0f;
        this.accelerationThreshold = 0.3f;
        this.maxAcceleration = 2.0f;
        
        // Default right stick acceleration settings
        this.rightStickAccelerationEnabled = false;
        this.rightStickAccelerationCurve = 1.2f;
        this.rightStickAccelerationThreshold = 0.25f;
        this.rightStickMaxAcceleration = 2.5f;
        
        this.isDefault = false;
    }
    
    /**
     * Create a default profile
     */
    public static ControllerProfile createDefaultProfile() {
        ControllerProfile profile = new ControllerProfile("Default", "Default controller settings");
        profile.setDefault(true);
        return profile;
    }
    
    /**
     * Copy constructor
     */
    public ControllerProfile(ControllerProfile other) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = other.name + " (Copy)";
        this.description = other.description;
        this.createdTimestamp = System.currentTimeMillis();
        this.modifiedTimestamp = this.createdTimestamp;
        this.isDefault = false;
        
        // Copy all settings
        this.leftStickSensitivity = other.leftStickSensitivity;
        this.rightStickSensitivity = other.rightStickSensitivity;
        this.triggerSensitivity = other.triggerSensitivity;
        
        this.leftStickDeadZone = other.leftStickDeadZone;
        this.rightStickDeadZone = other.rightStickDeadZone;
        this.triggerDeadZone = other.triggerDeadZone;
        
        this.accelerationEnabled = other.accelerationEnabled;
        this.accelerationCurve = other.accelerationCurve;
        this.accelerationThreshold = other.accelerationThreshold;
        this.maxAcceleration = other.maxAcceleration;
        
        this.rightStickAccelerationEnabled = other.rightStickAccelerationEnabled;
        this.rightStickAccelerationCurve = other.rightStickAccelerationCurve;
        this.rightStickAccelerationThreshold = other.rightStickAccelerationThreshold;
        this.rightStickMaxAcceleration = other.rightStickMaxAcceleration;
    }
    
    /**
     * Update the modified timestamp
     */
    public void updateModifiedTimestamp() {
        this.modifiedTimestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name;
        updateModifiedTimestamp();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description;
        updateModifiedTimestamp();
    }
    
    public long getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(long createdTimestamp) { this.createdTimestamp = createdTimestamp; }
    
    public long getModifiedTimestamp() { return modifiedTimestamp; }
    public void setModifiedTimestamp(long modifiedTimestamp) { this.modifiedTimestamp = modifiedTimestamp; }
    
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    
    // Sensitivity getters and setters
    public float getLeftStickSensitivity() { return leftStickSensitivity; }
    public void setLeftStickSensitivity(float leftStickSensitivity) { 
        this.leftStickSensitivity = Math.max(0.01f, Math.min(1.0f, leftStickSensitivity));
        updateModifiedTimestamp();
    }
    
    public float getRightStickSensitivity() { return rightStickSensitivity; }
    public void setRightStickSensitivity(float rightStickSensitivity) { 
        this.rightStickSensitivity = Math.max(0.01f, Math.min(1.0f, rightStickSensitivity));
        updateModifiedTimestamp();
    }
    
    public float getTriggerSensitivity() { return triggerSensitivity; }
    public void setTriggerSensitivity(float triggerSensitivity) { 
        this.triggerSensitivity = Math.max(0.01f, Math.min(1.0f, triggerSensitivity));
        updateModifiedTimestamp();
    }
    
    // Dead zone getters and setters
    public float getLeftStickDeadZone() { return leftStickDeadZone; }
    public void setLeftStickDeadZone(float leftStickDeadZone) { 
        this.leftStickDeadZone = Math.max(0.0f, Math.min(0.5f, leftStickDeadZone));
        updateModifiedTimestamp();
    }
    
    public float getRightStickDeadZone() { return rightStickDeadZone; }
    public void setRightStickDeadZone(float rightStickDeadZone) { 
        this.rightStickDeadZone = Math.max(0.0f, Math.min(0.5f, rightStickDeadZone));
        updateModifiedTimestamp();
    }
    
    public float getTriggerDeadZone() { return triggerDeadZone; }
    public void setTriggerDeadZone(float triggerDeadZone) { 
        this.triggerDeadZone = Math.max(0.0f, Math.min(0.3f, triggerDeadZone));
        updateModifiedTimestamp();
    }
    
    // Acceleration getters and setters
    public boolean isAccelerationEnabled() { return accelerationEnabled; }
    public void setAccelerationEnabled(boolean accelerationEnabled) { 
        this.accelerationEnabled = accelerationEnabled;
        updateModifiedTimestamp();
    }
    
    public float getAccelerationCurve() { return accelerationCurve; }
    public void setAccelerationCurve(float accelerationCurve) { 
        this.accelerationCurve = Math.max(0.1f, Math.min(3.0f, accelerationCurve));
        updateModifiedTimestamp();
    }
    
    public float getAccelerationThreshold() { return accelerationThreshold; }
    public void setAccelerationThreshold(float accelerationThreshold) { 
        this.accelerationThreshold = Math.max(0.1f, Math.min(0.8f, accelerationThreshold));
        updateModifiedTimestamp();
    }
    
    public float getMaxAcceleration() { return maxAcceleration; }
    public void setMaxAcceleration(float maxAcceleration) { 
        this.maxAcceleration = Math.max(1.0f, Math.min(5.0f, maxAcceleration));
        updateModifiedTimestamp();
    }
    
    // Right stick acceleration getters and setters
    public boolean isRightStickAccelerationEnabled() { return rightStickAccelerationEnabled; }
    public void setRightStickAccelerationEnabled(boolean rightStickAccelerationEnabled) { 
        this.rightStickAccelerationEnabled = rightStickAccelerationEnabled;
        updateModifiedTimestamp();
    }
    
    public float getRightStickAccelerationCurve() { return rightStickAccelerationCurve; }
    public void setRightStickAccelerationCurve(float rightStickAccelerationCurve) { 
        this.rightStickAccelerationCurve = Math.max(0.1f, Math.min(3.0f, rightStickAccelerationCurve));
        updateModifiedTimestamp();
    }
    
    public float getRightStickAccelerationThreshold() { return rightStickAccelerationThreshold; }
    public void setRightStickAccelerationThreshold(float rightStickAccelerationThreshold) { 
        this.rightStickAccelerationThreshold = Math.max(0.1f, Math.min(0.8f, rightStickAccelerationThreshold));
        updateModifiedTimestamp();
    }
    
    public float getRightStickMaxAcceleration() { return rightStickMaxAcceleration; }
    public void setRightStickMaxAcceleration(float rightStickMaxAcceleration) { 
        this.rightStickMaxAcceleration = Math.max(1.0f, Math.min(5.0f, rightStickMaxAcceleration));
        updateModifiedTimestamp();
    }
    
    /**
     * Reset all settings to default values
     */
    public void resetToDefaults() {
        // Sensitivity defaults
        this.leftStickSensitivity = 1.0f;
        this.rightStickSensitivity = 1.0f;
        this.triggerSensitivity = 1.0f;
        
        // Dead zone defaults
        this.leftStickDeadZone = 0.1f;
        this.rightStickDeadZone = 0.1f;
        this.triggerDeadZone = 0.05f;
        
        // Acceleration defaults
        this.accelerationEnabled = false;
        this.accelerationCurve = 1.0f;
        this.accelerationThreshold = 0.5f;
        this.maxAcceleration = 2.0f;
        
        // Right stick acceleration defaults
        this.rightStickAccelerationEnabled = false;
        this.rightStickAccelerationCurve = 1.0f;
        this.rightStickAccelerationThreshold = 0.5f;
        this.rightStickMaxAcceleration = 2.0f;
        
        updateModifiedTimestamp();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControllerProfile that = (ControllerProfile) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ControllerProfile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}