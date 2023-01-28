package com.example.criminalintent

import android.app.Application
import kotlin.reflect.KParameter

class CriminalIntentApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}