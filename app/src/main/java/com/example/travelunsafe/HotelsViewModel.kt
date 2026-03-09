package com.example.travelunsafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HotelsViewModel : ViewModel() {

    // ---- Login State ----
    private val _loginResult = MutableStateFlow<LoginClass?>(null)
    val loginResult: StateFlow<LoginClass?> = _loginResult

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    // ---- Register State ----
    private val _registerResult = MutableStateFlow<RegisterResponse?>(null)
    val registerResult: StateFlow<RegisterResponse?> = _registerResult

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError

    // ---- Profile State ----
    private val _profile = MutableStateFlow<ProfileClass?>(null)
    val profile: StateFlow<ProfileClass?> = _profile

    private val _profileError = MutableStateFlow<String?>(null)
    val profileError: StateFlow<String?> = _profileError

    // ---- Loading ----
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ---- Login (email + password) ----
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            try {
                val response = HotelsClient.api.login(
                    mapOf("email" to email, "password" to password)
                )
                if (response.isSuccessful && response.body()?.error == false) {
                    _loginResult.value = response.body()
                } else {
                    _loginError.value = response.body()?.message ?: "อีเมลหรือรหัสผ่านไม่ถูกต้อง"
                }
            } catch (e: Exception) {
                _loginError.value = "ไม่สามารถเชื่อมต่อ Server ได้"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---- Register ----
    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _registerError.value = null
            try {
                val response = HotelsClient.api.register(
                    RegisterClass(username = username, email = email, password = password)
                )
                if (response.isSuccessful && response.body()?.error == false) {
                    _registerResult.value = response.body()
                } else {
                    _registerError.value = response.body()?.message ?: "ลงทะเบียนไม่สำเร็จ"
                }
            } catch (e: Exception) {
                _registerError.value = "ไม่สามารถเชื่อมต่อ Server ได้"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---- Get Profile ----
    fun getProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _profileError.value = null
            try {
                val response = HotelsClient.api.getProfile(userId)
                if (response.isSuccessful) {
                    _profile.value = response.body()
                } else {
                    _profileError.value = "ไม่พบข้อมูลผู้ใช้"
                }
            } catch (e: Exception) {
                _profileError.value = "ไม่สามารถเชื่อมต่อ Server ได้"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---- Reset ----
    fun resetLoginResult() {
        _loginResult.value = null
        _loginError.value = null
    }

    fun resetRegisterResult() {
        _registerResult.value = null
        _registerError.value = null
    }
}