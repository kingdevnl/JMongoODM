package examples;

import com.mongodb.client.MongoClients;
import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Embed;
import nl.kingdev.jmongoodm.annotations.Entity;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmbedExample {

    public static void main(String[] args) {
        JMongoODM.init(MongoClients.create(), "exampleDB");
        UserEntity user = UserEntity.findOne(new Query().field("username")._equals("James"), UserEntity.class);
        if (user == null) {
            user = new UserEntity("James", "James", "Potter");
            user.profile = new Profile(Arrays.asList("Mark", "Bob"), 1337);
            user.save();
        }

    }

    //Define the POJO
    public static class Profile {

        @Column
        @nl.kingdev.jmongoodm.annotations.List(String.class)
        public List<String> friends = new ArrayList<>();
        @Column
        public int likes;

        private Profile() {
        }

        public Profile(List<String> friends, int likes) {
            this.friends = friends;
            this.likes = likes;
        }
    }

    //The entity annotation can take in the Collection name
    @Entity("users2")
    public static class UserEntity extends BaseEntity {

        //Mark the field to be mapped using @Column
        @Column
        public String username;
        @Column
        public String firstName;
        @Column
        public String lastName;

        @Column
        @Embed
        public Profile profile;

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
