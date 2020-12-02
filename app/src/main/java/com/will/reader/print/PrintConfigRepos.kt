package com.will.reader.print

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.createDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * created  by will on 2020/12/2 15:13
 */
class PrintConfigRepos private constructor(context: Context){
    private val dataStore: DataStore<Preferences>
    private val jsonAdapter = Moshi.Builder().build().adapter(PrintConfig::class.java)
    init {
        dataStore = context.createDataStore(DATA_STORE_NAME)
    }

    fun get(density: Float): Flow<PrintConfig>{
        val settingKey = preferencesKey<String>(SETTING_NAME)
        return dataStore.data.map {
            val settingStr = it[settingKey] ?: ""
            var setting = PrintConfig.default(density)
            if(settingStr.isNotEmpty()){
                try{
                    jsonAdapter.fromJson(settingStr)?.let {
                            config->
                        setting = config
                    }
                }catch (i: IOException){
                    i.printStackTrace()
                    Log.e("!~","setting Json parse error")
                }
            }
            setting
        }
    }
    suspend fun save(printConfig: PrintConfig){
        val settingKey = preferencesKey<String>(SETTING_NAME)
        dataStore.edit {
            settings ->
            val settingStr = jsonAdapter.toJson(printConfig)
            settings[settingKey] = settingStr
        }
    }

    companion object{
        private const val DATA_STORE_NAME = "printer_setting.setting"
        private const val SETTING_NAME = "printer_setting"
        private var instance: PrintConfigRepos? = null

        fun getInstance(context: Context): PrintConfigRepos{
            return instance ?: synchronized(this){
                instance ?: PrintConfigRepos(context).also { instance = it }
            }
        }
    }
}