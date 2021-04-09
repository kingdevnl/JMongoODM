package jmongoodm.testing;


import lombok.ToString;
import nl.kingdev.jmongoodm.annotations.Column;

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
