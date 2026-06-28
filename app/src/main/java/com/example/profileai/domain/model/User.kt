package com.example.profileai.domain.model

data class User(
    var id: Long = 0,
    var name: String = "",
    var email: String = "",
    var website: String = "",
    var phone: String = "",
    var image: String = "",
    var latitude: String = "",
    var longitude: String = ""
)