package nl.kingdev.jmongoorm.testing;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nl.kingdev.jmongoorm.annotations.Column;
import nl.kingdev.jmongoorm.annotations.Embed;
import nl.kingdev.jmongoorm.annotations.Entity;
import nl.kingdev.jmongoorm.entity.BaseEntity;
import nl.kingdev.jmongoorm.query.Query;

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
    public List<String> messages = new ArrayList<>();

}
