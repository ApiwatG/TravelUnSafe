package com.example.travelunsafe



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _deletedUsers = MutableStateFlow<List<User>>(emptyList())
    val deletedUsers: StateFlow<List<User>> = _deletedUsers

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init { getAllUsers() }


    fun getAllUsers() {
        viewModelScope.launch {
            try {
                val res = UserClient.instance.getAllUsers()
                if (res.isSuccessful) _users.value = res.body() ?: emptyList()
            } catch (e: Exception) { _message.value = e.message }
        }
    }

    fun hardDeleteUser(userId: String) {
        viewModelScope.launch {
            try {
                val res = UserClient.instance.hardDeleteUser(userId)
                if (res.isSuccessful) {
                    _message.value = "ลบผู้ใช้ถาวรสำเร็จ"
                    getDeletedUsers()
                }
            } catch (e: Exception) { _message.value = e.message }
        }
    }


    fun getDeletedUsers() {
        viewModelScope.launch {
            try {
                val res = UserClient.instance.getDeletedUsers()
                if (res.isSuccessful) _deletedUsers.value = res.body() ?: emptyList()
            } catch (e: Exception) { _message.value = e.message }
        }
    }

    fun banUser(userId: String) {
        viewModelScope.launch {
            try {
                val res = UserClient.instance.banUser(userId)
                if (res.isSuccessful) { _message.value = "แบนผู้ใช้สำเร็จ"; getAllUsers() }
            } catch (e: Exception) { _message.value = e.message }
        }
    }

    fun unbanUser(userId: String) {
        viewModelScope.launch {
            try {
                val res = UserClient.instance.unbanUser(userId)
                if (res.isSuccessful) { _message.value = "ปลดแบนสำเร็จ"; getAllUsers() }
            } catch (e: Exception) { _message.value = e.message }
        }
    }

    fun softDeleteUser(userId: String) {
        viewModelScope.launch {
            try {
                val res = UserClient.instance.softDeleteUser(userId)
                if (res.isSuccessful) { _message.value = "ลบผู้ใช้สำเร็จ"; getAllUsers() }
            } catch (e: Exception) { _message.value = e.message }
        }
    }

    fun restoreUser(userId: String) {
        viewModelScope.launch {
            try {
                val res = UserClient.instance.restoreUser(userId)
                if (res.isSuccessful) { _message.value = "กู้คืนผู้ใช้สำเร็จ"; getDeletedUsers() }
            } catch (e: Exception) { _message.value = e.message }
        }
    }

    fun clearMessage() { _message.value = null }
}