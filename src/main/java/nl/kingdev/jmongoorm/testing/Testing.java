package nl.kingdev.jmongoorm.testing;

import com.mongodb.client.MongoClients;
import nl.kingdev.jmongoorm.JMongoORM;
import nl.kingdev.jmongoorm.query.Query;

import java.util.Date;
import java.util.List;


public class Testing {


    public static void main(String[] args) {
        JMongoORM.init(MongoClients.create("mongodb://localhost:27017"), "testing");


//        TestEntity testEntity = TestEntity.findOne(new Query(), TestEntity.class);
//        System.out.println(testEntity);

//        Author author = new Author("KingdevNL");
//        Book favBook = new Book("Java is fun", author);
//       TestEntity testEntity = new TestEntity("Jasper", favBook);
//        testEntity.save();


        List<TestEntity> testEntities = TestEntity.find(new Query(), TestEntity.class);
        testEntities.forEach(testEntity -> {
            System.out.println(testEntity);

            testEntity.someDate = new Date(System.currentTimeMillis());

            testEntity.save();


        });


//        UserEntity user = UserEntity.findOne(new Query()
//                .field("username")._equals("Jasper2")
//                .field("data.salary")._greaterThen(1)
//                , UserEntity.class);
//
//
//        System.out.println(user);
//
//        user.setSalary(1337);
//        user.save();

//


        JMongoORM.close();
    }
}
