package com.controllertuner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.controllertuner.R;
import com.controllertuner.model.ControllerProfile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying controller profiles
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    
    private final List<ControllerProfile> profiles;
    private ControllerProfile currentProfile;
    private final ProfileActionListener listener;
    private final SimpleDateFormat dateFormat;
    
    public interface ProfileActionListener {
        void onProfileSelected(ControllerProfile profile);
        void onProfileEdit(ControllerProfile profile);
        void onProfileDuplicate(ControllerProfile profile);
        void onProfileDelete(ControllerProfile profile);
        void onProfileReset(ControllerProfile profile);
    }
    
    public ProfileAdapter(ProfileActionListener listener) {
        this.profiles = new ArrayList<>();
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }
    
    /**
     * Update the profiles list
     */
    public void updateProfiles(List<ControllerProfile> newProfiles, ControllerProfile currentProfile) {
        this.profiles.clear();
        this.profiles.addAll(newProfiles);
        this.currentProfile = currentProfile;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ControllerProfile profile = profiles.get(position);
        holder.bind(profile, profile.equals(currentProfile));
    }
    
    @Override
    public int getItemCount() {
        return profiles.size();
    }
    
    /**
     * ViewHolder for profile items
     */
    class ProfileViewHolder extends RecyclerView.ViewHolder {
        
        private final MaterialCardView cardView;
        private final ImageView profileIcon;
        private final TextView profileName;
        private final TextView profileDescription;
        private final TextView profileModified;
        private final TextView currentIndicator;
        private final MaterialButton editButton;
        private final MaterialButton menuButton;
        
        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.profileCard);
            profileIcon = itemView.findViewById(R.id.profileIcon);
            profileName = itemView.findViewById(R.id.profileName);
            profileDescription = itemView.findViewById(R.id.profileDescription);
            profileModified = itemView.findViewById(R.id.profileModified);
            currentIndicator = itemView.findViewById(R.id.currentIndicator);
            editButton = itemView.findViewById(R.id.editButton);
            menuButton = itemView.findViewById(R.id.menuButton);
        }
        
        public void bind(ControllerProfile profile, boolean isCurrent) {
            // Set profile information
            profileName.setText(profile.getName());
            profileDescription.setText(profile.getDescription());
            
            // Set modified date
            String modifiedText = "Modified: " + dateFormat.format(new Date(profile.getModifiedTimestamp()));
            profileModified.setText(modifiedText);
            
            // Set current indicator
            if (isCurrent) {
                currentIndicator.setVisibility(View.VISIBLE);
                currentIndicator.setText("CURRENT");
                cardView.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.profile_active));
                cardView.setStrokeWidth(4);
            } else {
                currentIndicator.setVisibility(View.GONE);
                cardView.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.profile_inactive));
                cardView.setStrokeWidth(1);
            }
            
            // Set profile icon based on type
            if (profile.isDefault()) {
                profileIcon.setImageResource(R.drawable.ic_star);
                profileIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.status_warning));
            } else {
                profileIcon.setImageResource(R.drawable.ic_gamepad);
                profileIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.profile_inactive));
            }
            
            // Set click listeners
            cardView.setOnClickListener(v -> {
                if (listener != null && !isCurrent) {
                    listener.onProfileSelected(profile);
                }
            });
            
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProfileEdit(profile);
                }
            });
            
            menuButton.setOnClickListener(v -> showProfileMenu(profile));
            
            // Disable delete for default profile
            if (profile.isDefault()) {
                menuButton.setVisibility(View.GONE);
            } else {
                menuButton.setVisibility(View.VISIBLE);
            }
        }
        
        /**
         * Show profile context menu
         */
        private void showProfileMenu(ControllerProfile profile) {
            // TODO: Implement popup menu with options:
            // - Duplicate
            // - Reset
            // - Delete (if not default)
            
            // For now, just show a simple action
            if (listener != null) {
                // Simulate menu selection - in real implementation, show PopupMenu
                listener.onProfileDuplicate(profile);
            }
        }
    }
}