package com.example.criminalintent

import android.content.Context
import androidx.room.Room
import com.example.criminalintent.database.CrimeDatabase
import com.example.criminalintent.database.migrate_1_2
import com.example.criminalintent.database.migrate_2_3
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    //конкретная реализации базы данных
    private val database: CrimeDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
        )
        .addMigrations(migrate_1_2, migrate_2_3)
        .build()

    suspend fun getCrime(id: UUID): Crime = database.crimeDao().getCrime(id)
    fun getCrimes() = database.crimeDao().getCrimes()



    fun updateCrime(crime: Crime) {
        coroutineScope.launch {
            database.crimeDao().updateCrime(crime)
        }
    }

    suspend fun addCrime(crime: Crime) {
        database.crimeDao().addCrime(crime)
    }
    suspend fun deleteCrime(crime: Crime) {
        //delay(1000)
        database.crimeDao().deleteCrime(crime)

    }

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}