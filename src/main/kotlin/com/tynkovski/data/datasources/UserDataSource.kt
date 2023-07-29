package com.tynkovski.data.datasources

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entities.User
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

interface UserDataSource {
    suspend fun getAllUsers(): Set<User>

    suspend fun getUserById(id: String): User?

    suspend fun getUserByLogin(login: String): User?

    suspend fun createUserAndGetId(user: User): Boolean
}

class UserDataSourceImpl(
    database: MongoDatabase,
) : UserDataSource {

    private val users = database.getCollection<User>(User.TABLE_NAME)

    override suspend fun getAllUsers(): Set<User> {
        return users
            .find()
            .toList()
            .toSet()
    }

    override suspend fun getUserById(id: String): User? {
        return users.find(Filters.eq("_id", id)).firstOrNull()
    }

    override suspend fun getUserByLogin(login: String): User? {
        return users.find(Filters.eq(User::login.name, login)).firstOrNull()
    }

    override suspend fun createUserAndGetId(user: User): Boolean {
        if (getUserByLogin(user.login) != null) return false

        return users.insertOne(user).wasAcknowledged()
    }
}