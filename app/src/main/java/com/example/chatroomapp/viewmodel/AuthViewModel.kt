package com.example.chatroomapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroomapp.data.UserRepository
import com.example.chatroomapp.di.Injection
import com.google.firebase.auth.FirebaseAuth
import com.example.chatroomapp.data.Result
import com.example.chatroomapp.data.User
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val userRepository: UserRepository

    init {
        //initialized using dependency injection
        userRepository = UserRepository(FirebaseAuth.getInstance(),
            Injection.instance())
    }

    private val auth_result = MutableLiveData<Result<Boolean>>()
    val authResult : LiveData<Result<Boolean>> get() = auth_result

    fun signUp(email: String, password: String, firstName: String, lastName: String){
        viewModelScope.launch {
            auth_result.value = userRepository.signUp(email,password,firstName,lastName)
        }
    }

    fun login(email: String, password: String){
        viewModelScope.launch {
            auth_result.value = userRepository.login(email,password)
        }
    }

}
