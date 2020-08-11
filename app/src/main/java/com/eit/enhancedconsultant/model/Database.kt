package com.eit.enhancedconsultant.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Database(
    var members: MutableList<MembersTask> = mutableListOf(),
    var meta: MutableList<MetaTask> = mutableListOf(),
    var tasks: MutableList<CompleteTask> = mutableListOf(),
    var users: MutableList<CompleteUser> = mutableListOf()
)

@IgnoreExtraProperties
data class MembersTask(
    var assignedBy: String,
    var assignedTo: String
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "assignedBy" to assignedBy,
            "assignedTo" to assignedTo
        )
    }
}

@IgnoreExtraProperties
data class MetaTask(
    var dueDate: Int,
    var name: String,
    var timeStamp: Int
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "dueDate" to dueDate,
            "name" to name,
            "timeStamp" to timeStamp
        )
    }
}

@IgnoreExtraProperties
data class CompleteTask(
    var accepted: Boolean,
    var assignedBy: String,
    var assignedTo: String,
    var completed: Boolean = false,
    var declined: Boolean,
    var dueDate: Int, // <-- Transform dueDate to Int here--------------------------------------------------------
    var name: String,
    var timeStamp: Int
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "accepted" to accepted,
            "assignedBy" to assignedBy,
            "assignedTo" to assignedTo,
            "completed" to completed,
            "declined" to declined,
            "dueDate" to dueDate,
            "name" to name,
            "timeStamp" to timeStamp
        )
    }
}

@IgnoreExtraProperties
data class CompleteUser(
    var tasks: MutableList<CompleteTask> = mutableListOf(), // <-- I want a listener to find out when there is a new task
    var user: User
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "tasks" to tasks,
            "user" to user
        )
    }
}

@IgnoreExtraProperties
data class User(
    var contactNumber: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var level: Int = 1,
    var loggedIn: Boolean = false,
    var password: String,
    var userName: String
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "contactNumber" to contactNumber,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "level" to level,
            "loggedIn" to loggedIn,
            "password" to password,
            "userName" to userName
        )
    }
}

