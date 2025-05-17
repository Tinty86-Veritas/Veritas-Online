package com.veritas.veritas.Util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.veritas.veritas.DB.Firebase.entity.Group;

public class SharedLobbyViewModel extends ViewModel {
    private MutableLiveData<Group> _currentLobby = new MutableLiveData<>(null);

    public LiveData<Group> getCurrentLobby() {
        return _currentLobby;
    }

    public void setCurrentLobby(Group group) {
        _currentLobby.setValue(group);
    }

    public Group getLobbySync() {
        return _currentLobby.getValue();
    }

    public void clearLobby() {
        _currentLobby.setValue(null);
    }
}
