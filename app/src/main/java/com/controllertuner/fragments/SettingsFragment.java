package com.controllertuner.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.controllertuner.R;
import com.controllertuner.profile.ProfileManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Fragment for app settings and configuration
 */
public class SettingsFragment extends Fragment {
    
    private static final String TAG = "SettingsFragment";
    
    // Preference keys
    private static final String PREF_AUTO_DETECT = "auto_detect_controllers";
    private static final String PREF_SHOW_RAW_VALUES = "show_raw_values";
    private static final String PREF_VIBRATION_FEEDBACK = "vibration_feedback";
    
    // Dependencies
    private ProfileManager profileManager;
    
    // UI Components
    private MaterialSwitch autoDetectSwitch;
    private MaterialSwitch showRawValuesSwitch;
    private MaterialSwitch vibrationFeedbackSwitch;
    private MaterialButton exportProfilesButton;
    private MaterialButton importProfilesButton;
    private MaterialButton resetAllProfilesButton;
    private TextView versionText;
    private TextView appDescriptionText;
    
    // Preferences
    private SharedPreferences preferences;
    
    public SettingsFragment() {
        // Required empty public constructor
    }
    
    /**
     * Set dependencies for this fragment
     */
    public void setDependencies(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        
        initializeViews(view);
        
        if (profileManager != null) {
            setupClickListeners();
            loadPreferences();
        }
    }
    
    /**
     * Initialize all UI views
     */
    private void initializeViews(View view) {
        autoDetectSwitch = view.findViewById(R.id.autoDetectControllersSwitch);
        showRawValuesSwitch = view.findViewById(R.id.showRawValuesSwitch);
        vibrationFeedbackSwitch = view.findViewById(R.id.vibrationFeedbackSwitch);
        exportProfilesButton = view.findViewById(R.id.exportProfilesButton);
        importProfilesButton = view.findViewById(R.id.importProfilesButton);
        resetAllProfilesButton = view.findViewById(R.id.resetAllProfilesButton);
        versionText = view.findViewById(R.id.appVersionText);
        appDescriptionText = null; // No app description text in current layout
        
        // Set static text
        if (versionText != null) {
            versionText.setText(R.string.version_info);
        }
    }
    
    /**
     * Set up click listeners
     */
    private void setupClickListeners() {
        // Switch listeners
        autoDetectSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference(PREF_AUTO_DETECT, isChecked);
        });
        
        showRawValuesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference(PREF_SHOW_RAW_VALUES, isChecked);
        });
        
        vibrationFeedbackSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference(PREF_VIBRATION_FEEDBACK, isChecked);
        });
        
        // Button listeners
        exportProfilesButton.setOnClickListener(v -> exportProfiles());
        importProfilesButton.setOnClickListener(v -> importProfiles());
        resetAllProfilesButton.setOnClickListener(v -> showResetAllProfilesDialog());
    }
    
    /**
     * Load preferences from SharedPreferences
     */
    private void loadPreferences() {
        autoDetectSwitch.setChecked(preferences.getBoolean(PREF_AUTO_DETECT, true));
        showRawValuesSwitch.setChecked(preferences.getBoolean(PREF_SHOW_RAW_VALUES, false));
        vibrationFeedbackSwitch.setChecked(preferences.getBoolean(PREF_VIBRATION_FEEDBACK, true));
    }
    
    /**
     * Save a preference value
     */
    private void savePreference(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }
    
    /**
     * Get a preference value
     */
    public boolean getPreference(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
    
    /**
     * Export profiles to external storage
     */
    private void exportProfiles() {
        if (profileManager == null) return;
        
        // TODO: Implement file picker and export functionality
        // For now, just show a placeholder message
        showMessage("Export functionality will be implemented in a future update");
        
        /*
        // Example implementation:
        try {
            String exportData = profileManager.exportProfiles();
            // Save to file using Storage Access Framework
            // Show success message
        } catch (Exception e) {
            showError("Failed to export profiles: " + e.getMessage());
        }
        */
    }
    
    /**
     * Import profiles from external storage
     */
    private void importProfiles() {
        if (profileManager == null) return;
        
        // TODO: Implement file picker and import functionality
        // For now, just show a placeholder message
        showMessage("Import functionality will be implemented in a future update");
        
        /*
        // Example implementation:
        try {
            // Open file picker using Storage Access Framework
            // Read file content
            // profileManager.importProfiles(importData);
            // Show success message
        } catch (Exception e) {
            showError("Failed to import profiles: " + e.getMessage());
        }
        */
    }
    
    /**
     * Show reset all profiles confirmation dialog
     */
    private void showResetAllProfilesDialog() {
        // TODO: Implement confirmation dialog
        // For now, just reset directly (not recommended for production)
        if (profileManager != null) {
            profileManager.resetAllProfiles();
            showMessage("All profiles have been reset to default values");
        }
    }
    
    /**
     * Show a message to the user
     */
    private void showMessage(String message) {
        // TODO: Implement proper message display (Toast, Snackbar, or Dialog)
        // For now, just log the message
        android.util.Log.i(TAG, message);
    }
    
    /**
     * Show an error message to the user
     */
    private void showError(String error) {
        // TODO: Implement proper error display
        // For now, just log the error
        android.util.Log.e(TAG, error);
    }
    
    /**
     * Public getters for preference values (for use by other components)
     */
    public boolean isAutoDetectEnabled() {
        return getPreference(PREF_AUTO_DETECT, true);
    }
    
    public boolean isShowRawValuesEnabled() {
        return getPreference(PREF_SHOW_RAW_VALUES, false);
    }
    
    public boolean isVibrationFeedbackEnabled() {
        return getPreference(PREF_VIBRATION_FEEDBACK, true);
    }
}