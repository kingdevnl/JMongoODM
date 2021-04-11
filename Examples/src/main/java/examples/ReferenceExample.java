package examples;

import com.mongodb.client.MongoClients;
import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Entity;
import nl.kingdev.jmongoodm.annotations.Reference;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReferenceExample {

    public static void main(String[] args) {
        JMongoODM.init(MongoClients.create(), "exampleDB");

        UserEntity user = new UserEntity("testUser");
        user.posts.add(new PostEntity("Just a simple post", "Testing, 1-2-3").save());
        user.posts.add(new PostEntity("Simple post", "Just a simple post!").save());
        user.save();

        System.out.println(((UserEntity) Objects.requireNonNull(UserEntity.findOne(new Query(), UserEntity.class))).posts);


    }

    @Entity("users3")
    public static class UserEntity extends BaseEntity {
        public String username;

        @Column
        @Reference(PostEntity.class)
        public List<PostEntity> posts = new ArrayList<>();

        private UserEntity() {
        }

        public UserEntity(String username) {
            this.username = username;
        }
    }

    @Entity("posts")
    public static class PostEntity extends BaseEntity {

        @Column
        public String title;
        @Column
        public String content;

        private PostEntity() {
        }

        public PostEntity(String title, String content) {
            this.title = title;
            this.content = content;
        }

        @Override
        public String toString() {
            return "PostEntity{" +
                    "title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
