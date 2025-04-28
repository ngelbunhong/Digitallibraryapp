package com.library.digitallibrary.data.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class GenericViewModelFactory(
    private val creators: Map<Class<out ViewModel>, () -> ViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")

        return try {
            creator() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}