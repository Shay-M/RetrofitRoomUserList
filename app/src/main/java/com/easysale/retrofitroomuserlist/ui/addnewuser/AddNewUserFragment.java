package com.easysale.retrofitroomuserlist.ui.addnewuser;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.easysale.retrofitroomuserlist.R;
import com.easysale.retrofitroomuserlist.databinding.FragmentAddNewUserBinding;
import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.ui.userlist.UserListViewModel;
import com.easysale.retrofitroomuserlist.utils.AvatarGenerator;
import com.easysale.retrofitroomuserlist.utils.AvatarHandler;
import com.easysale.retrofitroomuserlist.utils.messages.MessageConstants;
import com.easysale.retrofitroomuserlist.utils.messages.MessageDisplayer;
import com.easysale.retrofitroomuserlist.utils.messages.SnackBarMessage;
import com.easysale.retrofitroomuserlist.utils.validation.InputValidator;


public class AddNewUserFragment extends DialogFragment {

    private Uri selectedPhotoUri;
    private ActivityResultLauncher<String> pickPhotoLauncher;
    private MessageDisplayer messageDisplayer;
    private FragmentAddNewUserBinding binding;
    private UserListViewModel userListViewModel;

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
        messageDisplayer = new SnackBarMessage(view);

        // Initialize ActivityResultLauncher for selecting images
        pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        selectedPhotoUri = result;
                        Glide.with(this)
                                .load(result)
                                .centerCrop()
                                .error(android.R.drawable.ic_dialog_info) // Fallback image on error
                                .into(binding.avatarImage);
                    }
                });

        // Set up click listener for the avatar image
        binding.avatarImage.setOnClickListener(v -> picFromGalleria());

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

        binding.cancelButton.setOnClickListener(this::navigateBackToUserList);
    }


    private void navigateBackToUserList(View view) {
        dismiss();
        Navigation.findNavController(view).navigate(R.id.action_addNewUserFragment_to_userListFragment);
    }

    private void picFromGalleria() {
        pickPhotoLauncher.launch("image/*");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}

