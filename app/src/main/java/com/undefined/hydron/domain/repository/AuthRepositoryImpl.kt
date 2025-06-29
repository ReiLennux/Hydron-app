package com.undefined.hydron.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.undefined.hydron.domain.models.LoginModel
import com.undefined.hydron.domain.models.RegisterUser
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.UserModel
import com.undefined.hydron.domain.repository.interfaces.IAuthRepository
import com.undefined.hydron.domain.useCases.auth.LoginUser
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : IAuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override suspend fun registerUser(user: RegisterUser): Response<UserModel> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.login.email, user.login.password).await()
            val uid = result.user?.uid ?: return Response.Error(Exception("User ID is null"))

            val data = user.user.copy(uid = uid)

            try {
                database.child("users").child(uid).setValue(data).await()
                Response.Success(data)
            } catch (dbException: Exception) {
                result.user?.delete()?.await()
                Response.Error(Exception("Fallo al guardar en la base de datos. Se ha revertido el registro.", dbException))
            }
        } catch (authException: Exception) {
            Response.Error(authException)
        }
    }

    override suspend fun getUser(userId: String): Response<UserModel> {
        return try {
            val snapshot = database.child("users").child(userId).get().await()
            if (snapshot.exists()) {
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    Response.Success(user)
                } else {
                    Response.Error(Exception("User data is null"))
                }
            } else {
                    Response.Error(Exception("User not found"))
                }
        } catch (e: Exception) {
            Response.Error(e)
        }
    }


    override suspend fun loginUser(loginModel: LoginModel): Response<UserModel> {
        return try {
            val result = auth.signInWithEmailAndPassword(loginModel.email, loginModel.password).await()
            val uid = result.user?.uid ?: return Response.Error(Exception("User ID is null"))
            getUser(uid)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }



}
