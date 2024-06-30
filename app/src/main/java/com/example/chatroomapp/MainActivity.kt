package com.example.chatroomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatroomapp.data.NetworkUtils
import com.example.chatroomapp.screen.ChatRoomListScreen
import com.example.chatroomapp.screen.ChatScreen
import com.example.chatroomapp.screen.LoginScreen
import com.example.chatroomapp.screen.SignupScreen
import com.example.chatroomapp.ui.theme.ChatRoomAppTheme
import com.example.chatroomapp.viewmodel.AuthViewModel
import com.example.chatroomapp.viewmodel.MessageViewModel
import com.example.chatroomapp.viewmodel.RoomViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(NetworkUtils.isNetworkAvaliable(this)){
            //Internet Avaliable (Only Mobile Data not Wifi)
            setContent {
                startScreen()
            }
        }
        else{
            setContent {
                NoInternetScreen(this)
            }
        }


    }
}

@Composable
fun startScreen(){
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val roomViewModel: RoomViewModel = viewModel()
    //val messageViewModel: MessageViewModel = viewModel()

    ChatRoomAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavigationGraph(navController = navController,
                authViewModel = authViewModel)
            //messageViewModel = messageViewModel)
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    //messageViewModel: MessageViewModel
){
    NavHost(navController = navController, startDestination = Screen.SignupScreen.route){
        composable(route = Screen.SignupScreen.route){
            SignupScreen(
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                authViewModel = authViewModel
            )
        }
        composable(route = Screen.LoginScreen.route){
            LoginScreen(
                context = LocalContext.current,
                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                authViewModel = authViewModel,
                onSignInSuccess = { navController.navigate(Screen.ChatRoomsScreen.route) }
            )
        }

        composable(route = Screen.ChatRoomsScreen.route){
            ChatRoomListScreen(
                onJoinClicked = { room->
                    navController.navigate("${Screen.ChatScreen.route}/${room.id}")
                }
            )
        }

        composable("${Screen.ChatScreen.route}/{roomId}"){
            val roomId: String = it.arguments?.getString("roomId") ?: ""
            ChatScreen(roomId = roomId)
        }
    }
}

@Composable
fun NoInternetScreen( mainActivity: MainActivity){
    Scaffold {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No Internet Connection", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    if(NetworkUtils.isNetworkAvaliable(mainActivity)){
                        mainActivity.setContent{
                            startScreen()
                        }
                    }
                }) {
                    Text(text = "Retry")
                }
            }
        }
    }
}