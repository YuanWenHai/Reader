package com.will.reader.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.will.reader.data.model.Book
import com.will.reader.data.model.Chapter

/**
 * created  by will on 2020/11/22 11:20
 */
@Database(entities = [Book::class, Chapter::class],version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract fun getBookDao(): BookDao
    abstract fun getChapterDao(): ChapterDao


    companion object{
        @Volatile private var instance: AppDataBase? = null
        private const val DATABASE_NAME = "reader_database"
        fun getInstance(context: Context): AppDataBase{
            return instance ?: synchronized(this){
                instance ?: Room.databaseBuilder(context,AppDataBase::class.java, DATABASE_NAME)
                    .build().also { instance = it }
            }
        }
    }
}