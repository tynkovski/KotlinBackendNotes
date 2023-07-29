package com.tynkovski.data.datasources.user

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.models.entities.User
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

interface UserDataSource {
    suspend fun getAllUsers(): Set<User>

    suspend fun getUserByLogin(login: String): User?

    suspend fun createUserAndGetId(user: User): Boolean
}

class UserDataSourceImpl(
    database: MongoDatabase,
) : UserDataSource {

    private val users = database.getCollection<User>(User.collectionName())

    override suspend fun getAllUsers(): Set<User> {
        return users
            .find()
            .toList()
            .toSet()
    }

    override suspend fun getUserByLogin(login: String): User? {
        return users.find(Filters.eq(User::login.name, login)).firstOrNull()
    }

    override suspend fun createUserAndGetId(user: User): Boolean {
        if (getUserByLogin(user.login) != null) return false

        return users.insertOne(user).wasAcknowledged()
    }
}