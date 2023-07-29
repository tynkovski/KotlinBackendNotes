package com.tynkovski.di

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.datasources.UserDataSource
import com.tynkovski.data.datasources.UserDataSourceImpl
import org.koin.dsl.module

val dataSourceModule = module {
    single<UserDataSource> { UserDataSourceImpl(get<MongoDatabase>()) }
}