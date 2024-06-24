package com.example.chatroomapp.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoomRepository(
    private val firestore: FirebaseFirestore
) {
    suspend fun createRoom(name: String) : Result<Unit> = try {
        val room = Room(title = name)
        //storing into firebase inside rooms collection
        val doc = firestore.collection("rooms").add(room).await()
        doc.update("id",doc.id).await()
        Result.Success(Unit)
    } catch (e: Exception){
        Result.Error(e)
    }

    suspend fun getRooms(): Result<List<Room>> = try {
        val querySnapshot = firestore.collection("rooms").get().await()
        val rooms = querySnapshot.documents.map { document ->
            document.toObject(Room::class.java)!!.copy(id = document.id)
        }
        //the copy() function creates a copy of the Room object obtained from toObject().
        //However, it overrides the id property of the copy with the actual document ID from Firestore (document.id).
        Result.Success(rooms)
    } catch (e: Exception){
        Result.Error(e)
    }
}