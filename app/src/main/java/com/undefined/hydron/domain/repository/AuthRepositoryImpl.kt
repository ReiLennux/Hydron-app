package com.undefined.hydron.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.undefined.hydron.domain.models.RegisterUser
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.repository.interfaces.IAuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : IAuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override suspend fun registerUser(user: RegisterUser): Response<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.login.email, user.login.password).await()
            val uid = result.user?.uid ?: return Response.Error(Exception("User ID is null"))

            val data = user.user.copy(uid = uid)

            try {
                database.child("users").child(uid).setValue(data).await()
                Response.Success(true)
            } catch (dbException: Exception) {
                result.user?.delete()?.await()
                Response.Error(Exception("Fallo al guardar en la base de datos. Se ha revertido el registro.", dbException))
            }
        } catch (authException: Exception) {
            Response.Error(authException)
        }
    }

}
