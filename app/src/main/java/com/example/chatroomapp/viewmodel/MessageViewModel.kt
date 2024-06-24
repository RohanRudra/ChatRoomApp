package com.example.chatroomapp.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroomapp.data.Message
import com.example.chatroomapp.data.MessageRepository
import com.example.chatroomapp.data.Result
import com.example.chatroomapp.data.User
import com.example.chatroomapp.data.UserRepository
import com.example.chatroomapp.di.Injection
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MessageViewModel: ViewModel() {
    private val messageRepository: MessageRepository
    private val userRepository: UserRepository

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _roomId = MutableLiveData<String>()
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    init {
        messageRepository = MessageRepository(Injection.instance())
        userRepository = UserRepository(FirebaseAuth.getInstance(),
            Injection.instance())
        loadCurrentUser()
    }

    private fun loadCurrentUser(){
        viewModelScope.launch {
            when(val result = userRepository.getCurrentUser()){
                is Result.Success -> {
                    _currentUser.value = result.data
                }
                is Result.Error -> {
                    Log.e("MessageViewModel","Error in loading current user")
                }
            }
        }
    }

    fun setRoomId(roomId : String){
        viewModelScope.launch {
            _roomId.value = roomId
        }
    }

    fun loadMessages(){
        viewModelScope.launch {
            if(_roomId != null){
                messageRepository.getChatMessages(_roomId.value.toString())
                    .collect{
                        _messages.value = it
                    }
            }
        }
    }

    fun sendMessage(text : String){
        if(_currentUser.value != null){
            val message = Message(
                senderFirstName = _currentUser.value!!.firstName,
                senderId = _currentUser.value!!.email,
                text = text
            )
            viewModelScope.launch {
                when(messageRepository.sendMessage(_roomId.value.toString(), message)){
                    is Result.Success -> Unit
                    is Result.Error -> {}
                }
            }
        }

    }

}