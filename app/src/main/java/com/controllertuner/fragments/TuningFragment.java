package com.controllertuner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.controllertuner.R;
import com.controllertuner.mapping.InputMappingEngine;
import com.controllertuner.model.ControllerProfile;
import com.controllertuner.profile.ProfileManager;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Fragment for tuning controller settings
 */
public class TuningFragment extends Fragment {
    
    private static final String TAG = "TuningFragment";
    
    // Dependencies
    private ProfileManager profileManager;
    private InputMappingEngine mappingEngine;
    
    // UI Components - Sensitivity
    private Slider leftStickSensitivitySlider;
    private TextView leftStickSensitivityValue;
    private Slider rightStickSensitivitySlider;
    private TextView rightStickSensitivityValue;
    private Slider triggerSensitivitySlider;
    private TextView triggerSensitivityValue;
    
    // UI Components - Dead Zones
    private Slider leftStickDeadZoneSlider;
    private TextView leftStickDeadZoneValue;
    private Slider rightStickDeadZoneSlider;
    private TextView rightStickDeadZoneValue;
    private Slider triggerDeadZoneSlider;
    private TextView triggerDeadZoneValue;
    
    // UI Components - Acceleration
    private SwitchMaterial enableAccelerationSwitch;
    private LinearLayout accelerationSettingsLayout;
    private Slider accelerationCurveSlider;
    private TextView accelerationCurveValue;
    private Slider accelerationThresholdSlider;
    private TextView accelerationThresholdValue;
    private Slider maxAccelerationSlider;
    private TextView maxAccelerationValue;
    
    // UI Components - Right Stick Acceleration
    private SwitchMaterial enableRightStickAccelerationSwitch;
    private LinearLayout rightStickAccelerationSettingsLayout;
    private Slider rightStickAccelerationCurveSlider;
    private TextView rightStickAccelerationCurveValue;
    private Slider rightStickAccelerationThresholdSlider;
    private TextView rightStickAccelerationThresholdValue;
    private Slider rightStickMaxAccelerationSlider;
    private TextView rightStickMaxAccelerationValue;
    
    private boolean isUpdatingUI = false;
    
    public TuningFragment() {
        // Required empty public constructor
    }
    
