package com.easysale.retrofitroomuserlist.ui.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddNewUserViewModel extends ViewModel {
    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> avatarUrl = new MutableLiveData<>("https://reqres.in/img/faces/2-image.jpg");

    public void setFirstName(String firstName) {
        this.firstName.setValue(firstName);
    }

    public LiveData<String> getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName.setValue(lastName);
    }

    public LiveData<String> getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl.setValue(avatarUrl);
    }

    public LiveData<String> getAvatarUrl() {
        return avatarUrl;
    }

    public boolean validateInput() {
        return !isEmpty(firstName.getValue()) && !isEmpty(lastName.getValue()) && !isEmpty(email.getValue());
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
