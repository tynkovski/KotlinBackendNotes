package com.tynkovski.data.datasources

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entities.User
import kotlinx.coroutines.flow.firstOrNull

interface UserDataSource {

    suspend fun getUserById(id: String): User?

    suspend fun getUserByLogin(login: String): User?

    suspend fun createUser(user: User): Boolean

    suspend fun deleteUserById(id: String): Boolean

    suspend fun changePassword(id: String, newPassword: String, newSalt: String): Boolean

    suspend fun changeName(id: String, name: String): Boolean
}

class UserDataSourceImpl(
    database: MongoDatabase,
) : UserDataSource {

    private val users = database.getCollection<User>(User.TABLE_NAME)

    override suspend fun getUserById(id: String) =
        users.find(Filters.eq("_id", id)).firstOrNull()

    override suspend fun getUserByLogin(login: String) =
        users.find(Filters.eq(User::login.name, login)).firstOrNull()

    override suspend fun createUser(user: User): Boolean {
        if (getUserByLogin(user.login) != null) return false
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun deleteUserById(id: String) =
        users.deleteOne(Filters.eq("_id", id)).wasAcknowledged()

    override suspend fun changePassword(id: String, newPassword: String, newSalt: String): Boolean {
        val filters = Filters.eq("_id", id)
        val updates = listOf(
            Updates.set(User::password.name, newPassword),
            Updates.set(User::salt.name, newSalt),
        )
        return users.updateOne(filters, updates).wasAcknowledged()
    }

    override suspend fun changeName(id: String, name: String): Boolean {
        val filters = Filters.eq("_id", id)
        val updates = listOf(Updates.set(User::name.name, name))
        return users.updateOne(filters, updates).wasAcknowledged()
    }
}