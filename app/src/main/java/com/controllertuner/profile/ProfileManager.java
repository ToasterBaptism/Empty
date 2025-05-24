package com.controllertuner.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.controllertuner.model.ControllerProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages saving, loading, and organizing controller profiles
 */
public class ProfileManager {
    
    private static final String TAG = "ProfileManager";
    private static final String PREFS_NAME = "controller_profiles";
    private static final String KEY_PROFILES = "profiles";
    private static final String KEY_CURRENT_PROFILE_ID = "current_profile_id";
    private static final String KEY_DEFAULT_PROFILE_ID = "default_profile_id";
    
    private final Context context;
    private final SharedPreferences preferences;
    private final Gson gson;
    private final List<ControllerProfile> profiles;
    private final List<ProfileChangeListener> listeners;
    
    private ControllerProfile currentProfile;
    private ControllerProfile defaultProfile;
    
    public interface ProfileChangeListener {
        void onProfileAdded(ControllerProfile profile);
        void onProfileRemoved(ControllerProfile profile);
        void onProfileUpdated(ControllerProfile profile);
        void onCurrentProfileChanged(ControllerProfile profile);
    }
    
    public ProfileManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.profiles = new ArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        
        loadProfiles();
        ensureDefaultProfile();
    }
    
    /**
     * Add a listener for profile changes
     */
    public void addListener(ProfileChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener for profile changes
     */
    public void removeListener(ProfileChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Get all profiles
     */
    public List<ControllerProfile> getAllProfiles() {
        return new ArrayList<>(profiles);
    }
    
    /**
     * Get the current active profile
     */
    public ControllerProfile getCurrentProfile() {
        return currentProfile;
    }
    
    /**
     * Get the default profile
     */
    public ControllerProfile getDefaultProfile() {
        return defaultProfile;
    }
    
    /**
     * Set the current active profile
     */
    public void setCurrentProfile(ControllerProfile profile) {
        if (profile != null && profiles.contains(profile)) {
            this.currentProfile = profile;
            saveCurrentProfileId();
            
            // Notify listeners
            for (ProfileChangeListener listener : listeners) {
                listener.onCurrentProfileChanged(profile);
            }
        }
    }
    
    /**
     * Set the current profile by ID
     */
    public boolean setCurrentProfileById(String profileId) {
        ControllerProfile profile = getProfileById(profileId);
        if (profile != null) {
            setCurrentProfile(profile);
            return true;
        }
        return false;
    }
    
    /**
     * Get a profile by ID
     */
    public ControllerProfile getProfileById(String profileId) {
        for (ControllerProfile profile : profiles) {
            if (profile.getId().equals(profileId)) {
                return profile;
            }
        }
        return null;
    }
    
    /**
     * Add a new profile
     */
    public boolean addProfile(ControllerProfile profile) {
        if (profile == null || getProfileById(profile.getId()) != null) {
            return false;
        }
        
        profiles.add(profile);
        saveProfiles();
        
        // Notify listeners
        for (ProfileChangeListener listener : listeners) {
            listener.onProfileAdded(profile);
        }
        
        return true;
    }
    
    /**
     * Update an existing profile
     */
    public boolean updateProfile(ControllerProfile profile) {
        if (profile == null) {
            return false;
        }
        
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getId().equals(profile.getId())) {
                profile.updateModifiedTimestamp();
                profiles.set(i, profile);
                saveProfiles();
                
                // Update current profile reference if it's the same
                if (currentProfile != null && currentProfile.getId().equals(profile.getId())) {
                    currentProfile = profile;
                }
                
                // Update default profile reference if it's the same
                if (defaultProfile != null && defaultProfile.getId().equals(profile.getId())) {
                    defaultProfile = profile;
                }
                
                // Notify listeners
                for (ProfileChangeListener listener : listeners) {
                    listener.onProfileUpdated(profile);
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Remove a profile
     */
    public boolean removeProfile(ControllerProfile profile) {
        if (profile == null || profile.isDefault()) {
            return false; // Cannot remove default profile
        }
        
        boolean removed = profiles.remove(profile);
        if (removed) {
            saveProfiles();
            
            // If this was the current profile, switch to default
            if (currentProfile != null && currentProfile.getId().equals(profile.getId())) {
                setCurrentProfile(defaultProfile);
            }
            
            // Notify listeners
            for (ProfileChangeListener listener : listeners) {
                listener.onProfileRemoved(profile);
            }
        }
        
        return removed;
    }
    
    /**
     * Remove a profile by ID
     */
    public boolean removeProfileById(String profileId) {
        ControllerProfile profile = getProfileById(profileId);
        return profile != null && removeProfile(profile);
    }
    
    /**
     * Create a new profile with the given name
     */
    public ControllerProfile createProfile(String name) {
        return createProfile(name, "");
    }
    
    /**
     * Create a new profile with the given name and description
     */
    public ControllerProfile createProfile(String name, String description) {
        ControllerProfile profile = new ControllerProfile(name, description);
        if (addProfile(profile)) {
            return profile;
        }
        return null;
    }
    
    /**
     * Duplicate an existing profile
     */
    public ControllerProfile duplicateProfile(ControllerProfile original) {
        if (original == null) {
            return null;
        }
        
        ControllerProfile duplicate = new ControllerProfile(original);
        if (addProfile(duplicate)) {
            return duplicate;
        }
        return null;
    }
    
    /**
     * Reset a profile to default values
     */
    public boolean resetProfile(ControllerProfile profile) {
        if (profile == null) {
            return false;
        }
        
        // Create a new profile with default values but keep the same ID and metadata
        ControllerProfile defaultValues = new ControllerProfile();
        
        profile.setLeftStickSensitivity(defaultValues.getLeftStickSensitivity());
        profile.setRightStickSensitivity(defaultValues.getRightStickSensitivity());
        profile.setTriggerSensitivity(defaultValues.getTriggerSensitivity());
        
        profile.setLeftStickDeadZone(defaultValues.getLeftStickDeadZone());
        profile.setRightStickDeadZone(defaultValues.getRightStickDeadZone());
        profile.setTriggerDeadZone(defaultValues.getTriggerDeadZone());
        
        profile.setAccelerationEnabled(defaultValues.isAccelerationEnabled());
        profile.setAccelerationCurve(defaultValues.getAccelerationCurve());
        profile.setAccelerationThreshold(defaultValues.getAccelerationThreshold());
        profile.setMaxAcceleration(defaultValues.getMaxAcceleration());
        
        profile.setRightStickAccelerationEnabled(defaultValues.isRightStickAccelerationEnabled());
        profile.setRightStickAccelerationCurve(defaultValues.getRightStickAccelerationCurve());
        profile.setRightStickAccelerationThreshold(defaultValues.getRightStickAccelerationThreshold());
        profile.setRightStickMaxAcceleration(defaultValues.getRightStickMaxAcceleration());
        
        return updateProfile(profile);
    }
    
    /**
     * Reset all profiles to default values
     */
    public void resetAllProfiles() {
        for (ControllerProfile profile : profiles) {
            resetProfile(profile);
        }
    }
    
    /**
     * Get profiles sorted by name
     */
    public List<ControllerProfile> getProfilesSortedByName() {
        List<ControllerProfile> sorted = new ArrayList<>(profiles);
        Collections.sort(sorted, new Comparator<ControllerProfile>() {
            @Override
            public int compare(ControllerProfile p1, ControllerProfile p2) {
                // Default profile always first
                if (p1.isDefault()) return -1;
                if (p2.isDefault()) return 1;
                
                // Then sort by name
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });
        return sorted;
    }
    
    /**
     * Get profiles sorted by modification date (newest first)
     */
    public List<ControllerProfile> getProfilesSortedByDate() {
        List<ControllerProfile> sorted = new ArrayList<>(profiles);
        Collections.sort(sorted, new Comparator<ControllerProfile>() {
            @Override
            public int compare(ControllerProfile p1, ControllerProfile p2) {
                // Default profile always first
                if (p1.isDefault()) return -1;
                if (p2.isDefault()) return 1;
                
                // Then sort by modification date (newest first)
                return Long.compare(p2.getModifiedTimestamp(), p1.getModifiedTimestamp());
            }
        });
        return sorted;
    }
    
    /**
     * Load profiles from SharedPreferences
     */
    private void loadProfiles() {
        try {
            String profilesJson = preferences.getString(KEY_PROFILES, "[]");
            Type listType = new TypeToken<List<ControllerProfile>>(){}.getType();
            List<ControllerProfile> loadedProfiles = gson.fromJson(profilesJson, listType);
            
            if (loadedProfiles != null) {
                profiles.clear();
                profiles.addAll(loadedProfiles);
            }
            
            // Load current profile
            String currentProfileId = preferences.getString(KEY_CURRENT_PROFILE_ID, null);
            if (currentProfileId != null) {
                currentProfile = getProfileById(currentProfileId);
            }
            
            // Load default profile
            String defaultProfileId = preferences.getString(KEY_DEFAULT_PROFILE_ID, null);
            if (defaultProfileId != null) {
                defaultProfile = getProfileById(defaultProfileId);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading profiles", e);
            profiles.clear();
        }
    }
    
    /**
     * Save profiles to SharedPreferences
     */
    private void saveProfiles() {
        try {
            String profilesJson = gson.toJson(profiles);
            preferences.edit()
                    .putString(KEY_PROFILES, profilesJson)
                    .apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving profiles", e);
        }
    }
    
    /**
     * Save current profile ID
     */
    private void saveCurrentProfileId() {
        String profileId = currentProfile != null ? currentProfile.getId() : null;
        preferences.edit()
                .putString(KEY_CURRENT_PROFILE_ID, profileId)
                .apply();
    }
    
    /**
     * Save default profile ID
     */
    private void saveDefaultProfileId() {
        String profileId = defaultProfile != null ? defaultProfile.getId() : null;
        preferences.edit()
                .putString(KEY_DEFAULT_PROFILE_ID, profileId)
                .apply();
    }
    
    /**
     * Ensure there's always a default profile
     */
    private void ensureDefaultProfile() {
        // Look for existing default profile
        for (ControllerProfile profile : profiles) {
            if (profile.isDefault()) {
                defaultProfile = profile;
                break;
            }
        }
        
        // Create default profile if none exists
        if (defaultProfile == null) {
            defaultProfile = ControllerProfile.createDefaultProfile();
            profiles.add(0, defaultProfile); // Add at beginning
            saveProfiles();
            saveDefaultProfileId();
        }
        
        // Set as current profile if none is set
        if (currentProfile == null) {
            currentProfile = defaultProfile;
            saveCurrentProfileId();
        }
    }
    
    /**
     * Export profiles to JSON string
     */
    public String exportProfiles() {
        try {
            return gson.toJson(profiles);
        } catch (Exception e) {
            Log.e(TAG, "Error exporting profiles", e);
            return null;
        }
    }
    
    /**
     * Import profiles from JSON string
     */
    public boolean importProfiles(String json, boolean replaceExisting) {
        try {
            Type listType = new TypeToken<List<ControllerProfile>>(){}.getType();
            List<ControllerProfile> importedProfiles = gson.fromJson(json, listType);
            
            if (importedProfiles == null || importedProfiles.isEmpty()) {
                return false;
            }
            
            if (replaceExisting) {
                // Clear existing profiles (except default)
                List<ControllerProfile> toRemove = new ArrayList<>();
                for (ControllerProfile profile : profiles) {
                    if (!profile.isDefault()) {
                        toRemove.add(profile);
                    }
                }
                profiles.removeAll(toRemove);
            }
            
            // Add imported profiles
            for (ControllerProfile profile : importedProfiles) {
                if (!profile.isDefault() && getProfileById(profile.getId()) == null) {
                    profiles.add(profile);
                }
            }
            
            saveProfiles();
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error importing profiles", e);
            return false;
        }
    }
}