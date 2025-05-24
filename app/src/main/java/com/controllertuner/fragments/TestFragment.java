package com.controllertuner.fragments;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.controllertuner.R;
import com.controllertuner.input.ControllerInputManager;
import com.controllertuner.mapping.InputMappingEngine;
import com.controllertuner.model.ControllerProfile;
import com.controllertuner.profile.ProfileManager;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

/**
 * Fragment for testing controller input in real-time
 */
public class TestFragment extends Fragment {
    
    private static final String TAG = "TestFragment";
    
    // Dependencies
    private ControllerInputManager inputManager;
    private InputMappingEngine mappingEngine;
    private ProfileManager profileManager;
    private ControllerProfile currentProfile;
    
    // UI Components
    private TextView controllerStatusText;
    private StickVisualizerView leftStickIndicator;
    private StickVisualizerView rightStickIndicator;
    private TextView leftStickXValue;
    private TextView leftStickYValue;
    private TextView leftStickMagnitudeValue;
    private TextView rightStickXValue;
    private TextView rightStickYValue;
    private TextView rightStickMagnitudeValue;
    private ProgressBar leftTriggerProgress;
    private ProgressBar rightTriggerProgress;
    private TextView leftTriggerValue;
    private TextView rightTriggerValue;
    private TextView buttonStatusText;
    private MaterialButton resetButton;
    
    // State
    private boolean isTestingActive = false;
    private float leftStickX = 0f, leftStickY = 0f;
    private float rightStickX = 0f, rightStickY = 0f;
    private float leftTrigger = 0f, rightTrigger = 0f;
    private String lastButtonPressed = "";
    
    public TestFragment() {
        // Required empty public constructor
    }
    
