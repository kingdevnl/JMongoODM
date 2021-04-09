package nl.kingdev.jmongoorm.testing;

import com.mongodb.client.MongoClients;
import nl.kingdev.jmongoorm.JMongoORM;
import nl.kingdev.jmongoorm.query.Query;


public class Testing {


    public static void main(String[] args) {
        JMongoORM.init(MongoClients.create("mongodb://localhost:27017"), "testing");


        TestEntity testEntity = TestEntity.findOne(new Query(), TestEntity.class);
        System.out.println(testEntity);
        testEntity.save();



        JMongoORM.close();
    }
}
