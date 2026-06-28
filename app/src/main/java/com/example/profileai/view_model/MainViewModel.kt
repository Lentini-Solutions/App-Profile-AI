package com.example.profileai.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursosant.profileaiant.Constants
import com.example.profileai.data.repository.MainRepositoryDSImpl
import com.example.profileai.domain.model.User
import com.example.profileai.domain.repository.MainRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository): ViewModel() {

    val user: StateFlow<User> = repository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = User()
    )

    val areClicksEnable: StateFlow<Boolean> = repository.areClicksEnable.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    val sizeImage: StateFlow<String> = repository.sizeImage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Constants.DS_SIZE_LARGE
    )

    fun saveUser(user: User) {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }

    fun saveEnableClicks(enable: Boolean){
        viewModelScope.launch {
            repository.saveEnableClicks(enable)
        }
    }

    fun saveSizeImage(size: String){
        viewModelScope.launch {
            repository.saveSizeImage(size)
        }
    }

    fun clearData(){
        viewModelScope.launch {
            repository.clearData()
        }
    }
}