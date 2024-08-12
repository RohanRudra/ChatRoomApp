package com.example.chatroomapp.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatroomapp.data.Result
import com.example.chatroomapp.viewmodel.AuthViewModel
import kotlinx.coroutines.coroutineScope

@Composable
fun LoginScreen(
    context: Context,
    onNavigateToSignUp: () -> Unit,
    authViewModel: AuthViewModel,
    onSignInSuccess: () -> Unit
){
    val customBlue = Color(0xFF576CBC);
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val result by authViewModel.authResult.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation())

        Button(onClick = {
            authViewModel.login(email,password)
        },
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp)
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row{
            Text(text = "Don't have an account? ")
            Text(text = "Sign up.",
                modifier = Modifier.clickable{ onNavigateToSignUp() },
                color = customBlue)
        }

        //this block of code will be executed when the result changes
        LaunchedEffect(key1 = result) {
            when(result){
                is Result.Success -> {
                    //Toast.makeText(context,"Login Successful",Toast.LENGTH_SHORT).show()
                    onSignInSuccess()
                }
                is Result.Error -> {
                    Toast.makeText(context,"Incorrect Credentials",Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

    }
}


@Composable
@Preview
fun LoginScreenPreview(){
    //LoginScreen()
}