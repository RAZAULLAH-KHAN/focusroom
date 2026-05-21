package com.focusroom.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.focusroom.app.data.model.RoomEntity
import com.focusroom.app.data.model.UserEntity

@Database(entities = [UserEntity::class, RoomEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun roomDao(): RoomDao
}
