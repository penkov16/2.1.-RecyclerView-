package ru.netology.nmedia.repository

import okio.IOException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryimpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
) : AuthRepository {


    override suspend fun loginUser(login: String, pass: String): Token {
        try {
            val response = apiService.loginUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUser(login: String, name: String, pass: String): Token {
        try {

            val response = apiService.registerUser(login, name, pass)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}

