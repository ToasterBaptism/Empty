package com.controllertuner;

import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.controllertuner.fragments.ProfilesFragment;
import com.controllertuner.fragments.SettingsFragment;
import com.controllertuner.fragments.TestFragment;
import com.controllertuner.fragments.TuningFragment;
import com.controllertuner.input.ControllerInputManager;
import com.controllertuner.mapping.InputMappingEngine;
import com.controllertuner.profile.ProfileManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

/**
 * Main activity that hosts the controller tuning interface
 */
public class MainActivity extends AppCompatActivity implements ControllerInputManager.ControllerInputListener {
    
    private static final String TAG = "MainActivity";
    
    // UI Components
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private ImageView controllerStatusIcon;
    private TextView controllerStatusText;
    
    // Core Components
    private ControllerInputManager inputManager;
    private InputMappingEngine mappingEngine;
    private ProfileManager profileManager;
    
    // Fragments
    private TuningFragment tuningFragment;
    private ProfilesFragment profilesFragment;
    private TestFragment testFragment;
    private SettingsFragment settingsFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeComponents();
        setupUI();
        setupViewPager();
        setupBottomNavigation();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        inputManager.startListening();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        inputManager.stopListening();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inputManager != null) {
            inputManager.removeListener(this);
        }
    }
    
    /**
     * Initialize core components
     */
    private void initializeComponents() {
        // Initialize managers
        inputManager = new ControllerInputManager(this);
        mappingEngine = new InputMappingEngine();
        profileManager = new ProfileManager(this);
        
        // Set up relationships
        inputManager.addListener(this);
        mappingEngine.setProfile(profileManager.getCurrentProfile());
        
        // Listen for profile changes
        profileManager.addListener(new ProfileManager.ProfileChangeListener() {
            @Override
            public void onProfileAdded(com.controllertuner.model.ControllerProfile profile) {
                // Handle profile added
            }
            
            @Override
            public void onProfileRemoved(com.controllertuner.model.ControllerProfile profile) {
                // Handle profile removed
            }
            
            @Override
            public void onProfileUpdated(com.controllertuner.model.ControllerProfile profile) {
                // Update mapping engine if this is the current profile
                if (mappingEngine.getCurrentProfile().getId().equals(profile.getId())) {
                    mappingEngine.setProfile(profile);
                }
            }
            
            @Override
            public void onCurrentProfileChanged(com.controllertuner.model.ControllerProfile profile) {
                mappingEngine.setProfile(profile);
            }
        });
    }
    
    /**
     * Set up UI components
     */
    private void setupUI() {
        controllerStatusIcon = findViewById(R.id.controllerStatusIcon);
        controllerStatusText = findViewById(R.id.controllerStatusText);
        
        updateControllerStatus();
    }
    
    /**
     * Set up the ViewPager with fragments
     */
    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);
        
        // Create fragments
        tuningFragment = new TuningFragment();
        profilesFragment = new ProfilesFragment();
        testFragment = new TestFragment();
        settingsFragment = new SettingsFragment();
        
        // Pass dependencies to fragments
        tuningFragment.setDependencies(profileManager, mappingEngine);
        profilesFragment.setDependencies(profileManager);
        testFragment.setDependencies(inputManager, mappingEngine, profileManager);
        settingsFragment.setDependencies(profileManager);
        
        // Set up adapter
        FragmentPagerAdapter adapter = new FragmentPagerAdapter();
        viewPager.setAdapter(adapter);
        
        // Disable user input (navigation only through bottom nav)
        viewPager.setUserInputEnabled(false);
    }
    
    /**
     * Set up bottom navigation
     */
    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tuning) {
                viewPager.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.nav_profiles) {
                viewPager.setCurrentItem(1, true);
                return true;
            } else if (itemId == R.id.nav_test) {
                viewPager.setCurrentItem(2, true);
                return true;
            } else if (itemId == R.id.nav_settings) {
                viewPager.setCurrentItem(3, true);
                return true;
            }
            return false;
        });
        
        // Set default selection
        bottomNavigation.setSelectedItemId(R.id.nav_tuning);
    }
    
    /**
     * Update controller status display
     */
    private void updateControllerStatus() {
        InputDevice currentController = inputManager.getCurrentController();
        
        if (currentController != null) {
            String controllerType = ControllerInputManager.getControllerType(currentController);
            controllerStatusText.setText(getString(R.string.controller_connected, controllerType));
            controllerStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.controller_connected));
        } else {
            controllerStatusText.setText(R.string.no_controller_detected);
            controllerStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.controller_disconnected));
        }
    }
    
    /**
     * Get the profile manager instance
     */
    public ProfileManager getProfileManager() {
        return profileManager;
    }
    
    /**
     * Get the input manager instance
     */
    public ControllerInputManager getInputManager() {
        return inputManager;
    }
    
    /**
     * Get the mapping engine instance
     */
    public InputMappingEngine getMappingEngine() {
        return mappingEngine;
    }
    
    // ControllerInputListener implementation
    @Override
    public void onControllerConnected(InputDevice controller) {
        runOnUiThread(this::updateControllerStatus);
    }
    
    @Override
    public void onControllerDisconnected(InputDevice controller) {
        runOnUiThread(this::updateControllerStatus);
    }
    
    @Override
    public void onMotionEvent(MotionEvent event, InputDevice controller) {
        // Process through mapping engine
        mappingEngine.processMotionEvent(event, controller);
    }
    
    @Override
    public void onKeyEvent(KeyEvent event, InputDevice controller) {
        // Process through mapping engine
        mappingEngine.processKeyEvent(event, controller);
    }
    
    // Handle controller input events
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (inputManager.processMotionEvent(event)) {
            return true;
        }
        return super.onGenericMotionEvent(event);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (inputManager.processKeyEvent(event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (inputManager.processKeyEvent(event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    
    /**
     * ViewPager adapter for fragments
     */
    private class FragmentPagerAdapter extends FragmentStateAdapter {
        
        private final List<Fragment> fragments;
        
        public FragmentPagerAdapter() {
            super(MainActivity.this);
            fragments = new ArrayList<>();
            fragments.add(tuningFragment);
            fragments.add(profilesFragment);
            fragments.add(testFragment);
            fragments.add(settingsFragment);
        }
        
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }
        
        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}