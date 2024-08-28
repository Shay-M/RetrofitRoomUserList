package com.easysale.retrofitroomuserlist.ui.userdetail;

import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.easysale.retrofitroomuserlist.R;
import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.databinding.FragmentUserDetailBinding;
import com.easysale.retrofitroomuserlist.utils.image.ImagePickerHelper;
import com.easysale.retrofitroomuserlist.utils.messages.MessageDisplayer;
import com.easysale.retrofitroomuserlist.utils.messages.SnackBarMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserDetailFragment extends Fragment {
    private MessageDisplayer messageDisplayer;
    private FragmentUserDetailBinding binding;
    private UserDetailViewModel viewModel;
    private ImagePickerHelper imagePickerHelper;

    private EditText userFirstNameEditText;
    private EditText userLastNameEditText;
    private EditText userEmailEditText;
    private FloatingActionButton editButton, deleteButton, saveButton, cancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fragment_shared_element_transition));
        setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fragment_shared_element_transition));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(UserDetailViewModel.class);

        messageDisplayer = new SnackBarMessage(view);

        // Bind UI components
        userFirstNameEditText = binding.userFirstNameEditText;
        userLastNameEditText = binding.userLastNameEditText;
        userEmailEditText = binding.userEmailEditText;
        editButton = binding.editButton;
        deleteButton = binding.deleteButton;
        saveButton = binding.saveButton;
        cancelButton = binding.cancelButton;

        // Initialize ImagePickerHelper
        imagePickerHelper = new ImagePickerHelper(this, this::loadImage);

        // Initially hide save/cancel buttons
        saveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        // Assuming user details are passed as arguments to this fragment
        User user = (User) getArguments().getSerializable("user");

        // Populate UI with user details
        if (user != null) {
            userFirstNameEditText.setText(user.getFirstName());
            userLastNameEditText.setText(user.getLastName());
            userEmailEditText.setText(user.getEmail());
            imagePickerHelper.loadImage(Uri.parse(user.getAvatar()), binding.userAvatarImageView);

            // Ensure transition starts after the view has been created
            postponeEnterTransition();
            binding.userAvatarImageView.post(this::startPostponedEnterTransition);

        }

        // Set up button click listeners
        editButton.setOnClickListener(v -> enableEditMode());

        saveButton.setOnClickListener(v -> {
            saveUserDetails();
            disableEditMode();
        });

        cancelButton.setOnClickListener(v -> {
            disableEditMode();
            resetUserDetails(user);
        });

        deleteButton.setOnClickListener(v -> {
            viewModel.deleteUser(user).observe(getViewLifecycleOwner(), isDeleted -> {
                if (isDeleted) {
                    messageDisplayer.showMessage(getString(R.string.failed_to_delete_user));
                    navigateBackToUserList(v);
                } else {
                    messageDisplayer.showMessage(String.valueOf(R.string.failed_to_delete_user));
                }
            });
        });

        // Set up click listener for the avatar image
        binding.userAvatarImageView.setOnClickListener(v -> imagePickerHelper.pickImage());
        binding.userAvatarImageView.setClickable(false);
    }

    private void loadImage(Uri imageUri) {
        imagePickerHelper.loadImage(imageUri, binding.userAvatarImageView);
    }

    private void enableEditMode() {
        // Enable editing of the text fields
        userFirstNameEditText.setEnabled(true);
        userLastNameEditText.setEnabled(true);
        userEmailEditText.setEnabled(true);
        binding.userAvatarImageView.setClickable(true);
        binding.userAvatarImageView.setEnabled(true);

        // Show save/cancel buttons and hide edit/delete buttons
        binding.saveButton.setVisibility(View.VISIBLE);
        binding.cancelButton.setVisibility(View.VISIBLE);
        binding.editButton.setVisibility(View.GONE);
        binding.deleteButton.setVisibility(View.GONE);
    }

    private void disableEditMode() {
        // Disable editing of the text fields
        userFirstNameEditText.setEnabled(false);
        userLastNameEditText.setEnabled(false);
        userEmailEditText.setEnabled(false);

        // Hide save/cancel buttons and show edit/delete buttons
        binding.saveButton.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.editButton.setVisibility(View.VISIBLE);
        binding.deleteButton.setVisibility(View.VISIBLE);
    }

    private void saveUserDetails() {
        // Update the user details with the new data
        String newFirstName = userFirstNameEditText.getText().toString();
        String newLastName = userLastNameEditText.getText().toString();
        String newEmail = userEmailEditText.getText().toString();
        String avatarUrl = imagePickerHelper.getSelectedPhotoUri() != null ? imagePickerHelper.getSelectedPhotoUri().toString() : null;

        // Assuming user details are passed as arguments to this fragment
        User user = (User) getArguments().getSerializable("user");
        if (user != null) {
            user.setFirstName(newFirstName);
            user.setLastName(newLastName);
            user.setEmail(newEmail);
            if (avatarUrl != null) user.setAvatar(avatarUrl);

            viewModel.updateUser(user).observe(getViewLifecycleOwner(), success -> {
                if (success) {
                    messageDisplayer.showMessage(getString(R.string.user_updated_successfully));
                    disableEditMode();
                } else {
                    messageDisplayer.showMessage(getString(R.string.failed_to_update_user));
                }
            });
            ;
        }
    }

    private void resetUserDetails(User user) {
        // Reset the fields to the original user details
        if (user != null) {
            userFirstNameEditText.setText(user.getFirstName());
            userLastNameEditText.setText(user.getLastName());
            userEmailEditText.setText(user.getEmail());
            imagePickerHelper.loadImage(Uri.parse(user.getAvatar()), binding.userAvatarImageView);
        }
    }

    private void navigateBackToUserList(View view) {
        Navigation.findNavController(view).navigate(R.id.action_userDetailFragment_to_userListFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
