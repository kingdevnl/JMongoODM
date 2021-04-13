package nl.kingdev.jmongoodm.mapping;

import com.mongodb.client.MongoClients;
import lombok.ToString;
import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Embed;
import nl.kingdev.jmongoodm.annotations.Entity;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Test {

    public static void main(String[] args) {
        JMongoODM.init(MongoClients.create(), "jmongo");
        JMongoODM.getDatabase().getCollection("users").drop();


        User user = new User("James", 1337);

        user.testEmbed = new TestEmbed(1337, "Hello World!");
        user.messages.add(new Message("Hello", "Hello World!"));
        user.messages.add(new Message("Test", "Test works!"));

        user.uuid = UUID.randomUUID();

        user.accountState = State.ACTIVATED;
        user.createdAt = new Date(System.currentTimeMillis());
        user.save();


        System.out.println(User.findOne(new Query(), User.class));
        System.out.println(((User) User.findOne(new Query(), User.class)).objectID);


    }


    public enum State {
        ACTIVATED,
        DE_ACTIVATED
    }

    @ToString
    public static class TestEmbed {
        @Column
        public int likes;
        @Column
        public String message;


        public TestEmbed() {
        }

        public TestEmbed(int likes, String message) {
            this.likes = likes;
            this.message = message;
        }
    }

    @ToString
    public static class Message {
        @Column
        public String title;
        @Column
        public String content;

        public Message() {
        }

        public Message(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    @Entity("users")
    @ToString
    public static class User extends BaseEntity {
        @Column
        public String firstName;
        @Column
        public int age;


        @Column
        @Embed
        public TestEmbed testEmbed;

        @Column
        @nl.kingdev.jmongoodm.annotations.List(Message.class)
        public List<Message> messages = new ArrayList<>();


        @Column
        public UUID uuid;


        @Column
        public State accountState;

        @Column
        public Date createdAt;

        public User() {
        }

        public User(String firstName, int age) {
            this.firstName = firstName;
            this.age = age;
        }
    }
}
