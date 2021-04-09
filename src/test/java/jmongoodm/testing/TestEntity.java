package jmongoodm.testing;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Embed;
import nl.kingdev.jmongoodm.annotations.Entity;
import nl.kingdev.jmongoodm.entity.BaseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("test")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity extends BaseEntity {

    @Column()
    public String username;
    @Embed()
    public Book favBook;
    @Column()
    public Date someDate;

    @Column()
    @nl.kingdev.jmongoodm.annotations.List(Message.class)
    public List<Message> messages = new ArrayList<>();

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ToString
    public static class Message {
        @Column()
        public String title;
        @Column("content")
        public String body;

    }
}
