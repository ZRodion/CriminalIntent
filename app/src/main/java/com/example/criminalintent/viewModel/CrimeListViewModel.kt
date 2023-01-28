package com.example.criminalintent.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.CrimeRepository
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "crimeListViewModel"

class CrimeListViewModel : ViewModel() {

    //val crimes = CrimeRepository.get().getCrimes()
    private val crimeRepository = CrimeRepository.get()

    //безопасный доступ
    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())
    val crimes: StateFlow<List<Crime>>
        get() = _crimes.asStateFlow()

    init {
        viewModelScope.launch {
            Log.d("myTag", "initListViewModel")
            crimeRepository.getCrimes().collect {
                _crimes.value = it
            }
        }
    }

    suspend fun addCrime(crime: Crime){
        crimeRepository.addCrime(crime)
    }
}