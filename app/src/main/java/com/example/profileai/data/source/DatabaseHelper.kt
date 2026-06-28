package com.example.profileai.data.source

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cursosant.profileaiant.Constants
import com.example.profileai.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration.Companion.milliseconds


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, Constants.DATABASE_NAME, null,
    Constants.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE ${Constants.ENTITY_USER} (" +
                "${Constants.P_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${Constants.P_NAME} VARCHAR," +
                "${Constants.P_EMAIL} VARCHAR," +
                "${Constants.P_WEBSITE} VARCHAR," +
                "${Constants.P_PHONE} VARCHAR," +
                "${Constants.P_IMAGE} VARCHAR," +
                "${Constants.P_LATITUDE} VARCHAR," +
                "${Constants.P_LONGITUDE} VARCHAR)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun saveOrUpdateUser(user: User): Boolean {
        return if (user.id > 0) {
            updateNote(user)
        } else {
            insertUser(user) > 0
        }
    }

    //CREATE
    fun insertUser(user: User): Long {
        val database = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(Constants.P_NAME, user.name)
            put(Constants.P_EMAIL, user.email)
            put(Constants.P_WEBSITE, user.website)
            put(Constants.P_PHONE, user.phone)
            put(Constants.P_IMAGE, user.image)
            put(Constants.P_LATITUDE, user.latitude)
            put(Constants.P_LONGITUDE, user.longitude)
        }
        val resultId = database.insert(Constants.ENTITY_USER, null, contentValues)
        return resultId
    }

    fun getUser(): Flow<User> = flow {
        while (true) {
            val user = getFirstUser()
            emit(user)
            delay(3_000.milliseconds)
        }
    }.flowOn(Dispatchers.IO)

    //READ
    fun getFirstUser(): User {
        val user = User()
        val database = this.readableDatabase
        val query = "SELECT * FROM ${Constants.ENTITY_USER} LIMIT 1"
        val result = database.rawQuery(query, null)

        if (result.moveToFirst()){
            val id = result.getColumnIndex(Constants.P_ID)
            val name = result.getColumnIndex(Constants.P_NAME)
            val email = result.getColumnIndex(Constants.P_EMAIL)
            val website = result.getColumnIndex(Constants.P_WEBSITE)
            val phone = result.getColumnIndex(Constants.P_PHONE)
            val image = result.getColumnIndex(Constants.P_IMAGE)
            val latitude = result.getColumnIndex(Constants.P_LATITUDE)
            val longitude = result.getColumnIndex(Constants.P_LONGITUDE)

            user.id = result.getLong(if (id >= 0) id else 0)
            user.name = result.getString(if (name >= 0) name else 0)
            user.email = result.getString(if (email >= 0) email else 0)
            user.website = result.getString(if (website >= 0) website else 0)
            user.phone = result.getString(if (phone >= 0) phone else 0)
            user.image = result.getString(if (image >= 0) image else 0)
            user.latitude = result.getString(if (latitude >= 0) latitude else 0)
            user.longitude = result.getString(if (longitude >= 0) longitude else 0)
        }
        result.close()
        return user
    }

    //UPDATE
    fun updateNote(user: User): Boolean {
        val database = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(Constants.P_NAME, user.name)
            put(Constants.P_EMAIL, user.email)
            put(Constants.P_WEBSITE, user.website)
            put(Constants.P_PHONE, user.phone)
            put(Constants.P_IMAGE, user.image)
            put(Constants.P_LATITUDE, user.latitude)
            put(Constants.P_LONGITUDE, user.longitude)
        }

        val clause = "${Constants.P_ID} = ${user.id}"
        val result = database.update(
            Constants.ENTITY_USER,
            contentValues, clause, null)
        return result == Constants.TRUE
    }

    //DELETE
    fun deleteAllUsers(): Boolean {
        val database = this.writableDatabase
        val result = database.delete(
            Constants.ENTITY_USER,
            null, null)
        return result == Constants.TRUE
    }
}