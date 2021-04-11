package examples;

import com.mongodb.client.MongoClients;
import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Entity;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.query.Query;

public class SimpleExample {

    public static void main(String[] args) {
        JMongoODM.init(MongoClients.create(), "exampleDB");
        UserEntity user = UserEntity.findOne(new Query().field("username")._equals("James"), UserEntity.class);
        if (user == null) {
            user = new UserEntity("James", "James", "Potter");
            user.save();
            System.out.println(user);
        }

    }

    //The entity annotation can take in the Collection name
    @Entity("users")
    public static class UserEntity extends BaseEntity {

        //Mark the field to be mapped using @Column
        @Column
        public String username;
        @Column
        public String firstName;
        @Column
        public String lastName;

        //The ODM needs a empty constructor.
        private UserEntity() {
        }

        public UserEntity(String username, String firstName, String lastName) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }


}
