package com.example.chatroomapp.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatroomapp.data.Room
import com.example.chatroomapp.viewmodel.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomListScreen(
    roomViewModel: RoomViewModel = viewModel(),
    context: Context = LocalContext.current,
    onJoinClicked: (Room) -> Unit
    //Just to pass it to room item
){
    val customBlue = Color(0xFF405D72);
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    val rooms by roomViewModel.rooms.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chat Rooms") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = customBlue, // Background color
                    titleContentColor = Color.White,    // Title color
                    navigationIconContentColor = Color.White, // Navigation icon color
                    actionIconContentColor = Color.White  // Action icon color
                )
            )
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(it)) {

            LazyColumn {
                items(rooms){room ->
                    RoomItem(room = room, onJoinClicked = {onJoinClicked(room)})
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Create Room", fontSize = 18.sp)
            }

            if(showDialog){
                AlertDialog(onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(onClick = {
                                if(name.isNotBlank()){
                                    showDialog = false
                                    roomViewModel.createRoom(name = name)
                                    Toast.makeText(context, "Room Created", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text(text = "Add")
                            }
                            Button(onClick = { showDialog = false }) {
                                Text(text = "Cancel")
                            }
                        }
                    },
                    title = { Text(text = "Create a New Room")},
                    text = {
                        OutlinedTextField(value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            label = { Text(text = "Name")},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp))
                    })
            }
        }
    }

}

@Composable
@Preview
fun ChatRoomListScreenPreview(){
    ChatRoomListScreen(onJoinClicked = {})
}

@Composable
fun RoomItem(
    room: Room,
    onJoinClicked: (Room) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = room.title, fontSize = 18.sp, fontWeight = FontWeight.Normal)
        OutlinedButton(
            onClick = { onJoinClicked(room) }
        ) {
            Text("Join")
        }
    }
}

@Composable
@Preview
fun RoomItemPreview(){
    RoomItem(room = Room("id.com","Name"),{})
}
