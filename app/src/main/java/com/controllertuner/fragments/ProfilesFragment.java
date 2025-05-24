package com.controllertuner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.controllertuner.R;
import com.controllertuner.adapters.ProfileAdapter;
import com.controllertuner.model.ControllerProfile;
import com.controllertuner.profile.ProfileManager;
import com.google.android.material.button.MaterialButton;
import java.util.List;

/**
 * Fragment for managing controller profiles
 */
public class ProfilesFragment extends Fragment {
    
    private static final String TAG = "ProfilesFragment";
    
    // Dependencies
    private ProfileManager profileManager;
    
    // UI Components
    private TextView currentProfileName;
    private TextView currentProfileDescription;
    private MaterialButton editCurrentProfileButton;
    private MaterialButton addProfileButton;
    private MaterialButton sortProfilesButton;
    private RecyclerView profilesRecyclerView;
    
    // Adapter
    private ProfileAdapter profileAdapter;
    
    // State
    private boolean sortByDate = false;
    
    public ProfilesFragment() {
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
        return inflater.inflate(R.layout.fragment_profiles, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        updateCurrentProfileDisplay();
        updateProfilesList();
        
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
        currentProfileName = view.findViewById(R.id.currentProfileName);
        currentProfileDescription = view.findViewById(R.id.currentProfileDescription);
        editCurrentProfileButton = view.findViewById(R.id.editCurrentProfileButton);
        addProfileButton = view.findViewById(R.id.addProfileButton);
        sortProfilesButton = view.findViewById(R.id.sortProfilesButton);
        profilesRecyclerView = view.findViewById(R.id.profilesRecyclerView);
    }
    
    /**
     * Set up the RecyclerView
     */
    private void setupRecyclerView() {
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        profileAdapter = new ProfileAdapter(new ProfileAdapter.ProfileActionListener() {
            @Override
            public void onProfileSelected(ControllerProfile profile) {
                if (profileManager != null) {
                    profileManager.setCurrentProfile(profile);
                }
            }
            
            @Override
            public void onProfileEdit(ControllerProfile profile) {
                showEditProfileDialog(profile);
            }
            
            @Override
            public void onProfileDuplicate(ControllerProfile profile) {
                if (profileManager != null) {
                    ControllerProfile duplicate = profileManager.duplicateProfile(profile);
                    if (duplicate != null) {
                        showEditProfileDialog(duplicate);
                    }
                }
            }
            
            @Override
            public void onProfileDelete(ControllerProfile profile) {
                showDeleteProfileDialog(profile);
            }
            
            @Override
            public void onProfileReset(ControllerProfile profile) {
                showResetProfileDialog(profile);
            }
        });
        
        profilesRecyclerView.setAdapter(profileAdapter);
    }
    
    /**
     * Set up click listeners
     */
    private void setupClickListeners() {
        addProfileButton.setOnClickListener(v -> showCreateProfileDialog());
        
        editCurrentProfileButton.setOnClickListener(v -> {
            if (profileManager != null) {
                ControllerProfile currentProfile = profileManager.getCurrentProfile();
                if (currentProfile != null) {
                    showEditProfileDialog(currentProfile);
                }
            }
        });
        
        sortProfilesButton.setOnClickListener(v -> {
            sortByDate = !sortByDate;
            updateSortButtonText();
            updateProfilesList();
        });
    }
    
    /**
     * Update current profile display
     */
    private void updateCurrentProfileDisplay() {
        if (profileManager == null) return;
        
        ControllerProfile currentProfile = profileManager.getCurrentProfile();
        if (currentProfile != null) {
            currentProfileName.setText(currentProfile.getName());
            currentProfileDescription.setText(currentProfile.getDescription());
        } else {
            currentProfileName.setText(R.string.default_profile);
            currentProfileDescription.setText(R.string.default_profile_description);
        }
    }
    
    /**
     * Update profiles list
     */
    private void updateProfilesList() {
        if (profileManager == null || profileAdapter == null) return;
        
        List<ControllerProfile> profiles;
        if (sortByDate) {
            profiles = profileManager.getProfilesSortedByDate();
        } else {
            profiles = profileManager.getProfilesSortedByName();
        }
        
        ControllerProfile currentProfile = profileManager.getCurrentProfile();
        profileAdapter.updateProfiles(profiles, currentProfile);
    }
    
    /**
     * Update sort button text
     */
    private void updateSortButtonText() {
        if (sortByDate) {
            sortProfilesButton.setText(R.string.sort_by_date);
        } else {
            sortProfilesButton.setText(R.string.sort_by_name);
        }
    }
    
    /**
     * Show create profile dialog
     */
    private void showCreateProfileDialog() {
        // TODO: Implement profile creation dialog
        // For now, create a simple profile
        if (profileManager != null) {
            ControllerProfile newProfile = profileManager.createProfile("New Profile", "Custom profile");
            if (newProfile != null) {
                showEditProfileDialog(newProfile);
            }
        }
    }
    
    /**
     * Show edit profile dialog
     */
    private void showEditProfileDialog(ControllerProfile profile) {
        // TODO: Implement profile editing dialog
        // For now, just switch to the tuning fragment
        if (getActivity() != null && getActivity() instanceof com.controllertuner.MainActivity) {
            com.controllertuner.MainActivity mainActivity = (com.controllertuner.MainActivity) getActivity();
            if (profileManager != null) {
                profileManager.setCurrentProfile(profile);
            }
            // Switch to tuning tab
            // This would require access to the ViewPager or BottomNavigation
        }
    }
    
    /**
     * Show delete profile dialog
     */
    private void showDeleteProfileDialog(ControllerProfile profile) {
        // TODO: Implement delete confirmation dialog
        // For now, just delete directly (not recommended for production)
        if (profileManager != null && !profile.isDefault()) {
            profileManager.removeProfile(profile);
        }
    }
    
    /**
     * Show reset profile dialog
     */
    private void showResetProfileDialog(ControllerProfile profile) {
        // TODO: Implement reset confirmation dialog
        // For now, just reset directly (not recommended for production)
        if (profileManager != null) {
            profileManager.resetProfile(profile);
        }
    }
    
    /**
     * Profile change listener
     */
    private final ProfileManager.ProfileChangeListener profileChangeListener = 
            new ProfileManager.ProfileChangeListener() {
        @Override
        public void onProfileAdded(ControllerProfile profile) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> updateProfilesList());
            }
        }
        
        @Override
        public void onProfileRemoved(ControllerProfile profile) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> updateProfilesList());
            }
        }
        
        @Override
        public void onProfileUpdated(ControllerProfile profile) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateProfilesList();
                    updateCurrentProfileDisplay();
                });
            }
        }
        
        @Override
        public void onCurrentProfileChanged(ControllerProfile profile) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateCurrentProfileDisplay();
                    updateProfilesList();
                });
            }
        }
    };
}