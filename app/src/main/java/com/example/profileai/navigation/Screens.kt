package com.example.profileai.navigation

sealed class Screens(val route: String) {
    object Profile: Screens(route = "profile")
    object Settings: Screens(route = "settings")
    object Edit: Screens(route = "edit")
}