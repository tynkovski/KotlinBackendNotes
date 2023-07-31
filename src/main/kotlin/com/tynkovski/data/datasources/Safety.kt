package com.tynkovski.data.datasources

import com.mongodb.client.model.Updates
import org.bson.conversions.Bson

inline fun <reified T> safetySet(fieldName: String, value: T?): Bson {
    return if (value == null) Updates.unset(fieldName) else Updates.set(fieldName, value)
}