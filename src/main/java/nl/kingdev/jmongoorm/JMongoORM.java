package nl.kingdev.jmongoorm;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import nl.kingdev.jmongoorm.entity.BaseEntity;
import org.bson.Document;

import java.util.HashMap;

public class JMongoORM {



    @Getter
    private static MongoClient mongoClient;
    @Getter
    private static MongoDatabase database;


    public static void init(MongoClient client, String dbName) {
        mongoClient = client;
        JMongoORM.database = client.getDatabase(dbName);
    }



    public static void close() {
        mongoClient.close();
    }


}
