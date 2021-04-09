package nl.kingdev.jmongoorm.testing;

import lombok.ToString;
import nl.kingdev.jmongoorm.annotations.Column;
import nl.kingdev.jmongoorm.annotations.Embed;

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
