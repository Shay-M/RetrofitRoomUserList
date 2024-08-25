package com.easysale.retrofitroomuserlist.ui.addnewuser;

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

public class AddNewUserFragment extends DialogFragment {

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

        binding.addUserButton.setOnClickListener(v -> {
            String firstName = binding.firstNameEditText.getText().toString();
            String lastName = binding.lastNameEditText.getText().toString();
            String email = binding.emailEditText.getText().toString();
            String avatarUrl = binding.avatarImage.getTag() != null ? binding.avatarImage.getTag().toString() : "ToDo";

            if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()) {
                final User newUser = User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .avatar(avatarUrl)
                        .build();
                userListViewModel.addUser(newUser);
                dismiss();
                goBack(v);

            } else {
//                SnackBarMessage.notifyUser(v, "Please fill in all the fields.");
                goBack(v);
            }
        });

        binding.cancelButton.setOnClickListener(this::goBack);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void goBack(View view) {
        Navigation.findNavController(view).navigate(R.id.action_addNewUserFragment_to_userListFragment);

    }
}

