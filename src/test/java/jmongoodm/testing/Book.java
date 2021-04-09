package jmongoodm.testing;

import lombok.ToString;
import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Embed;

@ToString
public class Book {

    @Column()
    public String name;

    @Embed()
    public Author author;

    public Book() {
    }

    public Book(String name, Author author) {
        this.name = name;
        this.author = author;
    }
}
