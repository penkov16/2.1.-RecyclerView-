package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.dto.User

interface  AuthRepository {

    suspend fun  loginUser(login: String, pass: String):Token

    suspend fun registerUser(login: String , name: String,pass: String):Token

}