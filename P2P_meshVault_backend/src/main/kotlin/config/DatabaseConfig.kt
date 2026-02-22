package com.ltcoe.config

import com.ltcoe.model.entity.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    fun init(dbUrl: String, dbUser: String, dbPassword: String) {

        Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )

        println("Db connected")

        transaction {
            SchemaUtils.create(
                com.ltcoe.model.entity.Users,
                com.ltcoe.model.entity.Nodes,
                com.ltcoe.model.entity.Files,
                com.ltcoe.model.entity.Credits
            )
        }
    }
}