    /**
     * Set dependencies for this fragment
     */
    public void setDependencies(ControllerInputManager inputManager, 
                               InputMappingEngine mappingEngine,
                               ProfileManager profileManager) {
        this.inputManager = inputManager;
        this.mappingEngine = mappingEngine;
        this.profileManager = profileManager;
        this.currentProfile = profileManager != null ? profileManager.getCurrentProfile() : null;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        
        if (inputManager != null && mappingEngine != null && profileManager != null) {
            setupClickListeners();
            startTesting();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTesting();
    }
    
    /**
     * Initialize all UI views
     */
    private void initializeViews(View view) {
        controllerStatusText = view.findViewById(R.id.testControllerStatus);
        leftStickIndicator = view.findViewById(R.id.leftStickIndicator);
        rightStickIndicator = view.findViewById(R.id.rightStickIndicator);
        leftStickXValue = view.findViewById(R.id.leftStickXValue);
        leftStickYValue = view.findViewById(R.id.leftStickYValue);
        leftStickMagnitudeValue = view.findViewById(R.id.leftStickMagnitudeValue);
        rightStickXValue = view.findViewById(R.id.rightStickXValue);
        rightStickYValue = view.findViewById(R.id.rightStickYValue);
        rightStickMagnitudeValue = view.findViewById(R.id.rightStickMagnitudeValue);
        leftTriggerProgress = view.findViewById(R.id.leftTriggerProgress);
        rightTriggerProgress = view.findViewById(R.id.rightTriggerProgress);
        leftTriggerValue = view.findViewById(R.id.leftTriggerValue);
        rightTriggerValue = view.findViewById(R.id.rightTriggerValue);
        buttonStatusText = view.findViewById(R.id.buttonStatus);
        resetButton = null; // No reset button in current layout
        
        // Initialize stick visualizers
        if (leftStickIndicator != null) {
            leftStickIndicator.setStickPosition(0f, 0f);
        }
        if (rightStickIndicator != null) {
            rightStickIndicator.setStickPosition(0f, 0f);
        }
    }
    
    /**
     * Set up click listeners
     */
    private void setupClickListeners() {
        if (resetButton != null) {
            resetButton.setOnClickListener(v -> resetTestValues());
        }
    }
    
    /**
     * Start testing mode
     */
    private void startTesting() {
        isTestingActive = true;
        updateControllerStatus();
        
        if (inputManager != null) {
            inputManager.addListener(inputListener);
        }
    }
    
    /**
     * Stop testing mode
     */
    private void stopTesting() {
        isTestingActive = false;
        
        if (inputManager != null) {
            inputManager.removeListener(inputListener);
        }
    }
    
    /**
     * Reset all test values
     */
    private void resetTestValues() {
        leftStickX = leftStickY = 0f;
        rightStickX = rightStickY = 0f;
        leftTrigger = rightTrigger = 0f;
        lastButtonPressed = "";
        
        updateUI();
    }
    
    /**
     * Update controller status display
     */
    private void updateControllerStatus() {
        if (inputManager != null && inputManager.isControllerConnected()) {
            String controllerName = inputManager.getControllerName();
            controllerStatusText.setText(getString(R.string.controller_connected, controllerName));
            controllerStatusText.setTextColor(ContextCompat.getColor(requireContext(), R.color.controller_connected));
        } else {
            controllerStatusText.setText(R.string.no_controller_detected);
            controllerStatusText.setTextColor(ContextCompat.getColor(requireContext(), R.color.controller_disconnected));
        }
    }
    
    /**
     * Update all UI elements with current values
     */
    private void updateUI() {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            // Update stick visualizers
            if (leftStickIndicator != null) {
                leftStickIndicator.setStickPosition(leftStickX, leftStickY);
            }
            if (rightStickIndicator != null) {
                rightStickIndicator.setStickPosition(rightStickX, rightStickY);
            }
            
            // Update stick value displays
            updateStickValues();
            
            // Update trigger displays
            updateTriggerValues();
            
            // Update button status
            updateButtonStatus();
        });
    }
    
    /**
     * Update stick value text displays
     */
    private void updateStickValues() {
        // Update left stick values
        if (leftStickXValue != null) {
            leftStickXValue.setText(getString(R.string.x_axis_value, leftStickX));
        }
        if (leftStickYValue != null) {
            leftStickYValue.setText(getString(R.string.y_axis_value, leftStickY));
        }
        if (leftStickMagnitudeValue != null) {
            float leftMagnitude = (float) Math.sqrt(leftStickX * leftStickX + leftStickY * leftStickY);
            leftStickMagnitudeValue.setText(getString(R.string.magnitude_value, leftMagnitude));
        }
        
        // Update right stick values
        if (rightStickXValue != null) {
            rightStickXValue.setText(getString(R.string.x_axis_value, rightStickX));
        }
        if (rightStickYValue != null) {
            rightStickYValue.setText(getString(R.string.y_axis_value, rightStickY));
        }
        if (rightStickMagnitudeValue != null) {
            float rightMagnitude = (float) Math.sqrt(rightStickX * rightStickX + rightStickY * rightStickY);
            rightStickMagnitudeValue.setText(getString(R.string.magnitude_value, rightMagnitude));
        }
    }
    
    /**
     * Update trigger value displays
     */
    private void updateTriggerValues() {
        if (leftTriggerProgress != null) {
            leftTriggerProgress.setProgress((int) (leftTrigger * 100));
        }
        if (rightTriggerProgress != null) {
            rightTriggerProgress.setProgress((int) (rightTrigger * 100));
        }
        
        if (leftTriggerValue != null) {
            leftTriggerValue.setText(getString(R.string.trigger_value, leftTrigger * 100));
        }
        if (rightTriggerValue != null) {
            rightTriggerValue.setText(getString(R.string.trigger_value, rightTrigger * 100));
        }
    }
    
    /**
     * Update button status display
     */
    private void updateButtonStatus() {
        if (buttonStatusText != null) {
            if (lastButtonPressed.isEmpty()) {
                buttonStatusText.setText(R.string.press_any_button);
            } else {
                buttonStatusText.setText("Last pressed: " + lastButtonPressed);
            }
        }
    }
    
    /**
     * Input listener for controller events
     */
    private final ControllerInputManager.InputListener inputListener = 
            new ControllerInputManager.InputListener() {
        @Override
        public void onStickInput(int stickId, float x, float y) {
            if (getActivity() != null && currentProfile != null) {
                getActivity().runOnUiThread(() -> {
                    float[] mapped = mappingEngine.mapStickInput(stickId, x, y, currentProfile);
                    updateStickDisplay(stickId, mapped[0], mapped[1]);
                });
            }
        }
        
        @Override
        public void onTriggerInput(int triggerId, float value) {
            if (getActivity() != null && currentProfile != null) {
                getActivity().runOnUiThread(() -> {
                    float mappedValue = mappingEngine.mapTriggerInput(triggerId, value, currentProfile);
                    updateTriggerDisplay(triggerId, mappedValue);
                });
            }
        }
        
        @Override
        public void onButtonInput(int buttonId, boolean pressed) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> updateButtonDisplay(buttonId, pressed));
            }
        }
        
        @Override
        public void onControllerConnectionChanged(boolean connected) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> updateControllerStatus());
            }
        }
    };
    
    /**
     * Update stick display with new values
     */
    private void updateStickDisplay(int stickId, float x, float y) {
        if (stickId == ControllerInputManager.STICK_LEFT && leftStickIndicator != null) {
            leftStickIndicator.updateStickPosition(x, y);
        } else if (stickId == ControllerInputManager.STICK_RIGHT && rightStickIndicator != null) {
            rightStickIndicator.updateStickPosition(x, y);
        }
    }
    
    /**
     * Update trigger display with new value
     */
    private void updateTriggerDisplay(int triggerId, float value) {
        // Update trigger display in UI if needed
        // For now, just store the values
        if (triggerId == ControllerInputManager.TRIGGER_LEFT) {
            leftTrigger = value;
        } else if (triggerId == ControllerInputManager.TRIGGER_RIGHT) {
            rightTrigger = value;
        }
    }
    
    /**
     * Update button display with new state
     */
    private void updateButtonDisplay(int buttonId, boolean pressed) {
        if (pressed) {
            lastButtonPressed = getButtonName(buttonId);
            updateUI();
        }
    }
    
    /**
     * Convert button ID to readable name
     */
    private String getButtonName(int buttonId) {
        switch (buttonId) {
            case ControllerInputManager.BUTTON_A: return "A";
            case ControllerInputManager.BUTTON_B: return "B";
            case ControllerInputManager.BUTTON_X: return "X";
            case ControllerInputManager.BUTTON_Y: return "Y";
            case ControllerInputManager.BUTTON_L1: return "L1";
            case ControllerInputManager.BUTTON_R1: return "R1";
            case ControllerInputManager.BUTTON_L3: return "L3";
            case ControllerInputManager.BUTTON_R3: return "R3";
            case ControllerInputManager.BUTTON_START: return "Start";
            case ControllerInputManager.BUTTON_SELECT: return "Select";
            case ControllerInputManager.DPAD_UP: return "D-Pad Up";
            case ControllerInputManager.DPAD_DOWN: return "D-Pad Down";
            case ControllerInputManager.DPAD_LEFT: return "D-Pad Left";
            case ControllerInputManager.DPAD_RIGHT: return "D-Pad Right";
            default: return "Button " + buttonId;
        }
    }
    
    /**
     * Custom view for visualizing analog stick position
     */
    public static class StickVisualizerView extends View {
        
        private Paint backgroundPaint;
        private Paint indicatorPaint;
        private Paint deadZonePaint;
        private RectF bounds;
        private float stickX = 0f;
        private float stickY = 0f;
        private float deadZone = 0.1f;
        
        public StickVisualizerView(android.content.Context context) {
            super(context);
            init();
        }
        
        public StickVisualizerView(android.content.Context context, android.util.AttributeSet attrs) {
            super(context, attrs);
            init();
        }
        
        private void init() {
            backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            backgroundPaint.setColor(0xFFE0E0E0);
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setStrokeWidth(4f);
            
            indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            indicatorPaint.setColor(0xFF2196F3);
            indicatorPaint.setStyle(Paint.Style.FILL);
            
            deadZonePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            deadZonePaint.setColor(0x40FF9800);
            deadZonePaint.setStyle(Paint.Style.FILL);
            
            bounds = new RectF();
        }
        
        public void setStickPosition(float x, float y) {
            this.stickX = x;
            this.stickY = y;
            invalidate();
        }
        
        public void updateStickPosition(float x, float y) {
            setStickPosition(x, y);
        }
        
        public void setDeadZone(float deadZone) {
            this.deadZone = deadZone;
            invalidate();
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            
            int width = getWidth() - getPaddingLeft() - getPaddingRight();
            int height = getHeight() - getPaddingTop() - getPaddingBottom();
            int size = Math.min(width, height);
            
            float centerX = getPaddingLeft() + width / 2f;
            float centerY = getPaddingTop() + height / 2f;
            float radius = size / 2f - 10f;
            
            bounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            
            // Draw background circle
            canvas.drawOval(bounds, backgroundPaint);
            
            // Draw dead zone
            float deadZoneRadius = radius * deadZone;
            canvas.drawCircle(centerX, centerY, deadZoneRadius, deadZonePaint);
            
            // Draw stick position
            float stickPosX = centerX + stickX * radius;
            float stickPosY = centerY - stickY * radius; // Invert Y for screen coordinates
            canvas.drawCircle(stickPosX, stickPosY, 12f, indicatorPaint);
        }
    }
}