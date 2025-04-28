package com.library.digitallibrary.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.create(application, useMock = true)

    private val _book = MutableLiveData<List<Book>>()
    val book: LiveData<List<Book>> get() = _book

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadBooks() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val books = apiService.mockBooks()
                _book.value = books
            } catch (e: Exception) {
                _error.value = "Fail to load: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}