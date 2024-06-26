package com.example.chatroomapp.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatroomapp.R
import com.example.chatroomapp.data.Message
import com.example.chatroomapp.viewmodel.MessageViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun ChatScreen(
    roomId: String,
    messageViewModel: MessageViewModel = viewModel()
){
    val messages by messageViewModel.messages.observeAsState(emptyList())
    messageViewModel.setRoomId(roomId)

    val currentUser by messageViewModel.currentUser.observeAsState()
    val currentUserEmail = currentUser?.email
    val text = remember{ mutableStateOf("") }

    if (currentUser != null) {
        Log.e("chatscreen","$currentUser" + " hi")
        //initial loading of messages
        messageViewModel.loadMessages()

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            // Display the chat messages
            LazyColumn(modifier = Modifier.weight(1f)){
                items(messages){ message ->
                    Log.d("chatscreen","${message.senderId} -- ${messageViewModel.currentUser.value?.email}")
                    ChatMessageItem(message = message.copy(
                        isSentByCurrentUser = (message.senderId == currentUserEmail)
                    ))
                }
            }

            // Chat input field and send icon
            Row(modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .border(2.dp, Color.Gray)
                    .weight(1f)
                    .padding(5.dp)) {
                    BasicTextField(
                        value = text.value,
                        onValueChange = { text.value = it },
                        textStyle = TextStyle.Default.copy(fontSize = 20.sp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                IconButton(
                    onClick = {
                        // Send the message when the icon is clicked
                        if (text.value.isNotEmpty()) {
                            messageViewModel.sendMessage(text = text.value.trim())
                            text.value = ""
                        }
                        messageViewModel.loadMessages()
                    }
                ){
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }
        }

    } else {
        Column(verticalArrangement = Arrangement.Center) {
            Text("Loading user data...")
        }
    }

}

@Preview
@Composable
fun ChatScreenPreview(){
    //ChatScreen(roomId = "1")
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun formatTimeStamp(timeStamp: Long) : String{
    val messageDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
        ZoneId.systemDefault())
    //ofEpochMilli obtains an instance of Instant using milliseconds from the epoch of 1970-01-01T00:00:00Z
    val now = LocalDateTime.now()

    return when{
        //same day message
        isSameDay(messageDateTime, now) -> "today ${formatTime(messageDateTime)}"
        //yesterday message
        isSameDay(messageDateTime.plusDays(1),now) -> "Yesterday ${formatTime(messageDateTime)}"
        //some other day message
        else -> formatDate(messageDateTime)
    }
}

fun isSameDay(dateTime1: LocalDateTime, dateTime2: LocalDateTime) : Boolean{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime1.format(formatter) == dateTime2.format(formatter)
}

fun formatTime(dateTime: LocalDateTime): String{
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(dateTime)
}

fun formatDate(dateTime: LocalDateTime): String{
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return formatter.format(dateTime)
}

///////////////////////////////////////////////////////////////////////////////////////////////

@Composable
fun ChatMessageItem(message: Message){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
        horizontalAlignment = if(message.isSentByCurrentUser) Alignment.End
                                else Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) colorResource(id = R.color.purple_700) else Color.DarkGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.senderFirstName,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
        Text(
            text = formatTimeStamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}