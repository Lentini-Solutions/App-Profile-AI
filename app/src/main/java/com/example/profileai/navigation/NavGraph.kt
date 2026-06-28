package com.example.profileai.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.profileai.view.EditView
import com.example.profileai.view.ProfileView
import com.example.profileai.view.SettingsView
import com.example.profileai.view_model.MainViewModel

@Composable
fun NavGraph(navHostController: NavHostController, vm: MainViewModel) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.Profile.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(route = Screens.Profile.route){
            ProfileView(
                vm = vm,
                onEdit = {
                    navHostController.navigate(Screens.Edit.route)
                },
                onSettings = {
                    navHostController.navigate(Screens.Settings.route)
                }
            )
        }
        composable(route = Screens.Settings.route){
            SettingsView(vm = vm, onBack = {
                navHostController.popBackStack()
            })
        }

        composable(route = Screens.Edit.route) {
            EditView(vm = vm, onBack = {
              navHostController.popBackStack()
            })
        }
    }
}