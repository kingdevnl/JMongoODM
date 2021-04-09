package nl.kingdev.jmongoodm;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

public class JMongoODM {


    @Getter
    private static MongoClient mongoClient;
    @Getter
    private static MongoDatabase database;


    public static void init(MongoClient client, String dbName) {
        mongoClient = client;
        JMongoODM.database = client.getDatabase(dbName);
    }


    public static void close() {
        mongoClient.close();
    }


}
