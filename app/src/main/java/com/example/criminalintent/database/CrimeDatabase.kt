package com.example.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent.model.Crime

@Database(entities = [Crime::class], version = 3)
@TypeConverters(CrimeTypeConvertors::class)
abstract class CrimeDatabase: RoomDatabase(){
    abstract fun crimeDao(): CrimeDao
}

val migrate_1_2 = object : Migration(1, 2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}

val migrate_2_3 = object : Migration(2, 3){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN photoFileName TEXT"
        )
    }
}