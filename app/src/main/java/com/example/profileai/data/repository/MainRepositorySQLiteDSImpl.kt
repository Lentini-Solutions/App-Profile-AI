package com.example.profileai.data.repository

import com.example.profileai.data.source.DatabaseHelper
import com.example.profileai.data.source.ProfileDataStore
import com.example.profileai.domain.model.User
import com.example.profileai.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow

class MainRepositorySQLiteDSImpl(
    private val ds: ProfileDataStore,
    private val db: DatabaseHelper
) : MainRepository {

    override val user: Flow<User> = db.getUser()
    override val areClicksEnable: Flow<Boolean> = ds.areClicksEnabled
    override val sizeImage: Flow<String> = ds.getImageSize

    override suspend fun saveUser(user: User) {
        db.saveOrUpdateUser(user)
    }

    override suspend fun saveSizeImage(size: String){
        ds.saveSizeImage(size)
    }

    override suspend fun saveEnableClicks(enable: Boolean){
        ds.saveEnabledClicks(enable)
    }

    override suspend fun clearData(){
        //ds.saveOrUpdateUser(User())
        db.deleteAllUsers()
    }

}