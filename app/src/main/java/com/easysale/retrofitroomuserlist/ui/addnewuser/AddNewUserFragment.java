package com.easysale.retrofitroomuserlist.ui.addnewuser;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.easysale.retrofitroomuserlist.R;
import com.easysale.retrofitroomuserlist.databinding.FragmentAddNewUserBinding;
import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.ui.userlist.UserListViewModel;
import com.easysale.retrofitroomuserlist.utils.avatar.AvatarHandler;
import com.easysale.retrofitroomuserlist.utils.image.ImagePickerHelper;
import com.easysale.retrofitroomuserlist.utils.messages.MessageConstants;
import com.easysale.retrofitroomuserlist.utils.messages.MessageDisplayer;
import com.easysale.retrofitroomuserlist.utils.messages.SnackBarMessage;
import com.easysale.retrofitroomuserlist.utils.validation.InputValidator;

public class AddNewUserFragment extends DialogFragment {

    private Uri selectedPhotoUri;
    private MessageDisplayer messageDisplayer;
    private FragmentAddNewUserBinding binding;
    private UserListViewModel userListViewModel;
    private ImagePickerHelper imagePickerHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNewUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userListViewModel = new ViewModelProvider(requireActivity()).get(UserListViewModel.class);
        messageDisplayer = new SnackBarMessage(requireView().getRootView());

        // Initialize ImagePickerHelper
        imagePickerHelper = new ImagePickerHelper(this, imageUri -> {
            selectedPhotoUri = imageUri;
            imagePickerHelper.loadImage(imageUri, binding.avatarImage);
        });

        // Set up click listener for the avatar image
        binding.avatarImage.setOnClickListener(v -> imagePickerHelper.pickImage());

        binding.addUserButton.setOnClickListener(v -> {
            if (InputValidator.validateUserInput(binding.firstNameEditText, binding.lastNameEditText, binding.emailEditText)) {
                String firstName = binding.firstNameEditText.getText().toString().trim();
                String lastName = binding.lastNameEditText.getText().toString().trim();
                String email = binding.emailEditText.getText().toString().trim();
                String avatarUrl = AvatarHandler.getAvatarUri(requireContext(), selectedPhotoUri, firstName, lastName).toString();

                final User newUser = User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .avatar(avatarUrl)
                        .build();

                userListViewModel.addUser(newUser).observe(getViewLifecycleOwner(), success -> {
                    if (success) {
                        navigateBackToUserList(v);
                        messageDisplayer.showMessage(MessageConstants.USER_ADDED_SUCCESSFULLY);
                    } else {
                        messageDisplayer.showMessage(MessageConstants.ERROR_ADDING_USER);
                    }
                });

            } else {
                messageDisplayer.showMessage(MessageConstants.FIELDS_REQUIRED);
            }
        });

//        binding.cancelButton.setOnClickListener(this::navigateBackToUserList);
    }

    private void navigateBackToUserList(View view) {
        Navigation.findNavController(view).navigate(R.id.action_addNewUserFragment_to_userListFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
