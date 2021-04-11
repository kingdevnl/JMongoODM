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

package nl.kingdev.jmongoodm.test.tests;

import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.query.Query;
import nl.kingdev.jmongoodm.test.BaseTest;
import nl.kingdev.jmongoodm.test.entities.PostEntity;
import nl.kingdev.jmongoodm.test.entities.UserEntity;
import nl.kingdev.jmongoodm.test.entities.objects.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserReferenceTest extends BaseTest {


    @BeforeEach
    void delete() {
        JMongoODM.getDatabase().getCollection("users").drop();
        UserEntity userEntity = new UserEntity("James", 21);

        userEntity.posts.add(new PostEntity("Post #1", "Hello World!").save());
        userEntity.posts.add(new PostEntity("Post #2", "Just a simple post").save());

        userEntity.save();
    }


    @Test
    void test() {
        UserEntity user = UserEntity.findOne(new Query()
                        .field("username")._equals("James")
                        .field("age")._equals(21)
                , UserEntity.class);


        assertNotNull(user);
        assertNotNull(user.posts);

        assertEquals(2, user.posts.size());

        assertEquals("Post #1", user.posts.get(0).title);
        assertEquals("Hello World!", user.posts.get(0).content);

        assertEquals("Post #2", user.posts.get(1).title);
        assertEquals("Just a simple post", user.posts.get(1).content);


    }

}
