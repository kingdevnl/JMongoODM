package nl.kingdev.jmongoorm.testing;

import lombok.Data;
import nl.kingdev.jmongoorm.annotations.Column;
import nl.kingdev.jmongoorm.annotations.Entity;
import nl.kingdev.jmongoorm.entity.BaseEntity;

@Entity
@Data()
public class UserEntity extends BaseEntity {

    @Column
    private int userID;
    @Column
    private String username;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column("data.job")
    private String job;
    @Column("data.salary")
    private double salary;
}
