package jmongoodm.testing;

import com.mongodb.client.MongoClients;
import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.query.Query;


public class Testing {


    public static void main(String[] args) {
        JMongoODM.init(MongoClients.create("mongodb://localhost:27017"), "testing");


        TestEntity testEntity = TestEntity.findOne(new Query(), TestEntity.class);
        System.out.println(testEntity);
        testEntity.save();



        JMongoODM.close();
    }
}
