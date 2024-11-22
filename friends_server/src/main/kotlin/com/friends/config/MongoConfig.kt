package com.friends.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.friends.config"])
class MongoConfig(
    private val mongoProperties: MongoProperties,
) : AbstractMongoClientConfiguration() {
    override fun getDatabaseName(): String = mongoProperties.database

    @Bean
    override fun mongoClient(): MongoClient {
        val connectionString =
            ConnectionString(
                "mongodb://${mongoProperties.username}:${
                    String(
                        mongoProperties.password,
                    )
                }@${mongoProperties.host}:${mongoProperties.port}/${mongoProperties.database}?authSource=admin",
            )
        val mongoClientSettings =
            MongoClientSettings
                .builder()
                .applyConnectionString(connectionString)
                .build()

        return MongoClients.create(mongoClientSettings)
    }
}
