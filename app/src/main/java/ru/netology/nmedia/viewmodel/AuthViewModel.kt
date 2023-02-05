package ru.netology.nmedia.viewmodel

import android.app.Application
import android.support.v4.app.INotificationSideChannel
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.AuthRepository
import ru.netology.nmedia.repository.AuthRepositoryimpl
import ru.netology.nmedia.util.SingleLiveEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject

val token = Token(
    id = 0,
    token = "",
    avatar = "",
)
val authState = AuthState(
    id = 0,
    token = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val repository: AuthRepository
) : ViewModel() {


    val data: LiveData<AuthState> = auth.authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L


    private val _error = SingleLiveEvent<ErrorModel>()
    val error: LiveData<ErrorModel>
        get() = _error

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState


    fun loginUser(login: String, pass: String) = viewModelScope.launch {
        try {
            val token = repository.loginUser(login, pass)
            auth.setAuth(token.id, token.token)

        } catch (e: Exception) {
            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.LoginUser, e.message ?: ""))
        }
    }


    fun registerUser(login: String, name: String, pass: String) = viewModelScope.launch {
        try {
            val token = repository.registerUser(login, name, pass)
            auth.setAuth(token.id, token.token)
        } catch (e: Exception) {
            _error.postValue(
                ErrorModel(
                    ErrorType.AppError,
                    ActionType.RegisterUser,
                    e.message ?: ""
                )
            )
        }
    }

}





