package com.example.chatroomapp.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    roomTitle: String,
    roomId: String,
    messageViewModel: MessageViewModel = viewModel()
){
    val customBlue = Color(0xFF405D72);
    val customDarkGray = Color(0xFF414348);
    val customGreen = Color(0xFF5BD27D);


    val context = LocalContext.current as ComponentActivity
    val messages by messageViewModel.messages.observeAsState(emptyList())
    messageViewModel.setRoomId(roomId)

    val currentUser by messageViewModel.currentUser.observeAsState()
    val currentUserEmail = currentUser?.email
    val text = remember{ mutableStateOf("") }

    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val BoxColor = if (isSystemInDarkTheme()) customDarkGray else Color.White

    if (currentUser != null) {
        Log.e("chatscreen","$currentUser" + " hi")
        //initial loading of messages
        messageViewModel.loadMessages()
       Scaffold(
           topBar = {
               TopAppBar(
                   title = { Text(text = roomTitle) },
                   navigationIcon = {
                       IconButton(onClick = { context.onBackPressedDispatcher.onBackPressed() }) {
                           Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                       }
                   },
                   colors = TopAppBarDefaults.topAppBarColors(
                       containerColor = customBlue, // Background color
                       titleContentColor = Color.White,    // Title color
                       navigationIconContentColor = Color.White, // Navigation icon color
                       actionIconContentColor = Color.White  // Action icon color
                   )
               )
           }
       ) {
           Box(modifier = Modifier.fillMaxSize()) {
               Image(
                   painter = painterResource( id =
                       if (isSystemInDarkTheme()) R.drawable.darkmode else R.drawable.image),
                   contentDescription = null,
                   modifier = Modifier
                       .fillMaxSize()
                       .alpha(0.7f),
                   contentScale = ContentScale.Crop
               )

               Column(modifier = Modifier
                   .fillMaxSize()
                   .padding(horizontal = 16.dp)
                   .padding(it)
                   //.paint(painter = painterResource(id = R.drawable.image),)
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
                       .fillMaxWidth()
                       .padding(bottom = 10.dp),
                       verticalAlignment = Alignment.CenterVertically
                   ) {
                       Box(modifier = Modifier
                           .border(2.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
                           .weight(1f)
                           .height(50.dp)
                           .background(BoxColor)) {
                           BasicTextField(
                               value = text.value,
                               onValueChange = { text.value = it },
                               textStyle = TextStyle.Default.copy(fontSize = 20.sp, color = textColor),
                               modifier = Modifier
                                   .wrapContentHeight()
                                   .padding(horizontal = 5.dp, vertical = 6.dp)
                                   .align(Alignment.CenterStart)
                                   .padding(horizontal = 6.dp),
                               cursorBrush = if (isSystemInDarkTheme()) SolidColor(Color.White) else SolidColor(Color.Black)
                           )
                       }
                       Spacer(modifier = Modifier.width(6.dp))
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
                           Box(
                               contentAlignment = Alignment.Center,
                               modifier = Modifier
                                   .size(53.dp) // Size of the circle
                                   .background(color = customGreen, shape = CircleShape) // Green circle background
                           ) {
                               Icon(
                                   imageVector = Icons.Default.Send,
                                   contentDescription = "Send",
                                   modifier = Modifier.size(20.dp),
                                   tint = Color.Black // Optional: White icon color for better contrast
                               )
                           }
                       }
                   }
               }
           }
        }

    }
    else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CustomCircularLoadingIndicator()
        }
    }

}

@Preview
@Composable
fun ChatScreenPreview(){
    ChatScreen(roomId = "1", roomTitle = "Title")
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
        val customBlue = Color(0xC0405D72);
        val customGray = Color(0xF3E2F4FB);
        //val customGray = Color(0xFFdcfce7);

        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) customGray else customBlue,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isSentByCurrentUser) Color.Black else Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.senderFirstName,
            style = TextStyle(
                fontSize = 12.sp,
                color = if (isSystemInDarkTheme()) Color.White else Color.DarkGray,
            )
        )
        Text(
            text = formatTimeStamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = if (isSystemInDarkTheme()) Color.White else Color.DarkGray
            )
        )
    }
}

//////////////////////////////////////////////////////////////////////////////////

@Composable
fun CustomCircularLoadingIndicator(
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = Color.Black,
    durationMillis: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedRotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing)
        ), label = ""
    )

    Canvas(
        modifier = Modifier
            .size(size)
            .rotate(animatedRotation.value)
    ) {
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 270f, // 270 degrees to create the circular effect
            useCenter = false,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}