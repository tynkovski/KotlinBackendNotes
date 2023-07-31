package com.tynkovski.data.datasources

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entities.User
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

interface UserDataSource {

    suspend fun getUserById(id: String): User?

    suspend fun getUserByLogin(login: String): User?

    suspend fun createUser(user: User): Boolean
}

class UserDataSourceImpl(
    database: MongoDatabase,
) : UserDataSource {

    private val users = database.getCollection<User>(User.TABLE_NAME)

    private suspend fun getAllUsers() =  users
        .find()
        .toList()

    override suspend fun getUserById(id: String) =
        users.find(Filters.eq("_id", id)).firstOrNull()

    override suspend fun getUserByLogin(login: String) =
        users.find(Filters.eq(User::login.name, login)).firstOrNull()

    override suspend fun createUser(user: User): Boolean {
        if (getUserByLogin(user.login) != null) return false
        return users.insertOne(user).wasAcknowledged()
    }
}