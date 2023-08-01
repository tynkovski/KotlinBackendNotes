package com.tynkovski.data.mappers

import com.tynkovski.data.entities.User
import com.tynkovski.data.responses.UserResponse

fun userMapper(user: User): UserResponse {
    return UserResponse(user.id, user.login)
}