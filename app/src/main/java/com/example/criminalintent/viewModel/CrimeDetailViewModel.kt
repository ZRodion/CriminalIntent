package com.example.criminalintent.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.CrimeRepository
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CrimeDetailViewModel(crimeId: UUID) : ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    private val _crime: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val crime: StateFlow<Crime?>
        get() = _crime.asStateFlow()

    init {
        viewModelScope.launch {
            _crime.value = crimeRepository.getCrime(crimeId)
        }
    }

    fun updateCrime(onUpdate: (Crime) -> Crime) {
        _crime.update { oldCrime ->
            oldCrime?.let { onUpdate(it) }
        }
    }

    suspend fun deleteCrime() {
        crime.value?.let { crimeRepository.deleteCrime(it) }
    }

    //used to save to DB
    override fun onCleared() {
        super.onCleared()
        Log.d("myTag", "onCleared Detail View Model")
        crime.value?.let { crimeRepository.updateCrime(it) }
    }
}

class CrimeDetailViewModelFactory(
    private val crimeId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrimeDetailViewModel(crimeId) as T
    }
}