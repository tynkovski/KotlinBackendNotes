package com.tynkovski.di

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.datasources.user.UserDataSource
import com.tynkovski.data.datasources.user.UserDataSourceImpl
import org.koin.dsl.module

val dataSourceModule = module {
    single<UserDataSource> { UserDataSourceImpl(get<MongoDatabase>()) }
}