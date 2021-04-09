package nl.kingdev.jmongoorm.testing;

import lombok.ToString;
import nl.kingdev.jmongoorm.annotations.Column;

@ToString
public class Author {

    @Column()
    public String name;


    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }
}