    /**
     * Set dependencies for this fragment
     */
    public void setDependencies(ProfileManager profileManager, InputMappingEngine mappingEngine) {
        this.profileManager = profileManager;
        this.mappingEngine = mappingEngine;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tuning, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupSliderListeners();
        updateUIFromProfile();
        
        // Listen for profile changes
        if (profileManager != null) {
            profileManager.addListener(profileChangeListener);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (profileManager != null) {
            profileManager.removeListener(profileChangeListener);
        }
    }
    
    /**
     * Initialize all UI views
     */
    private void initializeViews(View view) {
        // Sensitivity sliders
        leftStickSensitivitySlider = view.findViewById(R.id.leftStickSensitivitySlider);
        leftStickSensitivityValue = view.findViewById(R.id.leftStickSensitivityValue);
        rightStickSensitivitySlider = view.findViewById(R.id.rightStickSensitivitySlider);
        rightStickSensitivityValue = view.findViewById(R.id.rightStickSensitivityValue);
        triggerSensitivitySlider = view.findViewById(R.id.triggerSensitivitySlider);
        triggerSensitivityValue = view.findViewById(R.id.triggerSensitivityValue);
        
        // Dead zone sliders
        leftStickDeadZoneSlider = view.findViewById(R.id.leftStickDeadZoneSlider);
        leftStickDeadZoneValue = view.findViewById(R.id.leftStickDeadZoneValue);
        rightStickDeadZoneSlider = view.findViewById(R.id.rightStickDeadZoneSlider);
        rightStickDeadZoneValue = view.findViewById(R.id.rightStickDeadZoneValue);
        triggerDeadZoneSlider = view.findViewById(R.id.triggerDeadZoneSlider);
        triggerDeadZoneValue = view.findViewById(R.id.triggerDeadZoneValue);
        
        // Acceleration controls
        enableAccelerationSwitch = view.findViewById(R.id.enableAccelerationSwitch);
        accelerationSettingsLayout = view.findViewById(R.id.accelerationSettingsLayout);
        accelerationCurveSlider = view.findViewById(R.id.accelerationCurveSlider);
        accelerationCurveValue = view.findViewById(R.id.accelerationCurveValue);
        accelerationThresholdSlider = view.findViewById(R.id.accelerationThresholdSlider);
        accelerationThresholdValue = view.findViewById(R.id.accelerationThresholdValue);
        maxAccelerationSlider = view.findViewById(R.id.maxAccelerationSlider);
        maxAccelerationValue = view.findViewById(R.id.maxAccelerationValue);
        
        // Right stick acceleration controls
        enableRightStickAccelerationSwitch = view.findViewById(R.id.enableRightStickAccelerationSwitch);
        rightStickAccelerationSettingsLayout = view.findViewById(R.id.rightStickAccelerationSettingsLayout);
        rightStickAccelerationCurveSlider = view.findViewById(R.id.rightStickAccelerationCurveSlider);
        rightStickAccelerationCurveValue = view.findViewById(R.id.rightStickAccelerationCurveValue);
        rightStickAccelerationThresholdSlider = view.findViewById(R.id.rightStickAccelerationThresholdSlider);
        rightStickAccelerationThresholdValue = view.findViewById(R.id.rightStickAccelerationThresholdValue);
        rightStickMaxAccelerationSlider = view.findViewById(R.id.rightStickMaxAccelerationSlider);
        rightStickMaxAccelerationValue = view.findViewById(R.id.rightStickMaxAccelerationValue);
    }
    
    /**
     * Set up listeners for all sliders and switches
     */
    private void setupSliderListeners() {
        // Sensitivity sliders
        setupSliderListener(leftStickSensitivitySlider, leftStickSensitivityValue, 
                           value -> getCurrentProfile().setLeftStickSensitivity(value / 100f), "%d%%");
        
        setupSliderListener(rightStickSensitivitySlider, rightStickSensitivityValue,
                           value -> getCurrentProfile().setRightStickSensitivity(value / 100f), "%d%%");
        
        setupSliderListener(triggerSensitivitySlider, triggerSensitivityValue,
                           value -> getCurrentProfile().setTriggerSensitivity(value / 100f), "%d%%");
        
        // Dead zone sliders
        setupSliderListener(leftStickDeadZoneSlider, leftStickDeadZoneValue,
                           value -> getCurrentProfile().setLeftStickDeadZone(value / 100f), "%d%%");
        
        setupSliderListener(rightStickDeadZoneSlider, rightStickDeadZoneValue,
                           value -> getCurrentProfile().setRightStickDeadZone(value / 100f), "%d%%");
        
        setupSliderListener(triggerDeadZoneSlider, triggerDeadZoneValue,
                           value -> getCurrentProfile().setTriggerDeadZone(value / 100f), "%d%%");
        
        // Acceleration sliders
        setupSliderListener(accelerationCurveSlider, accelerationCurveValue,
                           value -> getCurrentProfile().setAccelerationCurve(value), "%.1f");
        
        setupSliderListener(accelerationThresholdSlider, accelerationThresholdValue,
                           value -> getCurrentProfile().setAccelerationThreshold(value / 100f), "%d%%");
        
        setupSliderListener(maxAccelerationSlider, maxAccelerationValue,
                           value -> getCurrentProfile().setMaxAcceleration(value), "%.1fx");
        
        // Right stick acceleration sliders
        setupSliderListener(rightStickAccelerationCurveSlider, rightStickAccelerationCurveValue,
                           value -> getCurrentProfile().setRightStickAccelerationCurve(value), "%.1f");
        
        setupSliderListener(rightStickAccelerationThresholdSlider, rightStickAccelerationThresholdValue,
                           value -> getCurrentProfile().setRightStickAccelerationThreshold(value / 100f), "%d%%");
        
        setupSliderListener(rightStickMaxAccelerationSlider, rightStickMaxAccelerationValue,
                           value -> getCurrentProfile().setRightStickMaxAcceleration(value), "%.1fx");
        
        // Acceleration switches
        enableAccelerationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdatingUI) {
                getCurrentProfile().setAccelerationEnabled(isChecked);
                updateProfile();
                updateAccelerationVisibility();
            }
        });
        
        enableRightStickAccelerationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdatingUI) {
                getCurrentProfile().setRightStickAccelerationEnabled(isChecked);
                updateProfile();
                updateRightStickAccelerationVisibility();
            }
        });
    }
    
    /**
     * Set up a slider listener with value formatting
     */
    private void setupSliderListener(Slider slider, TextView valueText, 
                                   SliderValueSetter setter, String format) {
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            if (!isUpdatingUI && fromUser) {
                setter.setValue(value);
                updateProfile();
            }
            // Update value text
            if (format.contains("d")) {
                valueText.setText(String.format(format, (int) value));
            } else {
                valueText.setText(String.format(format, value));
            }
        });
    }
    
    /**
     * Interface for setting slider values
     */
    private interface SliderValueSetter {
        void setValue(float value);
    }
    
    /**
     * Update UI from current profile
     */
    private void updateUIFromProfile() {
        if (profileManager == null) return;
        
        ControllerProfile profile = profileManager.getCurrentProfile();
        if (profile == null) return;
        
        isUpdatingUI = true;
        
        // Update sensitivity sliders
        leftStickSensitivitySlider.setValue(profile.getLeftStickSensitivity() * 100);
        rightStickSensitivitySlider.setValue(profile.getRightStickSensitivity() * 100);
        triggerSensitivitySlider.setValue(profile.getTriggerSensitivity() * 100);
        
        // Update dead zone sliders
        leftStickDeadZoneSlider.setValue(profile.getLeftStickDeadZone() * 100);
        rightStickDeadZoneSlider.setValue(profile.getRightStickDeadZone() * 100);
        triggerDeadZoneSlider.setValue(profile.getTriggerDeadZone() * 100);
        
        // Update acceleration controls
        enableAccelerationSwitch.setChecked(profile.isAccelerationEnabled());
        accelerationCurveSlider.setValue(profile.getAccelerationCurve());
        accelerationThresholdSlider.setValue(profile.getAccelerationThreshold() * 100);
        maxAccelerationSlider.setValue(profile.getMaxAcceleration());
        
        // Update right stick acceleration controls
        enableRightStickAccelerationSwitch.setChecked(profile.isRightStickAccelerationEnabled());
        rightStickAccelerationCurveSlider.setValue(profile.getRightStickAccelerationCurve());
        rightStickAccelerationThresholdSlider.setValue(profile.getRightStickAccelerationThreshold() * 100);
        rightStickMaxAccelerationSlider.setValue(profile.getRightStickMaxAcceleration());
        
        isUpdatingUI = false;
        
        updateAccelerationVisibility();
        updateRightStickAccelerationVisibility();
    }
    
    /**
     * Update acceleration settings visibility
     */
    private void updateAccelerationVisibility() {
        boolean enabled = enableAccelerationSwitch.isChecked();
        accelerationSettingsLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
    
    /**
     * Update right stick acceleration settings visibility
     */
    private void updateRightStickAccelerationVisibility() {
        boolean enabled = enableRightStickAccelerationSwitch.isChecked();
        rightStickAccelerationSettingsLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
    
    /**
     * Get current profile
     */
    private ControllerProfile getCurrentProfile() {
        return profileManager != null ? profileManager.getCurrentProfile() : null;
    }
    
    /**
     * Update the current profile
     */
    private void updateProfile() {
        if (profileManager != null) {
            ControllerProfile profile = getCurrentProfile();
            if (profile != null) {
                profileManager.updateProfile(profile);
            }
        }
    }
    
    /**
     * Profile change listener
     */
    private final ProfileManager.ProfileChangeListener profileChangeListener = 
            new ProfileManager.ProfileChangeListener() {
        @Override
        public void onProfileAdded(ControllerProfile profile) {
            // Not needed for tuning fragment
        }
        
        @Override
        public void onProfileRemoved(ControllerProfile profile) {
            // Not needed for tuning fragment
        }
        
        @Override
        public void onProfileUpdated(ControllerProfile profile) {
            // Update UI if this is the current profile
            if (getCurrentProfile() != null && getCurrentProfile().getId().equals(profile.getId())) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::updateUIFromProfile);
                }
            }
        }
        
        @Override
        public void onCurrentProfileChanged(ControllerProfile profile) {
            // Update UI for new current profile
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::updateUIFromProfile);
            }
        }
        
        private void updateUIFromProfile() {
            TuningFragment.this.updateUIFromProfile();
        }
    };
}