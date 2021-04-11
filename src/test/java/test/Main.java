/*
 * MIT License
 *
 * Copyright (c) 2021 kingdevnl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package test;

import com.mongodb.client.MongoClients;
import lombok.ToString;
import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.annotations.*;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        //Setup the ODM
        JMongoODM.init(MongoClients.create(), "jmongo");


//        Create a user
        User user = new User();
        user.createdAt = new Date(System.currentTimeMillis());
        user.userID = UUID.randomUUID();
        user.username = "jasper";
        user.messages.add(new Message("uwu", "works"));

        user.actions.add(Action.START);
        user.actions.add(Action.PAUSE);
        user.actions.add(Action.STOP);
        user.favBook = new Book("Harry potter", "JK, Rowling");

        user.warns.add(new Warn("First warning").save());
        user.warns.add(new Warn("Second warning").save());
        user.save();


        //Search for a user with a query
        User found = User.findOne(new Query()
                        .field("username")._equals("jasper")
                , User.class);


        System.out.println(found);



        //Close the ODM
        JMongoODM.close();

    }


    enum Action {
        STOP,
        PAUSE,
        START,

    }


    @Entity("warns")
    @ToString
    public static class Warn extends BaseEntity {
        @Column()
        public String reason;

        public Warn() {
        }

        public Warn(String reason) {
            this.reason = reason;
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

    @ToString
    public static class Book {
        @Column
        public String title;
        @Column
        public String author;

        public Book() {
        }

        public Book(String title, String author) {
            this.title = title;
            this.author = author;
        }
    }


    @Entity("users")
    @ToString
    public static class User extends BaseEntity {

        @Column
        public UUID userID;

        @Column
        public String username;

        @Column
        public Date createdAt;


        @Column
        @List(Message.class)
        public ArrayList<Message> messages = new ArrayList<>();


        @Column
        @List(Action.class)
        public ArrayList<Action> actions = new ArrayList<>();


        @Column
        @Embed
        public Book favBook;


        @Column
        @Reference(Warn.class)
        public ArrayList<Warn> warns = new ArrayList<>();

        //For internal use
        private User() {
        }


    }
}
