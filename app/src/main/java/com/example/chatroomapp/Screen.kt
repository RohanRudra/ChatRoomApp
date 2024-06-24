package com.example.chatroomapp

sealed class Screen(val route: String) {
    object LoginScreen: Screen("logicscreen")
    object SignupScreen: Screen("signupscreen")
    object ChatRoomsScreen: Screen("chatroomscreen")
    object ChatScreen: Screen("chatscreen")
}