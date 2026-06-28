package com.example.profileai.di

import com.example.profileai.data.repository.MainRepositoryDSImpl
import com.example.profileai.data.repository.MainRepositorySQLiteDSImpl
import com.example.profileai.data.source.DatabaseHelper
import com.example.profileai.data.source.ProfileDataStore
import com.example.profileai.domain.repository.MainRepository
import com.example.profileai.view_model.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single { ProfileDataStore(androidContext()) }
    single { DatabaseHelper(androidContext()) }
    single<MainRepository> { MainRepositorySQLiteDSImpl(get(), get()) }
    viewModel { MainViewModel(get()) }
}