package com.undefined.hydron.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.TrackingPayload
import com.undefined.hydron.domain.models.toFirebaseMap
import com.undefined.hydron.domain.repository.interfaces.IBindDataRepository
import kotlinx.coroutines.tasks.await

class BindDataRepositoryImpl : IBindDataRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override suspend fun bindData(payload: TrackingPayload): Response<Boolean> {
        return try {
            val uid = auth.currentUser?.uid ?: return Response.Error(Exception("Usuario no autenticado"))

            val userRef = database.child("realtime_tracking").child(uid)

            val dataMap = payload.toFirebaseMap()

            userRef.updateChildren(dataMap).await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}
