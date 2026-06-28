package com.example.profileai.domain.repository

import com.example.profileai.domain.model.User
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    val user: Flow<User>
    val areClicksEnable: Flow<Boolean>
    val sizeImage: Flow<String>

    suspend fun saveUser(user: User)
    suspend fun saveSizeImage(size: String)
    suspend fun saveEnableClicks(enable: Boolean)
    suspend fun clearData()

}