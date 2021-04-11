#JMongoODM
Simple MongoDB ODM for Java!

For all examples check Examples/src/main/java/examples

Simple example use:

```java

import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Entity;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.query.Query;

//The entity annotation can take in the Collection name
@Entity("users")
public class UserEntity extends BaseEntity {

    //Mark the field to be mapped using @Column
    @Column
    public String username;
    @Column
    public String firstName;
    @Column
    public String lastName;

    //The ODM needs a empty constructor.
    private UserEntity() {
    }

    public UserEntity(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

public class Testing {

    public static void main(String[] args) {
        UserEntity user = UserEntity.findOne(new Query().field("username")._equals("James"), UserEntity.class);
        if (user == null) {
            user = new UserEntity("James", "James", "Potter");
            user.save();
        }

    }
}
```
